package com.bitdubai.reference_wallet.crypto_broker_wallet.fragments.common;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatEditText;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.Country;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_cbp_api.layer.negotiation.customer_broker_sale.exceptions.CantCreateLocationSaleException;
import com.bitdubai.fermat_cbp_api.layer.wallet_module.crypto_broker.interfaces.CryptoBrokerWalletModuleManager;
import com.bitdubai.reference_wallet.crypto_broker_wallet.R;

import java.util.ArrayList;
import java.util.List;


public class CreateNewLocationFragment extends AbstractFermatFragment<ReferenceAppFermatSession<CryptoBrokerWalletModuleManager>, ResourceProviderManager>
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    // Data
    private List<Country> countries = new ArrayList<>();
    private Country selectedCountry;
    private static int MAX_LENGHT_STATE = 30;
    private static int MAX_LENGHT_CITY = 30;
    private static int MAX_LENGHT_ADDRESS = 100;

    // UI
    private FermatEditText cityTextView;
    private FermatEditText stateEditText;
    private FermatEditText zipCodeEditText;
    private FermatEditText addressLineOneEditText;
    private FermatEditText addressLineTwoEditText;
    FermatTextView cityTextCount, stateTextCount, address1TextCount, address2TextCount;

    private CryptoBrokerWalletModuleManager walletManager;
    private ErrorManager errorManager;

    private final TextWatcher cityTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {cityTextCount.setText(String.valueOf(MAX_LENGHT_CITY - s.length()));}
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };
    private final TextWatcher stateTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {stateTextCount.setText(String.valueOf(MAX_LENGHT_STATE - s.length()));}
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };
    private final TextWatcher address1TextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {address1TextCount.setText(String.valueOf(MAX_LENGHT_ADDRESS - s.length()));}
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };
    private final TextWatcher address2TextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {address2TextCount.setText(String.valueOf(MAX_LENGHT_ADDRESS - s.length()));}
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };

    public static CreateNewLocationFragment newInstance() {
        return new CreateNewLocationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.cbw_fragement_create_new_location, container, false);

        for(Country c : Country.values()) {
               if(c != Country.NONE)
                   countries.add(c);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.cbw_spinner_item, getListOfCountryNames(countries));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner countrySpinner = (Spinner) layout.findViewById(R.id.cbw_country_spinner);
        countrySpinner.setOnItemSelectedListener(this);
        countrySpinner.setAdapter(adapter);

        cityTextView = (FermatEditText) layout.findViewById(R.id.cbw_city_edit_text);

        stateEditText = (FermatEditText) layout.findViewById(R.id.cbw_state_edit_text);
        zipCodeEditText = (FermatEditText) layout.findViewById(R.id.cbw_zip_code_edit_text);
        addressLineOneEditText = (FermatEditText) layout.findViewById(R.id.cbw_address_line_1_edit_text);
        addressLineTwoEditText = (FermatEditText) layout.findViewById(R.id.cbw_address_line_2_edit_text);
        stateTextCount = (FermatTextView) layout.findViewById(R.id.cbw_state_edit_text_count);
        cityTextCount = (FermatTextView) layout.findViewById(R.id.cbw_city_edit_text_count);
        address1TextCount = (FermatTextView) layout.findViewById(R.id.cbw_address_line_1_edit_text_count);
        address2TextCount = (FermatTextView) layout.findViewById(R.id.cbw_address_line_2_edit_text_count);

        //Limit max length, add textWatchers
        cityTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGHT_CITY)});
        stateEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGHT_STATE)});
        addressLineOneEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGHT_ADDRESS)});
        addressLineTwoEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGHT_ADDRESS)});

        cityTextView.addTextChangedListener(cityTextWatcher);
        stateEditText.addTextChangedListener(stateTextWatcher);
        addressLineOneEditText.addTextChangedListener(address1TextWatcher);
        addressLineTwoEditText.addTextChangedListener(address2TextWatcher);

        cityTextCount.setText(String.valueOf(MAX_LENGHT_CITY));
        stateTextCount.setText(String.valueOf(MAX_LENGHT_STATE));
        address1TextCount.setText(String.valueOf(MAX_LENGHT_ADDRESS));
        address2TextCount.setText(String.valueOf(MAX_LENGHT_ADDRESS));

        layout.findViewById(R.id.cbw_create_new_location_button).setOnClickListener(this);


        try {
            walletManager = appSession.getModuleManager();
            errorManager = appSession.getErrorManager();
        } catch (Exception e) {
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(Wallets.CBP_CRYPTO_BROKER_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, e);
        }

        configureToolbar();

        return layout;
    }

    private void configureToolbar() {
        Toolbar toolbar = getToolbar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            toolbar.setBackground(getResources().getDrawable(R.drawable.cbw_action_bar_gradient_colors, null));
        else
            toolbar.setBackground(getResources().getDrawable(R.drawable.cbw_action_bar_gradient_colors));

        if (toolbar.getMenu() != null) toolbar.getMenu().clear();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCountry = countries.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onClick(View view) {
        StringBuilder location = new StringBuilder();


        if (cityTextView.getText().toString().length() > 0)
            location.append(cityTextView.getText().toString()).append(", ");

        if (stateEditText.getText().toString().length() > 0)
            location.append(stateEditText.getText().toString()).append(", ");

        if (zipCodeEditText.getText().toString().length() > 0)
            location.append(zipCodeEditText.getText().toString()).append(", ");

        if (addressLineOneEditText.getText().toString().length() > 0)
            location.append(addressLineOneEditText.getText().toString()).append(", ");

        if (addressLineTwoEditText.getText().toString().length() > 0)
            location.append(addressLineTwoEditText.getText().toString()).append(", ");

        if (selectedCountry != null)
            location.append(selectedCountry.getCountry()).append(".");

        if (location.length() > 0) {
            try {
                walletManager.createNewLocation(location.toString(), "");
                changeActivity(Activities.CBP_CRYPTO_BROKER_WALLET_SETTINGS_MY_LOCATIONS, appSession.getAppPublicKey());
            } catch (CantCreateLocationSaleException e) {
                if (errorManager != null)
                    errorManager.reportUnexpectedWalletException(Wallets.CBP_CRYPTO_BROKER_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_THIS_FRAGMENT, e);
            }
            /*
                List<String> locations = (List<String>) appSession.getData(CryptoBrokerWalletSession.LOCATION_LIST);
                int pos = locations.size()-1;
                if(locations.get(pos).equals("settings")){
                    locations.remove(pos);
                    locations.add(location.toString());
                    changeActivity(Activities.CBP_CRYPTO_BROKER_WALLET_SETTINGS_MY_LOCATIONS, appSession.getAppPublicKey());
                }
                if(locations.get(pos).equals("wizard")){
                    locations.remove(pos);
                    locations.add(location.toString());
                    changeActivity(Activities.CBP_CRYPTO_BROKER_WALLET_SET_LOCATIONS, appSession.getAppPublicKey());
                }
            */
        } else {
            Toast.makeText(getActivity(), "Need to set the fields", Toast.LENGTH_LONG).show();
        }
    }

    private List<String> getListOfCountryNames(List<Country> countries) {
        List<String> data = new ArrayList<>();
        for (Country country : countries)
            data.add(country.getCountry());
        return data;
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        List<String> locations = (List<String>) appSession.getData(CryptoBrokerWalletSession.LOCATION_LIST);
        int pos = locations.size()-1;
        if(locations.get(pos).equals("settings") || locations.get(pos).equals("wizard")){
            locations.remove(pos);
        }
    }*/

}
