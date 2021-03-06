package org.fermat.fermat_dap_android_sub_app_redeem_point_identity.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.utils.ImagesUtils;
import com.bitdubai.fermat_android_api.ui.Views.PresentationDialog;
import com.bitdubai.fermat_android_api.ui.transformation.CircleTransform;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.exceptions.ActorIdentityNotSelectedException;
import com.bitdubai.fermat_api.layer.modules.exceptions.CantGetSelectedActorIdentityException;
import com.bitdubai.fermat_api.layer.pip_engine.interfaces.ResourceProviderManager;
import com.bitdubai.fermat_dap_android_sub_app_redeem_point_identity_bitdubai.R;
import com.squareup.picasso.Picasso;

import org.fermat.fermat_dap_android_sub_app_redeem_point_identity.session.SessionConstants;
import org.fermat.fermat_dap_android_sub_app_redeem_point_identity.util.CommonLogger;
import org.fermat.fermat_dap_android_sub_app_redeem_point_identity.util.IdentityRedeemDialogCropImage;
import org.fermat.fermat_dap_api.layer.dap_identity.asset_issuer.exceptions.CantCreateNewIdentityAssetIssuerException;
import org.fermat.fermat_dap_api.layer.dap_identity.redeem_point.exceptions.CantCreateNewRedeemPointException;
import org.fermat.fermat_dap_api.layer.dap_identity.redeem_point.interfaces.RedeemPointIdentity;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.redeem_point_identity.RedeemPointIdentitySettings;
import org.fermat.fermat_dap_api.layer.dap_sub_app_module.redeem_point_identity.interfaces.RedeemPointIdentityModuleManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.widget.Toast.makeText;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateRedeemPointIdentityFragment extends AbstractFermatFragment<ReferenceAppFermatSession<RedeemPointIdentityModuleManager>, ResourceProviderManager> {

    private static final String TAG = "RedeemPointIdentity";

    private static final int CREATE_IDENTITY_FAIL_MODULE_IS_NULL = 0;
    private static final int CREATE_IDENTITY_FAIL_NO_VALID_DATA = 1;
    private static final int CREATE_IDENTITY_FAIL_MODULE_EXCEPTION = 2;
    private static final int CREATE_IDENTITY_SUCCESS = 3;

    private static final int GALLERY_KITKAT_INTENT_CALLED = 3;
    private Bitmap mIdentityBitmap;
    private Uri imageToUploadUri;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    private static final int CONTEXT_MENU_CAMERA = 1;
    private static final int CONTEXT_MENU_GALLERY = 2;

    private byte[] brokerImageByteArray;

    private RedeemPointIdentityModuleManager moduleManager;
    private ErrorManager errorManager;

    private Button createButton;
    private TextView mIdentityName;
    private TextView mIdentityContactInformation;
    private TextView mIdentityAddressCountryName;
    private TextView mIdentityAddressProvinceName;
    private TextView mIdentityAddressCityName;
    private TextView mIdentityAddressPostalCode;
    private TextView mIdentityAddressStreetName;
    private TextView mIdentityAddressHouseNumber;
    private ImageView mIdentityImage;

    private RedeemPointIdentity identitySelected;
    private boolean isUpdate = false;

    RedeemPointIdentitySettings redeemPointIdentitySettings = null;

    private boolean updateProfileImage = false;
    private boolean contextMenuInUse = false;

    ExecutorService executorService;

    public static CreateRedeemPointIdentityFragment newInstance() {
        return new CreateRedeemPointIdentityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        executorService = Executors.newFixedThreadPool(3);

        try {
            moduleManager = appSession.getModuleManager();
            errorManager = appSession.getErrorManager();
            setHasOptionsMenu(true);

            executorService.submit(new Runnable() {
                @Override
                public void run() {
//                    settingsManager = appSession.getModuleManager().getSettingsManager();

                    try {
                        if (appSession.getAppPublicKey() != null) {
                            redeemPointIdentitySettings = moduleManager.loadAndGetSettings(appSession.getAppPublicKey());
                        } else {
                            redeemPointIdentitySettings = moduleManager.loadAndGetSettings("1");
                        }
                    } catch (Exception e) {
                        redeemPointIdentitySettings = null;
                    }

                    try {
                        if (redeemPointIdentitySettings == null) {
                            redeemPointIdentitySettings = new RedeemPointIdentitySettings();
                            redeemPointIdentitySettings.setIsPresentationHelpEnabled(true);
                            if (appSession.getAppPublicKey() != null) {
                                moduleManager.persistSettings(appSession.getAppPublicKey(), redeemPointIdentitySettings);
                            } else {
                                moduleManager.persistSettings("1", redeemPointIdentitySettings);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            CommonLogger.exception(TAG, ex.getMessage(), ex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootLayout = inflater.inflate(R.layout.fragment_dap_create_redeem_point_identity, container, false);
        initViews(rootLayout);
        setUpIdentity();

        Handler handlerTimer = new Handler();
        handlerTimer.postDelayed(new Runnable() {
            public void run() {
                if (redeemPointIdentitySettings != null) {
                    if (redeemPointIdentitySettings.isPresentationHelpEnabled()) {
                        setUpPresentation(false);
                    }
                }
            }
        }, 500);

        return rootLayout;
    }

    private void setUpPresentation(boolean checkButton) {
        try {
            PresentationDialog presentationDialog = new PresentationDialog.Builder(getActivity(), appSession)
                    .setBannerRes(R.drawable.banner_redeem_point_identity)
                    .setIconRes(R.drawable.redeem_point)
                    .setVIewColor(R.color.dap_identity_redeem_view_color)
                    .setTitleTextColor(R.color.dap_identity_redeem_view_color)
                    .setSubTitle(R.string.dap_redeem_identity_welcome_subTitle)
                    .setBody(R.string.dap_redeem_identity_welcome_body)
                    .setTemplateType(PresentationDialog.TemplateType.TYPE_PRESENTATION_WITHOUT_IDENTITIES)
                    .setIsCheckEnabled(checkButton)
                    .build();

            presentationDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inicializa las vistas de este Fragment
     *
     * @param layout el layout de este Fragment que contiene las vistas
     */
    private void initViews(View layout) {
        createButton = (Button) layout.findViewById(R.id.dap_redeem_point_button);
        mIdentityImage = (ImageView) layout.findViewById(R.id.dap_redeem_point_image);
        mIdentityName = (TextView) layout.findViewById(R.id.dap_redeem_point_name);
        mIdentityContactInformation = (TextView) layout.findViewById(R.id.dap_redeem_point_contact_information);
        mIdentityAddressCountryName = (TextView) layout.findViewById(R.id.dap_redeem_point_country_name);
        mIdentityAddressProvinceName = (TextView) layout.findViewById(R.id.dap_redeem_point_address_province_name);
        mIdentityAddressCityName = (TextView) layout.findViewById(R.id.dap_redeem_point_address_city_name);
        mIdentityAddressPostalCode = (TextView) layout.findViewById(R.id.dap_redeem_point_address_postal_code);
        mIdentityAddressStreetName = (TextView) layout.findViewById(R.id.dap_redeem_point_address_street_name);
        mIdentityAddressHouseNumber = (TextView) layout.findViewById(R.id.dap_redeem_point_address_house_number);


        createButton.setText((!isUpdate) ? "Create" : "Update");

        mIdentityName.requestFocus();
        registerForContextMenu(mIdentityImage);

//        mIdentityContactInformation.requestFocus();
//        mIdentityAddressCountryName.requestFocus();
//        mIdentityAddressProvinceName.requestFocus();
//        mIdentityAddressCityName.requestFocus();
//        mIdentityAddressPostalCode.requestFocus();
//        mIdentityAddressStreetName.requestFocus();
//        mIdentityAddressHouseNumber.requestFocus();

        mIdentityImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonLogger.debug(TAG, "Entrando en mIdentityImage.setOnClickListener");
                getActivity().openContextMenu(mIdentityImage);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonLogger.debug(TAG, "Entrando en createButton.setOnClickListener");
                createNewIdentity();

            }
        });
    }

    private void publishResult(final int resultKey) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (resultKey) {
                    case CREATE_IDENTITY_SUCCESS:
//                        changeActivity(Activities.DAP_SUB_APP_REDEEM_POINT_IDENTITY.getCode(), appSession.getAppPublicKey());
                        if (!isUpdate) {
                            Toast.makeText(getActivity(), "Identity created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Changes saved", Toast.LENGTH_SHORT).show();
                        }
                        getActivity().onBackPressed();
                        break;
                    case CREATE_IDENTITY_FAIL_MODULE_EXCEPTION:
                        Toast.makeText(getActivity(), "Error al crear la identidad", Toast.LENGTH_LONG).show();
                        break;
                    case CREATE_IDENTITY_FAIL_NO_VALID_DATA:
                        Toast.makeText(getActivity(), "La data no es valida", Toast.LENGTH_LONG).show();
                        break;
                    case CREATE_IDENTITY_FAIL_MODULE_IS_NULL:
                        Toast.makeText(getActivity(), "No se pudo acceder al module manager, es null", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void setUpIdentity() {
        try {

            identitySelected = (RedeemPointIdentity) appSession.getData(SessionConstants.IDENTITY_SELECTED);

            if (identitySelected != null) {
                loadIdentity();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ActiveActorIdentityInformation activeActorIdentityInformation = null;
                        try {
                            activeActorIdentityInformation = appSession.getModuleManager().getSelectedActorIdentity();
                        } catch (CantGetSelectedActorIdentityException | ActorIdentityNotSelectedException e) {
                            e.printStackTrace();
                        }
                        if (activeActorIdentityInformation != null) {
                            identitySelected = (RedeemPointIdentity) activeActorIdentityInformation;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (identitySelected != null) {
                                                                loadIdentity();
                                                                isUpdate = true;
                                                                createButton.setText("Save changes");
                                                            }
                                                        }
                                                    }
                        );
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIdentity() {
        if (identitySelected.getImage() != null) {
            Bitmap bitmap = null;
            if (identitySelected.getImage().length > 0) {
                bitmap = BitmapFactory.decodeByteArray(identitySelected.getImage(), 0, identitySelected.getImage().length);
//                bitmap = Bitmap.createScaledBitmap(bitmap, mBrokerImage.getWidth(), mBrokerImage.getHeight(), true);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_actor);

                //Picasso.with(getActivity()).load(R.drawable.profile_image).into(mBrokerImage);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            brokerImageByteArray = ImagesUtils.toByteArray(bitmap);
            mIdentityImage.setImageDrawable(ImagesUtils.getRoundedBitmap(getResources(), bitmap));
        }
        mIdentityName.setText(identitySelected.getAlias());
        mIdentityContactInformation.setText(identitySelected.getContactInformation());
        mIdentityAddressCountryName.setText(identitySelected.getCountryName());
        mIdentityAddressProvinceName.setText(identitySelected.getProvinceName());
        mIdentityAddressCityName.setText(identitySelected.getCityName());
        mIdentityAddressPostalCode.setText(identitySelected.getPostalCode());
        mIdentityAddressStreetName.setText(identitySelected.getStreetName());
        mIdentityAddressHouseNumber.setText(identitySelected.getHouseNumber());
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            Bitmap imageBitmap = null;
//            ImageView pictureView = mIdentityImage;
//            contextMenuInUse = false;
//
//            switch (requestCode) {
//                case REQUEST_IMAGE_CAPTURE:
//                    Bundle extras = data.getExtras();
//                    imageBitmap = (Bitmap) extras.get("data");
//                    break;
//                case REQUEST_LOAD_IMAGE:
//                    Uri selectedImage = data.getData();
//                    try {
//                        if (isAttached) {
//                            ContentResolver contentResolver = getActivity().getContentResolver();
//                            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage);
//                            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, pictureView.getWidth(), pictureView.getHeight(), true);
//                            brokerImageByteArray = ImagesUtils.toByteArray(imageBitmap);
//                            updateProfileImage = true;
//                            Picasso.with(getActivity()).load(selectedImage).transform(new CircleTransform()).into(mIdentityImage);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(getActivity().getApplicationContext(), "Error Load Image", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//            }
//
////            if (pictureView != null && imageBitmap != null)
////                pictureView.setImageDrawable(new BitmapDrawable(getResources(), imageBitmap));
////                pictureView.setImageDrawable(ImagesUtils.getRoundedBitmap(getResources(), imageBitmap));
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Choose mode");
        menu.setHeaderIcon(getActivity().getResources().getDrawable(R.drawable.ic_camera_green));
        menu.add(Menu.NONE, CONTEXT_MENU_CAMERA, Menu.NONE, "Camera");
        menu.add(Menu.NONE, CONTEXT_MENU_GALLERY, Menu.NONE, "Gallery");

        super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!contextMenuInUse) {
            switch (item.getItemId()) {
                case CONTEXT_MENU_CAMERA:
                    dispatchTakePictureIntent();
                    contextMenuInUse = true;
                    return true;
                case CONTEXT_MENU_GALLERY:
                    loadImageFromGallery();
                    contextMenuInUse = true;
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Crea una nueva identidad para un crypto broker
     *
     * @return key con el resultado de la operacion:<br/><br/>
     * <code>CREATE_IDENTITY_SUCCESS</code>: Se creo exitosamente una identidad <br/>
     * <code>CREATE_IDENTITY_FAIL_MODULE_EXCEPTION</code>: Se genero una excepcion cuando se ejecuto el metodo para crear la identidad en el Module Manager <br/>
     * <code>CREATE_IDENTITY_FAIL_MODULE_IS_NULL</code>: No se tiene una referencia al Module Manager <br/>
     * <code>CREATE_IDENTITY_FAIL_NO_VALID_DATA</code>: Los datos ingresados para crear la identidad no son validos (faltan datos, no tiene el formato correcto, etc) <br/>
     */
    private int createNewIdentity() {

        final String brokerNameText = mIdentityName.getText().toString();
        final String brokerContactInformation = mIdentityContactInformation.getText().toString();
        final String brokerAddressProvinceName = mIdentityAddressProvinceName.getText().toString();
        final String brokerAddressStreetName = mIdentityAddressStreetName.getText().toString();
        final String brokerAddressPostalCode = mIdentityAddressPostalCode.getText().toString();
        final String brokerAddressCityName = mIdentityAddressCityName.getText().toString();
        final String brokerAddressCountryName = mIdentityAddressCountryName.getText().toString();
        final String brokerAddressHouseNumber = mIdentityAddressHouseNumber.getText().toString();
        boolean dataIsValid = validateIdentityData(brokerNameText, brokerImageByteArray);

        if (dataIsValid) {
            if (appSession.getModuleManager() != null) {
                try {
                    if (!isUpdate) {
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    moduleManager.createNewRedeemPoint(brokerNameText, (brokerImageByteArray == null) ? convertImage(R.drawable.profile_actor) : brokerImageByteArray,
                                            brokerContactInformation, brokerAddressCountryName, brokerAddressProvinceName, brokerAddressCityName, brokerAddressPostalCode,
                                            brokerAddressStreetName, brokerAddressHouseNumber);
                                    publishResult(CREATE_IDENTITY_SUCCESS);
                                } catch (CantCreateNewRedeemPointException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (updateProfileImage)
                                        moduleManager.updateIdentityRedeemPoint(identitySelected.getPublicKey(), brokerNameText, brokerImageByteArray, brokerContactInformation,
                                                brokerAddressCountryName, brokerAddressProvinceName, brokerAddressCityName, brokerAddressPostalCode, brokerAddressStreetName, brokerAddressHouseNumber);
                                    else
                                        moduleManager.updateIdentityRedeemPoint(identitySelected.getPublicKey(), brokerNameText, identitySelected.getImage(), brokerContactInformation,
                                                brokerAddressCountryName, brokerAddressProvinceName, brokerAddressCityName, brokerAddressPostalCode, brokerAddressStreetName, brokerAddressHouseNumber);
                                    publishResult(CREATE_IDENTITY_SUCCESS);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    errorManager.reportUnexpectedUIException(UISource.VIEW, UnexpectedUIExceptionSeverity.UNSTABLE, e);
                }
                return CREATE_IDENTITY_SUCCESS;
            }
            return CREATE_IDENTITY_FAIL_MODULE_IS_NULL;
        }
        return CREATE_IDENTITY_FAIL_NO_VALID_DATA;

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            contextMenuInUse = false;
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    // grant all three uri permissions!
                    if (imageToUploadUri != null) {
                        String provider = "com.android.providers.media.MediaProvider";
                        Uri selectedImage = imageToUploadUri;
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                getActivity().getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                getActivity().grantUriPermission(provider, selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                getActivity().grantUriPermission(provider, selectedImage, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                getActivity().grantUriPermission(provider, selectedImage, Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                getActivity().requestPermissions(
                                        new String[]{Manifest.permission.CAMERA},
                                        REQUEST_IMAGE_CAPTURE);
                            }
                        }
                        getActivity().getContentResolver().notifyChange(selectedImage, null);
                        Bitmap reducedSizeBitmap = getBitmap(imageToUploadUri.getPath());
                        if (reducedSizeBitmap != null) {
                            mIdentityBitmap = reducedSizeBitmap;
                        }
                    }
                    try {
                        if (checkCameraPermission()) {
                            if (checkWriteExternalPermission()) {
                                if (mIdentityBitmap != null) {
                                    if (mIdentityBitmap.getWidth() >= 192 && mIdentityBitmap.getHeight() >= 192) {
                                        final IdentityRedeemDialogCropImage identityRedeemDialogCropImage = new IdentityRedeemDialogCropImage(getActivity(), appSession, null, mIdentityBitmap);
                                        identityRedeemDialogCropImage.show();
                                        identityRedeemDialogCropImage.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                if (identityRedeemDialogCropImage.getCroppedImage() != null) {
                                                    mIdentityBitmap = getResizedBitmap(rotateBitmap(identityRedeemDialogCropImage.getCroppedImage(), ExifInterface.ORIENTATION_NORMAL), 230, 230);
                                                    Picasso.with(getActivity()).load(getImageUri(getActivity(), mIdentityBitmap)).transform(new CircleTransform()).into(mIdentityImage);
                                                    brokerImageByteArray = ImagesUtils.toByteArray(mIdentityBitmap);
                                                    updateProfileImage = true;
                                                } else {
                                                    mIdentityBitmap = null;
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "The image selected is too small. Please select a photo with height and width of at least 192x192", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Error on upload image", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
                    }
                    break;
                case REQUEST_LOAD_IMAGE:
                    Uri selectedImage = data.getData();
                    try {
                        if (isAttached) {
                            ContentResolver contentResolver = getActivity().getContentResolver();
                            mIdentityBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage);
                            if (mIdentityBitmap.getWidth() >= 192 && mIdentityBitmap.getHeight() >= 192) {
                                final IdentityRedeemDialogCropImage identityRedeemDialogCropImagee = new IdentityRedeemDialogCropImage(getActivity(), appSession, null, mIdentityBitmap);
                                identityRedeemDialogCropImagee.show();
                                identityRedeemDialogCropImagee.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        if (identityRedeemDialogCropImagee.getCroppedImage() != null) {
                                            mIdentityBitmap = getResizedBitmap(rotateBitmap(identityRedeemDialogCropImagee.getCroppedImage(), ExifInterface.ORIENTATION_NORMAL), 230, 230);
                                            Picasso.with(getActivity()).load(getImageUri(getActivity(), mIdentityBitmap)).transform(new CircleTransform()).into(mIdentityImage);
                                            brokerImageByteArray = ImagesUtils.toByteArray(mIdentityBitmap);
                                            updateProfileImage = true;
                                        } else {
                                            mIdentityBitmap = null;
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "The image selected is too small. Please select a photo with height and width of at least 192x192", Toast.LENGTH_LONG).show();
                            }

                        }
                    } catch (Exception e) {
                        errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
                        Toast.makeText(getActivity().getApplicationContext(), "Error loading the image", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GALLERY_KITKAT_INTENT_CALLED:
                    Uri selectedImagee = data.getData();
                    // Check for the freshest data.
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            String provider = "com.android.providers.media.MediaProvider";
                            getActivity().getContentResolver().takePersistableUriPermission(selectedImagee, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getActivity().getContentResolver().takePersistableUriPermission(selectedImagee, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getActivity().grantUriPermission(provider, selectedImagee, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            getActivity().grantUriPermission(provider, selectedImagee, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getActivity().grantUriPermission(provider, selectedImagee, Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            getActivity().requestPermissions(
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_LOAD_IMAGE);
                        }
                    }
                    try {
                        if (isAttached) {
                            mIdentityBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImagee);
                            if (mIdentityBitmap.getWidth() >= 192 && mIdentityBitmap.getHeight() >= 192) {
                                final IdentityRedeemDialogCropImage identityRedeemDialogCropImagee = new IdentityRedeemDialogCropImage(getActivity(), appSession, null, mIdentityBitmap);
                                identityRedeemDialogCropImagee.show();
                                identityRedeemDialogCropImagee.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        if (identityRedeemDialogCropImagee.getCroppedImage() != null) {
                                            mIdentityBitmap = getResizedBitmap(rotateBitmap(identityRedeemDialogCropImagee.getCroppedImage(), ExifInterface.ORIENTATION_NORMAL), 230, 230);
                                            Picasso.with(getActivity()).load(getImageUri(getActivity(), mIdentityBitmap)).transform(new CircleTransform()).into(mIdentityImage);
                                            brokerImageByteArray = ImagesUtils.toByteArray(mIdentityBitmap);
                                            updateProfileImage = true;
                                        } else {
                                            mIdentityBitmap = null;
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "The image selected is too small. Please select a photo with height and width of at least 192x192", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
                        Toast.makeText(getActivity().getApplicationContext(), "Error loading the image", Toast.LENGTH_SHORT).show();
                    }
            }
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);

    }

    private byte[] convertImage(int resImage) {
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), resImage);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

//    private void dispatchTakePictureIntent() {
//        Log.i(TAG, "Opening Camera app to take the picture...");
//
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    private void loadImageFromGallery() {
//        Log.i(TAG, "Loading Image from Gallery...");
//
//        Intent loadImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(loadImageIntent, REQUEST_LOAD_IMAGE);
//    }

    private boolean validateIdentityData(String brokerNameText, byte[] brokerImageBytes) {
        if (brokerNameText.isEmpty())
            return false;
        if (brokerImageBytes == null)
            return true;
        if (brokerImageBytes.length > 0)
            return true;

        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.dap_redeem_identity_menu_main, menu);
//        menu.add(0, SessionConstants.IC_ACTION_REDEEM_IDENTITY_HELP_PRESENTATION, 0, R.string.help).setIcon(R.drawable.dap_identity_redeem_help_icon)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();

            switch (id) {
                //case IC_ACTION_REDEEM_IDENTITY_HELP_PRESENTATION:
                case 2:
                setUpPresentation(moduleManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
                    break;
//                case 1:
//                    changeActivity(Activities.CHT_CHAT_GEOLOCATION_IDENTITY, appSession.getAppPublicKey());
//                    break;
            }
//            if (item.getItemId() == SessionConstants.IC_ACTION_REDEEM_IDENTITY_HELP_PRESENTATION) {
//                setUpPresentation(moduleManager.loadAndGetSettings(appSession.getAppPublicKey()).isPresentationHelpEnabled());
//                return true;
//            }

        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));
            makeText(getActivity(), "Identity Redeem system error",
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean ExistIdentity() throws CantCreateNewIdentityAssetIssuerException {
        try {
            if (!moduleManager.getIdentityAssetRedeemPoint().getAlias().isEmpty()) {
                Log.i("DAP EXIST IDENTITY", "TRUE");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        Log.i("DAP EXIST IDENTITY", "FALSE");
        return false;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void dispatchTakePictureIntent() {
        // Check permission for CAMERA
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    getActivity().requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_IMAGE_CAPTURE);
                } else {
                    getActivity().requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_IMAGE_CAPTURE);
                }
            } else {
                if (checkWriteExternalPermission()) {
                    Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
                    chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    imageToUploadUri = Uri.fromFile(f);
                    startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_LONG).show();
                }
            }
        } else {

            Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File f = new File(Environment.getExternalStorageDirectory(), "POST_IMAGE.jpg");
            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            imageToUploadUri = Uri.fromFile(f);
            startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private Bitmap getBitmap(String path) {
        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 3000000; // 1.2MP
            in = getActivity().getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getActivity().getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void loadImageFromGallery() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        GALLERY_KITKAT_INTENT_CALLED);
            }
        }
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
//            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Choose picture"), REQUEST_LOAD_IMAGE);
        } else {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/jpeg");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private boolean checkWriteExternalPermission() {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermission() {
        String permission = "android.permission.CAMERA";
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
