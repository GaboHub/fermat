package com.bitdubai.fermat_ccp_plugin.layer.wallet_module.loss_protected_wallet.developer.bitdubai.version_1.exceptions;


import com.bitdubai.fermat_ccp_api.layer.wallet_module.loss_protected_wallet.exceptions.LossProtectedWalletException;

/**
 * The Class <code>CantCreateOrRegisterActorException</code>
 * is thrown when i cant create or register an actor.
 * <p/>
 *
 * Created Natalia Cortez on 07/03/2016.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CantRequestOrRegisterLossProtectedAddressException extends LossProtectedWalletException {

    public static final String DEFAULT_MESSAGE = "CAN'T REQUEST OR REGISTER LOS PROTECTED ADDRESS EXCEPTION";

    public CantRequestOrRegisterLossProtectedAddressException(final String message, final Exception cause, final String context, final String possibleReason) {
        super(message, cause, context, possibleReason);
    }
}
