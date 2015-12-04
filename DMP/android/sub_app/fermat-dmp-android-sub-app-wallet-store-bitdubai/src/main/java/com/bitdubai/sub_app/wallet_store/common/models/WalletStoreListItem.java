package com.bitdubai.sub_app.wallet_store.common.models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bitdubai.fermat_api.layer.all_definition.enums.WalletCategory;
import com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_store.enums.InstallationStatus;
import com.bitdubai.fermat_wpd_api.layer.wpd_sub_app_module.wallet_store.interfaces.WalletStoreCatalogueItem;
import com.wallet_store.bitdubai.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import static com.bitdubai.fermat_wpd_api.layer.wpd_middleware.wallet_store.enums.InstallationStatus.INSTALLED;

/**
 * Created on 22/08/15.
 * Representa la informacion de un item de la lista de wallets que esta en el catalogo de la Wallet Store.
 *
 * @author Nelson Ramirez
 */
public class WalletStoreListItem implements Serializable {
    private static final long serialVersionUID = -8730067026050196759L;


    private InstallationStatus installationStatus;
    private String walletName;
    private String description;
    private Bitmap walletIcon;
    private UUID id;
    private WalletCategory category;
    private boolean testData;
    private int bannerWalletRes;


    /**
     * Crea un nuevo WalletStoreListItem
     *
     * @param catalogueItem un item del catalogo
     * @param res           resource object to generate the icon
     */
    public WalletStoreListItem(WalletStoreCatalogueItem catalogueItem, Resources res) {
        testData = false;

        id = catalogueItem.getId();

        category = catalogueItem.getCategory();

        walletName = catalogueItem.getName();

        description = catalogueItem.getDescription();

        installationStatus = catalogueItem.getInstallationStatus();

        try {
            byte[] iconBytes = catalogueItem.getIcon();
            walletIcon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);
        } catch (Exception e) {
            walletIcon = BitmapFactory.decodeResource(res, R.drawable.wallet_1);
        }

    }

    public int getBannerWalletRes() {
        return bannerWalletRes;
    }


    public String getDescription() {
        return description;
    }

    public WalletCategory getCategory() {
        return category;
    }

    public UUID getId() {
        return id;
    }

    public InstallationStatus getInstallationStatus() {
        return installationStatus;
    }

    public String getWalletName() {
        return walletName;
    }

    public Bitmap getWalletIcon() {
        return walletIcon;
    }


    private WalletStoreListItem(String walletName, InstallationStatus installationStatus, Bitmap walletIcon, int bannerWalletRes) {
        this.walletName = walletName;
        this.installationStatus = installationStatus;
        this.walletIcon = walletIcon;
        this.testData = true;
        this.bannerWalletRes = bannerWalletRes;
    }

    public static ArrayList<WalletStoreListItem> getTestData(Resources res) {

        String[] walletNames = {
                "Bitcoin Wallet",
                "Crypto Broker",
                "Crypto Customer",
                "Asset Issuer",
                "Asset User",
                "Redeem Point"};

        String[] prices = {
                "FREE",
                "FREE",
                "FREE",
                "FREE",
                "FREE",
                "FREE"};

        InstallationStatus[] installed = {
                INSTALLED,
                INSTALLED,
                INSTALLED,
                INSTALLED,
                INSTALLED,
                INSTALLED};

        int[] walletIcons = {
                R.drawable.bitcoin_wallet_med,
                R.drawable.crypto_broker_med,
                R.drawable.crypto_customer_med,
                R.drawable.asset_issuer,
                R.drawable.asset_user_wallet,
                R.drawable.redeem_point};

        int[] walletBanners = {
                R.drawable.banner_bitcoin_wallet,
                R.drawable.banner_crypto_broker,
                R.drawable.banner_crypto_customer,
                R.drawable.asset_issuer,
                R.drawable.asset_user_wallet,
                R.drawable.redeem_point};

        ArrayList<WalletStoreListItem> testItems = new ArrayList<>();
        for (int i = 0; i < walletIcons.length && i < installed.length && i < prices.length && i < walletNames.length; i++) {
            Bitmap icon = BitmapFactory.decodeResource(res, walletIcons[i]);
            WalletStoreListItem item = new WalletStoreListItem(walletNames[i], installed[i], icon, walletBanners[i]);
            testItems.add(item);
        }

        return testItems;
    }


    public boolean isTestData() {
        return testData;
    }

    public void setBannerWalletRes(int bannerWalletRes) {
        this.bannerWalletRes = bannerWalletRes;
    }
}
