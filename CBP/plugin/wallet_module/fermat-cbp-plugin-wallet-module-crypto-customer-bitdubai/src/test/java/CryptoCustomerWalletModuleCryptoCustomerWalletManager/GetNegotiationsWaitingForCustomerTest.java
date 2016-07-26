package CryptoCustomerWalletModuleCryptoCustomerWalletManager;

import com.bitdubai.fermat_cbp_api.layer.wallet_module.common.exceptions.CantGetNegotiationsWaitingForCustomerException;
import com.bitdubai.fermat_cbp_plugin.layer.wallet_module.crypto_customer.developer.bitdubai.version_1.structure.CryptoCustomerWalletModuleCryptoCustomerWalletManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

/**
 * Created by roy on 7/02/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class GetNegotiationsWaitingForCustomerTest {
    @Test
    public void getNegotiationsWaitingForCustomer() throws CantGetNegotiationsWaitingForCustomerException {
        CryptoCustomerWalletModuleCryptoCustomerWalletManager cryptoCustomerWalletModuleCryptoCustomerWalletManager = mock(CryptoCustomerWalletModuleCryptoCustomerWalletManager.class, Mockito.RETURNS_DEEP_STUBS);
        doCallRealMethod().when(cryptoCustomerWalletModuleCryptoCustomerWalletManager).getNegotiationsWaitingForCustomer(Mockito.any(Integer.class), Mockito.any(Integer.class));
    }
}
