/*
 * @#ClientCheckInDao.java - 2016
 * Copyright Fermat.org, All rights reserved.
 */
package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.daos;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.enums.ProfileTypes;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ClientProfile;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities.Client;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities.ClientCheckIn;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities.ProfileRegistrationHistory;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.enums.RegistrationResult;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.enums.RegistrationType;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.exceptions.CantDeleteRecordDataBaseException;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.exceptions.CantInsertRecordDataBaseException;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.exceptions.CantReadRecordDataBaseException;

import org.apache.commons.lang.ClassUtils;
import org.jboss.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.websocket.Session;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.ClientCheckInDao</code>
 * is the responsible for manage the <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities.ClientCheckIn</code> entity
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 22/07/16
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ClientCheckInDao  extends AbstractBaseDao<ClientCheckIn>{

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(ClientCheckInDao.class));

    /**
     * Constructor
     */
    public ClientCheckInDao(){
        super(ClientCheckIn.class);
    }

    /**
     * Check in a client and associate with the session
     *
     * @param session
     * @param clientProfile
     */
    public void checkIn(Session session, ClientProfile clientProfile) throws CantInsertRecordDataBaseException {

        LOG.debug("Executing checkIn(" + session.getId() + ", " + clientProfile.getIdentityPublicKey() + ")");

        EntityManager connection = getConnection();
        EntityTransaction transaction = connection.getTransaction();

        try {

            transaction.begin();
            Client client = new Client(clientProfile);
            ClientCheckIn clientCheckIn = new ClientCheckIn(session, client);

            if (exist(session.getId())){
                connection.merge(clientCheckIn);
            }else {
                connection.persist(clientCheckIn);
            }

            ProfileRegistrationHistory profileRegistrationHistory = new ProfileRegistrationHistory(clientProfile.getIdentityPublicKey(), clientProfile.getDeviceType(), ProfileTypes.CLIENT, RegistrationType.CHECK_IN, RegistrationResult.SUCCESS, "");
            connection.persist(profileRegistrationHistory);

            transaction.commit();

        }catch (Exception e){
            transaction.rollback();
            throw new CantInsertRecordDataBaseException(CantReadRecordDataBaseException.DEFAULT_MESSAGE, e, "Network Node", "");
        }finally {
            connection.close();
        }

    }

    /**
     * Check out a client associate with the session, and check out all network services and
     * actors associate with this session too
     *
     * @param session
     */
    public void checkOut(Session session) throws CantDeleteRecordDataBaseException {

        LOG.debug("Executing checkOut("+session.getId()+")");

        EntityManager connection = getConnection();
        EntityTransaction transaction = connection.getTransaction();

        try {

            transaction.begin();

            ClientCheckIn clientCheckIn = findById(session.getId());

            if (clientCheckIn != null){

                connection.remove(clientCheckIn);

                JPADaoFactory.getNetworkServiceCheckInDao().checkOut(session, clientCheckIn.getClient());

                JPADaoFactory.getActorCheckInDao().checkOut(session, clientCheckIn.getClient());

                ProfileRegistrationHistory profileRegistrationHistory = new ProfileRegistrationHistory(clientCheckIn.getClient().getId(), clientCheckIn.getClient().getDeviceType(), ProfileTypes.CLIENT, RegistrationType.CHECK_OUT, RegistrationResult.SUCCESS, "");
                connection.persist(profileRegistrationHistory);
            }

            transaction.commit();

        }catch (Exception e){
            transaction.rollback();
            throw new CantDeleteRecordDataBaseException(CantReadRecordDataBaseException.DEFAULT_MESSAGE, e, "Network Node", "");
        }finally {
            connection.close();
        }

    }

    /**
     * This method deletes all the client checked
     * @throws CantDeleteRecordDataBaseException
     */
    public void deleteAll() throws CantDeleteRecordDataBaseException {
        LOG.debug("Executing deleting all the client checked");

        EntityManager connection = getConnection();
        EntityTransaction transaction = connection.getTransaction();
        try{
            transaction.begin();
            Query query = connection.createNamedQuery("DELETE FROM ClientCheckIn");
            int count = query.executeUpdate();
            LOG.debug(new StringBuilder("Deleted ")
                    .append(count)
                    .append(" records"));
            transaction.commit();
        } catch (Exception e){
            transaction.rollback();
            throw new CantDeleteRecordDataBaseException(
                    CantReadRecordDataBaseException.DEFAULT_MESSAGE,
                    e,
                    "Network Node",
                    "Cannot delete all the clients checked");
        }finally {
            connection.close();
        }
    }

}
