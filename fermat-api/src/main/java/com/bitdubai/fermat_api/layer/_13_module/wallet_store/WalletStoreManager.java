package com.bitdubai.fermat_api.layer._13_module.wallet_store;

import com.bitdubai.fermat_api.layer._13_module.wallet_store.exceptions.CantRecordInstalledWalletException;
import com.bitdubai.fermat_api.layer._13_module.wallet_store.exceptions.CantRecordUninstalledWalletException;

import java.util.UUID;

/**
 * Created by loui on 05/02/15.
 */
public interface WalletStoreManager {
    
    public void recordInstalledWallet (UUID walletId) throws CantRecordInstalledWalletException;
    
    public void recordUninstalledwallet (UUID walletId) throws CantRecordUninstalledWalletException;
    
    
}
