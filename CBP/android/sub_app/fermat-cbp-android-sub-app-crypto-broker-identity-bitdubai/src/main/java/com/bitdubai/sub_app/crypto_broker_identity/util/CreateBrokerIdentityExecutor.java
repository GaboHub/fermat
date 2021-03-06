package com.bitdubai.sub_app.crypto_broker_identity.util;

import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedSubAppExceptionSeverity;
import com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.enums.SubApps;
import com.bitdubai.fermat_cbp_api.all_definition.enums.Frecuency;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_identity.exceptions.CantCreateCryptoBrokerException;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_identity.interfaces.CryptoBrokerIdentityInformation;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_broker_identity.interfaces.CryptoBrokerIdentityModuleManager;

/**
 * Execute the method of the module manager to create a broker identity
 * <p/>
 * Created by nelson on 14/10/15.
 */
public class CreateBrokerIdentityExecutor {
    public static final int EXCEPTION_THROWN = 3;
    public static final int SUCCESS = 1;
    public static final int INVALID_ENTRY_DATA = 2;
    public static final int MISSING_IMAGE = 4;

    private byte[] imageInBytes;
    private String identityName;

    private CryptoBrokerIdentityModuleManager moduleManager;
    private ErrorManager errorManager;
    private CryptoBrokerIdentityInformation identity;

    public CreateBrokerIdentityExecutor(byte[] imageInBytes, String identityName) {
        this.imageInBytes = imageInBytes;
        this.identityName = identityName;
    }

    public CreateBrokerIdentityExecutor(CryptoBrokerIdentityModuleManager moduleManager, ErrorManager errorManager, byte[] imageInBytes, String identityName) {
        this(imageInBytes, identityName);

        this.moduleManager = moduleManager;
        this.errorManager = errorManager;
        identity = null;
    }

    public CreateBrokerIdentityExecutor(ReferenceAppFermatSession<CryptoBrokerIdentityModuleManager> session, String identityName, byte[] imageInBytes) {
        this(imageInBytes, identityName);
        identity = null;

        if (session != null) {
            this.moduleManager = session.getModuleManager();
            this.errorManager = session.getErrorManager();
        }
    }

    public int execute() {

        if (imageIsInvalid())
            return MISSING_IMAGE;

        if (entryDataIsInvalid())
            return INVALID_ENTRY_DATA;

        try {
            //TODO:NELSON Hay que pasarle los valores correcto al accuracy y la frecuencia
            identity = moduleManager.createCryptoBrokerIdentity(identityName, imageInBytes,0, Frecuency.NONE);

        } catch (CantCreateCryptoBrokerException ex) {
            if (errorManager != null)
                errorManager.reportUnexpectedSubAppException(
                        SubApps.CBP_CRYPTO_BROKER_IDENTITY,
                        UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT,
                        ex);

            return EXCEPTION_THROWN;
        }

        return SUCCESS;
    }

    public CryptoBrokerIdentityInformation getIdentity() {
        return identity;
    }

    private boolean imageIsInvalid() {
        if (imageInBytes == null) return true;
        return imageInBytes.length == 0;
    }

    private boolean entryDataIsInvalid() {
        if (moduleManager == null) return true;
        if (imageInBytes == null) return true;
        if (imageInBytes.length == 0) return true;
        if (identityName == null) return true;
        return identityName.isEmpty();
    }
}
