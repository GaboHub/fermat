package com.bitdubai.wallet_platform_core.layer._12_agent;

import com.bitdubai.wallet_platform_api.layer.CantStartLayerException;
import com.bitdubai.wallet_platform_api.layer.PlatformLayer;
import com.bitdubai.wallet_platform_api.layer._12_agent.AIAgent;
// import com.bitdubai.wallet_platform_core.layer._12_agent.licesnse.LicenseSubsystem;


/**
 * Created by ciencias on 03.01.15.
 */
public class AgentLayer implements PlatformLayer {

    AIAgent mLicenseAgent;

    /*
    public AIAgent getLicenseAgent() {

        return mLicenseAgent;
    } 
    */

    @Override
    public void start()  throws CantStartLayerException {


        /**
         * I will start now the license agent.
         */
        /*
        AgentSubsystem licenseSubsystem = new LicenseSubsystem();

        try {
            licenseSubsystem.start();
            mLicenseAgent = ((AgentSubsystem) licenseSubsystem).getAIAgent();

        } catch (CantStartSubsystemException e) {
            System.err.println("CantStartSubsystemException: " + e.getMessage());
*/
            /**
             * The license agent is vital for the com.bitdubai.platform to work. Without it starting there is no way to use the
             * com.bitdubai.platform itself.
             */
  /*          throw new CantStartLayerException();
        
        }
    */    
    }
}
