package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;

import java.util.List;

/**
 * The Class <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.ActorsProfileListMsgRespond</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 27/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ActorsProfileListMsgRespond extends MsgRespond {

    /**
     * Represent the profile list
     */
    private List<ResultDiscoveryTraceActor> profileList;

    /**
     * Constructor with parameters
     *
     * @param status
     * @param details
     * @param profileList
     */
    public ActorsProfileListMsgRespond(STATUS status, String details, List<ResultDiscoveryTraceActor> profileList) {
        super(status, details);
        this.profileList = profileList;
    }

    /**
     * Gets the value of profileList and returns
     *
     * @return profileList
     */
    public List<ResultDiscoveryTraceActor> getProfileList() {
        return profileList;
    }

    /**
     * Generate the json representation
     * @return String
     */
    @Override
    public String toJson() {
        return GsonProvider.getGson().toJson(this, getClass());
    }

    /**
     * Get the object
     *
     * @param content
     * @return PackageContent
     */
    public static ActorsProfileListMsgRespond parseContent(String content) {
        return GsonProvider.getGson().fromJson(content, ActorsProfileListMsgRespond.class);
    }
}
