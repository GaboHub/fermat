package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_api.layer.osa_android.location_system.Location;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.NearNodeListMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.NearNodeListMsgRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.NodeProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.DistanceCalculator;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.HeadersAttName;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.endpoinsts.FermatWebSocketChannelEndpoint;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.PackageProcessor;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.context.NodeContext;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.context.NodeContextItem;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.daos.JPADaoFactory;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities.NodeCatalog;


import org.apache.commons.lang.ClassUtils;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.websocket.Session;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients.NearNodeListRequestProcessor</code>
 * process all packages received the type <code>MessageType.NEAR_NODE_LIST_REQUEST</code><p/>
 *
 * Created by Roberto Requena - (rart3001@gmail.com) on 26/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class NearNodeListRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(NearNodeListRequestProcessor.class));

    /**
     * Represents the JPADaoFactory.
     */
    private JPADaoFactory jpaDaoFactory;

    /**
     * Constructor
     */
    public NearNodeListRequestProcessor() {
        super(PackageType.NEAR_NODE_LIST_REQUEST);
    }

    /**
     * (non-javadoc)
     * @see PackageProcessor#processingPackage(Session, Package, FermatWebSocketChannelEndpoint)
     */
    @Override
    public void processingPackage(Session session, Package packageReceived, FermatWebSocketChannelEndpoint channel) {

        LOG.info("Processing new package received: "+ packageReceived.getPackageType());

        String destinationIdentityPublicKey = (String) session.getUserProperties().get(HeadersAttName.CPKI_ATT_HEADER_NAME);

        try {

            NearNodeListMsgRequest messageContent = NearNodeListMsgRequest.parseContent(packageReceived.getContent());

            /*
             * Create the method call history
             */
            methodCallsHistory(packageReceived.getContent(), destinationIdentityPublicKey);

            /*
             * Get the client location
             */
            Location clientLocation = messageContent.getClientLocation();

            /*
             * Get the node catalog list
             */
            List<NodeCatalog> nodesCatalogs = getJPADaoFactory().getNodeCatalogDao().list();

            /*
             * Filter and order
             */
            List<NodeCatalog> nodesCatalogsFiltered = applyGeoLocationFilter(clientLocation, nodesCatalogs);

            /*
             * Create a node list
             */
            List<NodeProfile> nodesProfileList = new ArrayList<>();
            for (final NodeCatalog node: nodesCatalogsFiltered.subList(0,50)) {

                NodeProfile nodeProfile = new NodeProfile();
                nodeProfile.setIdentityPublicKey(node.getId());
                nodeProfile.setName(node.getName());
                nodeProfile.setDefaultPort(node.getDefaultPort());
                nodeProfile.setIp(node.getIp());
                nodeProfile.setLocation(node.getLocation());

                nodesProfileList.add(nodeProfile);
            }

            /*
             * If all ok, respond whit success message
             */
            NearNodeListMsgRespond respondNearNodeListMsg = new NearNodeListMsgRespond(NearNodeListMsgRespond.STATUS.SUCCESS, NearNodeListMsgRespond.STATUS.SUCCESS.toString(),nodesProfileList);
            channel.sendPackage(session, respondNearNodeListMsg.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.NEAR_NODE_LIST_RESPONSE, destinationIdentityPublicKey);

        }catch (Exception exception){

            try {

                LOG.error(exception);

                /*
                 * If all ok, respond whit success message
                 */
                NearNodeListMsgRespond respondNearNodeListMsg = new NearNodeListMsgRespond(NearNodeListMsgRespond.STATUS.FAIL, exception.getLocalizedMessage(), null);
                channel.sendPackage(session, respondNearNodeListMsg.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.NEAR_NODE_LIST_RESPONSE, destinationIdentityPublicKey);

            } catch (Exception e) {
                LOG.error(e);
            }
        }

    }


    /**
     *  Method that apply geo location filter to the list
     *
     * @param clientLocation
     * @param nodesCatalogs
     * @return List<NodesCatalog>
     */
    private List<NodeCatalog> applyGeoLocationFilter(Location clientLocation, List<NodeCatalog> nodesCatalogs) {

        /*
         * Hold the data ordered by distance
         */
        Map<Double, NodeCatalog> orderedByDistance = new TreeMap<>();

        /*
         * For each node
         */
        for (final NodeCatalog node: nodesCatalogs) {

            /*
             * If component have a geo location
             */
            if (node.getLocation().getLatitude() != 0 &&
                    node.getLocation().getLongitude() != 0){

                /*
                 * Calculate the distance between the two points
                 */
                Double componentDistance = DistanceCalculator.distance(clientLocation, node.getLocation(), DistanceCalculator.KILOMETERS);

                /*
                 * Add to the list
                 */
                orderedByDistance.put(componentDistance, node);

            }

        }

        return new ArrayList<>(orderedByDistance.values());
    }

    private JPADaoFactory getJPADaoFactory(){
        if (jpaDaoFactory == null)
            jpaDaoFactory = JPADaoFactory.getInstance();

        return jpaDaoFactory;
    }
}
