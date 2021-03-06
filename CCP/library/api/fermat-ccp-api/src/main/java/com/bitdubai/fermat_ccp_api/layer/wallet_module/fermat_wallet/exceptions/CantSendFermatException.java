package com.bitdubai.fermat_ccp_api.layer.wallet_module.fermat_wallet.exceptions;

/**
 * The interface <code>CantGetBalanceException</code>
 * is thrown when i cant get balance of the wallet.
 *
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 18/06/15.
 * @version 1.0
 */
public class CantSendFermatException extends FermatWalletException {

    public static final String DEFAULT_MESSAGE = "CAN'T SEND CRYPTO EXCEPTION";

    public CantSendFermatException(final String message, final Exception cause, final String context, final String possibleReason) {
        super(message, cause, context, possibleReason);
    }

    public CantSendFermatException(final String message, final Exception cause) {
        this(message, cause, "", "");
    }

    public CantSendFermatException(final String message) {
        this(message, null);
    }

    public CantSendFermatException() {
        this(DEFAULT_MESSAGE);
    }
}
