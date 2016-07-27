/*
 * @#JPADaoFactory.java - 2016
 * Copyright Fermat.org, All rights reserved.
 */
package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.daos;


/**
 * The class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.daos.JPADaoFactory</code>
 * represent the Data Access Object factory
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 23/07/16
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public final class JPADaoFactory {

    /**
     * Represent the instance
     */
    private static final JPADaoFactory instance = new JPADaoFactory();

    /**
     * Represent the actorCatalogDao
     */
    private final ActorCatalogDao actorCatalogDao;

    /**
     * Represent the actorCheckInDao
     */
    private final ActorCheckInDao actorCheckInDao;

    /**
     * Represent the clientCheckInDao
     */
    private final ClientCheckInDao clientCheckInDao;

    /**
     * Represent the clientDao
     */
    private final ClientDao clientDao;

    /**
     * Represent the methodCallsHistoryDao
     */
    private final MethodCallsHistoryDao methodCallsHistoryDao;

    /**
     * Represent the networkServiceCheckInDao
     */
    private final NetworkServiceCheckInDao networkServiceCheckInDao;

    /**
     * Represent the networkServiceDao
     */
    private final NetworkServiceDao networkServiceDao;

    /**
     * Represent the nodeCatalogDao
     */
    private final NodeCatalogDao nodeCatalogDao;

    /**
     * Represent the profileRegistrationHistoryDao
     */
    private final ProfileRegistrationHistoryDao profileRegistrationHistoryDao;

    /**
     * Constructor
     */
    private JPADaoFactory() {
        super();
        actorCatalogDao = new ActorCatalogDao();
        actorCheckInDao = new ActorCheckInDao();
        clientCheckInDao = new ClientCheckInDao();
        clientDao = new ClientDao();
        methodCallsHistoryDao = new MethodCallsHistoryDao();
        networkServiceCheckInDao = new NetworkServiceCheckInDao();
        networkServiceDao = new NetworkServiceDao();
        nodeCatalogDao = new NodeCatalogDao();
        profileRegistrationHistoryDao = new ProfileRegistrationHistoryDao();
    }

    /**
     * Get the value of actorCatalogDao
     *
     * @return actorCatalogDao
     */
    static public ActorCatalogDao getActorCatalogDao() {
        return instance.actorCatalogDao;
    }

    /**
     * Get the value of actorCheckInDao
     *
     * @return actorCheckInDao
     */
    static public ActorCheckInDao getActorCheckInDao() {
        return instance.actorCheckInDao;
    }

    /**
     * Get the value of clientCheckInDao
     *
     * @return clientCheckInDao
     */
    static public ClientCheckInDao getClientCheckInDao() {
        return instance.clientCheckInDao;
    }

    /**
     * Get the value of clientDao
     *
     * @return clientDao
     */
    static public ClientDao getClientDao() {
        return instance.clientDao;
    }

    /**
     * Get the value of methodCallsHistoryDao
     *
     * @return methodCallsHistoryDao
     */
    static public MethodCallsHistoryDao getMethodCallsHistoryDao() {
        return instance.methodCallsHistoryDao;
    }

    /**
     * Get the value of networkServiceCheckInDao
     *
     * @return networkServiceCheckInDao
     */
    static public NetworkServiceCheckInDao getNetworkServiceCheckInDao() {
        return instance.networkServiceCheckInDao;
    }

    /**
     * Get the value of networkServiceDao
     *
     * @return networkServiceDao
     */
    static public NetworkServiceDao getNetworkServiceDao() {
        return instance.networkServiceDao;
    }

    /**
     * Get the value of nodeCatalogDao
     *
     * @return nodeCatalogDao
     */
    static public NodeCatalogDao getNodeCatalogDao() {
        return instance.nodeCatalogDao;
    }

    /**
     * Get the value of profileRegistrationHistoryDao
     *
     * @return profileRegistrationHistoryDao
     */
    static public ProfileRegistrationHistoryDao getProfileRegistrationHistoryDao() {
        return instance.profileRegistrationHistoryDao;
    }

    static public JPADaoFactory getInstance(){
        return instance;
    }
}
