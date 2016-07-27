/*
 * @#ClientCheckIn.java - 2016
 * Copyright Fermat.org, All rights reserved.
 */
package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.enums.ProfileStatus;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.websocket.Session;

/**
 * The interface <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities.ClientCheckIn</code> is
 * represent the session of a client into the node
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 23/07/16
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
@Entity
@NamedQueries(
        @NamedQuery(name="isClientOnline",query="SELECT c from ClientCheckIn c where c.client.id = :id and c.client.status = ProfileStatus.ONLINE")
)
public class ClientCheckIn extends AbstractBaseEntity<String>{

    /**
     * Represent the serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Represent the id
     */
    @Id
    private String id;

    /**
     * Represent the client
     */
    @NotNull
    @OneToOne(cascade = {CascadeType.ALL}, targetEntity = Client.class)
    private Client client;

    /**
     * Represent the timestamp
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;

    /**
     * Constructor
     */
    public ClientCheckIn() {
        super();
        this.id = "";
        this.client = null;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Constructor with parameter
     */
    public ClientCheckIn(Session session) {
        super();
        this.id = session.getId();
        this.client = null;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Constructor with parameters
     *
     * @param session
     * @param clientProfile
     */
    public ClientCheckIn(Session session, Client clientProfile) {
        this.id = session.getId();
        this.client = clientProfile;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * (non-javadoc)
     * @see AbstractBaseEntity@getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Set the id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the client
     * @return client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Set the networkService
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Get the timestamp
     * @return timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp
     * @param timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * (non-javadoc)
     * @see AbstractBaseEntity@equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientCheckIn)) return false;

        ClientCheckIn that = (ClientCheckIn) o;

        return getId().equals(that.getId());

    }

    /**
     * (non-javadoc)
     * @see AbstractBaseEntity@hashCode()
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * (non-javadoc)
     *
     * @see AbstractBaseEntity@toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientCheckIn{");
        sb.append("id='").append(id).append('\'');
        sb.append(", client=").append((client != null ? client.getId() : null));
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();
    }
}
