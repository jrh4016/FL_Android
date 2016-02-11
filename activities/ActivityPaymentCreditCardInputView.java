package com.skeds.android.phone.business.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.AsyncTasks.SendInvoiceTask;
import com.skeds.android.phone.business.Custom.AccountMenu;
import com.skeds.android.phone.business.Custom.QuickAction;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.Constants;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTAppointment;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCreditCard;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTInvoice;
import com.skeds.android.phone.business.ui.fragment.AppointmentFragment;
import com.skeds.android.phone.business.ui.fragment.InvoiceListFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.UniMagTools.uniMagReaderToolsMsg;
import IDTech.MSR.uniMag.UniMagTools.uniMagSDKTools;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;

//Step1, implements the interface uniMagReaderMsg
public class ActivityPaymentCreditCardInputView extends BaseSkedsActivity
        implements
        uniMagReaderMsg,
        uniMagReaderToolsMsg {

    private static final String DEBUG_TAG = "[Credit Card]";

    /*
     * Initial Values used by IDTech
     */
    private uniMagReader myUniMagReader = null;
    private boolean _isOK = false; // update the powerup status

    private String _strMsg = null;
    private boolean _isCardData = false;

    boolean getPressYESNO = false;
    private String _strMSRData = null;
    private byte[] _MSRData = null;

    // property for the menu item.
    private MenuItem itemStartSC = null;

    private Handler handler = new Handler();
    private boolean bReturnYESNO = false;

    private UniMagTopDialog dlgTopShow = null;
    private UniMagTopDialog dlgSwipeTopShow = null;

    private String firstName;
    private String lastName;
    private String cardNumber;
    private String expirationMonth;
    private String expirationYear;

    /*
     * Layout Stuff
     */
    private LinearLayout headerLayout;
    private ImageView headerButtonUser;
    private TextView headerButtonSwipeCard;
    private TextView headerButtonSubmit;
    private ImageView headerButtonBack;

    private EditText edittextNameOnCard;
    private EditText edittextCardNumber;
    private EditText edittextCardMonth;
    private EditText edittextCardYear;
    private EditText edittextCardCVV;
    private EditText edittextAddressLine1;
    private EditText edittextAddressLine2;
    private EditText edittextAddressCity;
    private Spinner spinnerAddressState;
    private Spinner spinnerAddressProvince;
    private EditText edittextAddressZip;
    private Spinner spinnerAddressCountry;

    private TextView textState;
    private TextView textZip;

    /*
     * Primary Email Dialog
     */
    private Dialog mPrimaryEmailDialog;
    private TextView mPrimaryEmailDialogSave, mPrimaryEmailDialogCancel;
    private EditText mPrimaryEmailDialogText;

    private Context mContext;
    private Activity mActivity;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_creditcard_view);

        // Used to control the volume of the app
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        headerLayout = (LinearLayout) findViewById(R.id.activity_header);

        mActivity = ActivityPaymentCreditCardInputView.this;
        mContext = this;

        final QuickAction accountMenu;
        accountMenu = AccountMenu.setupMenu(mContext, mActivity);

        headerButtonUser = (ImageView) headerLayout
                .findViewById(R.id.header_button_user);
        headerButtonBack = (ImageView) headerLayout
                .findViewById(R.id.header_button_back);

        headerButtonUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountMenu.show(v);
                accountMenu.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });

        headerButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        headerButtonSwipeCard = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_left);
        headerButtonSubmit = (TextView) headerLayout
                .findViewById(R.id.header_standard_button_right);

        edittextNameOnCard = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_name_on_card);
        edittextNameOnCard.requestFocus();

        edittextCardNumber = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_card_number);
        edittextCardMonth = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_expires_month);
        edittextCardYear = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_expires_year);
        edittextCardCVV = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_cvv);
        edittextAddressLine1 = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_address_1);
        edittextAddressLine2 = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_address_2);
        edittextAddressCity = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_city);
        spinnerAddressState = (Spinner) findViewById(R.id.activity_credit_card_payment_spinner_state);
        spinnerAddressProvince = (Spinner) findViewById(R.id.activity_credit_card_payment_spinner_province);
        edittextAddressZip = (EditText) findViewById(R.id.activity_credit_card_payment_edittext_zip);
        spinnerAddressCountry = (Spinner) findViewById(R.id.activity_credit_card_payment_spinner_country);

        textState = (TextView) findViewById(R.id.activity_credit_card_payment_textview_state);
        textZip = (TextView) findViewById(R.id.activity_credit_card_payment_textview_zip);

        setupUI();

        if (UserUtilitiesSingleton.getInstance().user.isUseCardReader())
            initializeReader();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (UserUtilitiesSingleton.getInstance().user.isUseCardReader()) {
            if (myUniMagReader != null) {
                // you should stop swipe card and unregister when the
                // application go to background
                myUniMagReader.stopSwipeCard();
                myUniMagReader.unregisterListen();
                myUniMagReader.release();
            }
            hideTopDialog();
            hideSwipeTopDialog();
        }
    }


    @Override
    protected void onResume() {
        if (UserUtilitiesSingleton.getInstance().user.isUseCardReader()) {
            if (myUniMagReader != null) {
                myUniMagReader.registerListen();
            }
            if (itemStartSC != null)
                itemStartSC.setEnabled(true);
        }
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(mContext,
                    ActivityPaymentOptionTypesView.class);
            startActivity(i);
            // finish();

            return true;

        } else if (KeyEvent.KEYCODE_HOME == keyCode
                || KeyEvent.KEYCODE_SEARCH == keyCode) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK
                || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {

            return false;
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK
                || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }


    private void setupUI() {

        int selection = -1;

        headerButtonSwipeCard.setOnClickListener(mSwipeCardListener);
        headerButtonSubmit.setOnClickListener(mSubmitListener);

        spinnerAddressCountry
                .setOnItemSelectedListener(mCountrySelectedListener);

        if (!UserUtilitiesSingleton.getInstance().user.isUseCardReader()) {
            headerButtonSwipeCard.setVisibility(View.GONE);
        }

        if (UserUtilitiesSingleton.getInstance().user.isCanadian()) {
            String[] provinces = mContext.getResources().getStringArray(
                    R.array.can_provinces_abr);
            for (int i = 0; i < provinces.length; i++) {
                if (provinces[i].toString().equals(
                        AppDataSingleton.getInstance().getInvoice().getCustomer().getAddressState()
                                .toString())) {
                    selection = i;
                    break;
                }
            }
        } else {
            String[] states = mContext.getResources().getStringArray(
                    R.array.us_states_abr);
            for (int i = 0; i < states.length; i++) {
                if (states[i].toString().equals(
                        AppDataSingleton.getInstance().getInvoice().getCustomer().getAddressState()
                                .toString())) {
                    selection = i;
                    break;
                }
            }
        }

        // Auto complete address
        edittextAddressLine1.setText(AppDataSingleton.getInstance().getInvoice().getCustomer()
                .getAddress1());
        edittextAddressLine2.setText(AppDataSingleton.getInstance().getInvoice().getCustomer()
                .getAddress2());
        edittextAddressCity.setText(AppDataSingleton.getInstance().getInvoice().getCustomer()
                .getAddressCity());
        edittextAddressZip.setText(AppDataSingleton.getInstance().getInvoice().getCustomer()
                .getAddressPostalCode());

        setupCountry(selection);
    }

    private void setupCountry(int stateNum) {

        InputFilter maxLengthFilter = null;

        if (!UserUtilitiesSingleton.getInstance().user.isCanadian()) {

            spinnerAddressState.setVisibility(View.VISIBLE);
            spinnerAddressProvince.setVisibility(View.GONE);

            spinnerAddressCountry.setSelection(0); // US
            if (stateNum != -1)
                spinnerAddressState.setSelection(stateNum);

            // Sets the max input length
            maxLengthFilter = new InputFilter.LengthFilter(5);
            edittextAddressZip.setFilters(new InputFilter[]{maxLengthFilter});

            textState.setText("State");
            textZip.setText("Zip Code");

        } else {

            spinnerAddressState.setVisibility(View.GONE);
            spinnerAddressProvince.setVisibility(View.VISIBLE);

            spinnerAddressCountry.setSelection(1); // Canada
            if (stateNum != -1)
                spinnerAddressProvince.setSelection(stateNum);

            // Sets the max input length
            maxLengthFilter = new InputFilter.LengthFilter(7);
            edittextAddressZip.setFilters(new InputFilter[]{maxLengthFilter});

            textState.setText("Province");
            textZip.setText("Postal Code");
        }
    }


    private OnItemSelectedListener mCountrySelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parentView,
                                   View selectedItemView, int position, long id) {

            setupCountry(-1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    };

    private OnClickListener mSwipeCardListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (myUniMagReader != null)
                myUniMagReader.startSwipeCard();
        }
    };

    private OnClickListener mSubmitListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            try {
                String year = edittextCardYear.getText().toString();

                int y = Integer.parseInt(year);

                int curYear = Calendar.getInstance().get(Calendar.YEAR);

                String month = edittextCardMonth.getText().toString();

                int m = Integer.parseInt(month);

                int curMonth = Calendar.getInstance().get(Calendar.MONTH);


                if ((edittextCardYear.getText().toString().length() < 4)) {
                    Toast.makeText(mContext, "Please check year", Toast.LENGTH_LONG).show();
                    return;
                }

                if ((y < curYear)) {
                    Toast.makeText(mContext, "Please check year", Toast.LENGTH_LONG).show();
                    return;
                }

                if (m > 12 || m < 1) {
                    Toast.makeText(mContext, "Incorrect Month", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (y == curYear)
                    if (m < curMonth + 1) {
                        Toast.makeText(mContext, "Credit Card is Expired", Toast.LENGTH_SHORT).show();
                        return;
                    }


                if (!TextUtils.isEmpty(edittextNameOnCard.getText())
                        && !TextUtils.isEmpty(edittextCardNumber.getText())
                        && !TextUtils.isEmpty(edittextCardMonth.getText())
                        && !TextUtils.isEmpty(edittextCardYear.getText())
                        && !TextUtils.isEmpty(edittextCardCVV.getText())
                        && !TextUtils.isEmpty(edittextAddressLine1.getText())
                        && !TextUtils.isEmpty(edittextAddressCity.getText())
                        && !TextUtils.isEmpty(edittextAddressZip.getText())) {

                    // ViewInvoice.CREDIT_CARD_TYPE = 0;

                    new SubmitInvoiceTask().execute();
                } else {
                    Toast.makeText(mContext,
                            "Please be sure to fill in all fields.",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private OnClickListener mPrimaryEmailDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dialog_primary_email_button_save:
                    if (!TextUtils.isEmpty(mPrimaryEmailDialogText.getText())) {
                        AppDataSingleton.getInstance().getInvoice().setCustomerEmail(
                                mPrimaryEmailDialogText.getText().toString());
                        mPrimaryEmailDialog.dismiss();
                        new SubmitInvoiceTask().execute();
                    } else {
                        Toast.makeText(mContext, "Email cannot be blank",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.dialog_primary_email_button_cancel:
                    mPrimaryEmailDialog.dismiss();
                    new SubmitInvoiceTask().execute();
                    break;
                default:
                    // Nothing
                    break;
            }
        }
    };


    private String getXMLFileFromRaw() {
        //the target filename in the application path
        String fileNameWithPath = null;
        fileNameWithPath = "idt_unimagcfg_default.xml";

        try {
            InputStream in = getResources().openRawResource(R.raw.idt_unimagcfg_default);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            in.close();
            deleteFile(fileNameWithPath);
            FileOutputStream fout = openFileOutput(fileNameWithPath, MODE_PRIVATE);
            fout.write(buffer);
            fout.close();

            // to refer to the application path
            File fileDir = this.getFilesDir();
            fileNameWithPath = fileDir.getParent() + java.io.File.separator + fileDir.getName();
            fileNameWithPath += java.io.File.separator + "idt_unimagcfg_default.xml";

        } catch (Exception e) {
            e.printStackTrace();
            fileNameWithPath = null;
        }
        return fileNameWithPath;
    }

    private void initializeReader() {
        /*
         * if(myUniMagReader!=null){ myUniMagReader.unregisterListen();
		 * myUniMagReader.release(); myUniMagReader = null; } // if
		 * (isConnectWithCommand) myUniMagReader = new
		 * uniMagReader(this,this,true); // else // myUniMagReader = new
		 * uniMagReader(this,this);
		 */
        if (myUniMagReader == null)
            myUniMagReader = new uniMagReader(this, this, true);

        myUniMagReader.setVerboseLoggingEnable(false);
        myUniMagReader.registerListen();

        // load the XML configuratin file

        new LoadConfigTask().execute();


        // Initializing SDKTool for firmware update
        uniMagSDKTools firmwareUpdateTool = new uniMagSDKTools(this, this);
        firmwareUpdateTool.setUniMagReader(myUniMagReader);
        myUniMagReader.setSDKToolProxy(firmwareUpdateTool.getSDKToolProxy());
    }

    private class LoadConfigTask extends BaseUiReportTask<String> {

        public LoadConfigTask() {
            super(ActivityPaymentCreditCardInputView.this, "Loading Config File...");
        }

        @Override
        protected boolean taskBody(String... params) throws Exception {

            String fileNameWithPath = getXMLFileFromRaw();
            if (!isFileExist(fileNameWithPath)) {
                fileNameWithPath = null;
            }

            myUniMagReader.setXMLFileNameWithPath(fileNameWithPath);
            myUniMagReader.loadingConfigurationXMLFile(true);
            return true;
        }

    }

    private boolean isFileExist(String path) {

        if (path == null)
            return false;

        File file = new File(path);
        if (!file.exists()) {
            return false;
        }

        return true;
    }

    private String getFileFromRaw() {
        // the target filename in the application path
        String fileNameWithPath = null;
        fileNameWithPath = "idt_unimagcfg_1_15_1.xml";

        try {
            InputStream in = getResources().openRawResource(
                    R.raw.idt_unimagcfg_default);

            int length = in.available();
            byte[] buffer = new byte[length];

            in.read(buffer);
            in.close();

            deleteFile(fileNameWithPath);

            FileOutputStream fout = openFileOutput(fileNameWithPath,
                    MODE_PRIVATE);
            fout.write(buffer);
            fout.close();

            // to refer to the application path
            File fileDir = mContext.getFilesDir();
            fileNameWithPath = fileDir.getParent() + java.io.File.separator
                    + fileDir.getName();
            fileNameWithPath = fileNameWithPath + java.io.File.separator
                    + "idt_unimagcfg_1_15_1.xml";

        } catch (Exception e) {
            e.printStackTrace();
            fileNameWithPath = null;
        }

        return fileNameWithPath;
    }

    public void showPopupWindow(Context context, View parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View vPopupWindow = inflater.inflate(R.layout.dlgtopview2bnt,
                null, false);
        final PopupWindow pw = new PopupWindow(vPopupWindow, 300, 300, true);

        TextView myTV = (TextView) vPopupWindow.findViewById(R.id.TView_Info);
        myTV.setText(_strMsg);

        Button btnOK = (Button) vPopupWindow.findViewById(R.id.btnYes);
        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bReturnYESNO = true;
                pw.dismiss();
            }
        });

        Button btnCancel = (Button) vPopupWindow.findViewById(R.id.btnNo);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bReturnYESNO = false;
                pw.dismiss();
            }
        });
        pw.showAsDropDown(vPopupWindow);
    }


    private class SubmitInvoiceTask extends BaseUiReportTask<String> {
        SubmitInvoiceTask() {
            super(ActivityPaymentCreditCardInputView.this,
                    R.string.async_task_string_submitting_completed_invoice);
        }

        @Override
        protected void onSuccess() {
            new SubmitCreditCardTask().execute();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTInvoice.update(AppDataSingleton.getInstance().getInvoice());
            return true;
        }
    }

    private class SubmitCreditCardTask extends BaseUiReportTask<String> {
        SubmitCreditCardTask() {
            super(ActivityPaymentCreditCardInputView.this,
                    R.string.async_task_string_submitting_payment);
        }

        @Override
        protected void onSuccess() {
            //TODO: dirty hack for cents here
            if (ActivityPaymentOptionTypesView.paidAmount.subtract(
                    AppDataSingleton.getInstance().getInvoice().getTotal()).abs().compareTo(
                    ActivityPaymentOptionTypesView.EPSILON) <= 0) {

                new CloseAppointmentTask().execute();


            }
            else {
                Toast.makeText(mContext, "Payment is accepted",
                        Toast.LENGTH_SHORT).show();

                onBackPressed();

            }
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            String firstName = "";
            String lastName = "";

            if (edittextNameOnCard.getText().toString().contains(" ")) {
                firstName = edittextNameOnCard
                        .getText()
                        .toString()
                        .substring(
                                0,
                                edittextNameOnCard.getText().toString()
                                        .indexOf(" "));
                lastName = edittextNameOnCard
                        .getText()
                        .toString()
                        .substring(
                                edittextNameOnCard.getText().toString()
                                        .indexOf(" "),
                                edittextNameOnCard.getText().toString()
                                        .length());
            } else {
                firstName = edittextNameOnCard.getText().toString();
            }

            // String first = mCardName
            // .getText()
            // .toString()
            // .substring(0,
            // mCardName.getText().toString().indexOf(" "));
            // String last = mCardName
            // .getText()
            // .toString()
            // .substring(mCardName.getText().toString().indexOf(" "),
            // mCardName.getText().toString().length());
            String cNum = edittextCardNumber.getText().toString();
            String year = edittextCardYear.getText().toString();
            String expDate = edittextCardMonth.getText().toString() + "/"
                    + (year.length() >= 2 ?
                    year.substring(year.length() - 2, year.length()) :     // get last 2 sybmols
                    year); // if length <2 - just send it
            String cvv = edittextCardCVV.getText().toString();
            String address1 = edittextAddressLine1.getText().toString();
            String address2 = edittextAddressLine2.getText().toString();
            String city = edittextAddressCity.getText().toString();
            int country = spinnerAddressCountry.getSelectedItemPosition();
            String state = "";

            if (country == 0) { // US
                int stateNum = spinnerAddressState.getSelectedItemPosition();
                String[] states = getResources().getStringArray(
                        R.array.us_states_full);

                state = states[stateNum].toString();
                states = null;
            } else {
                int provinceNum = spinnerAddressProvince
                        .getSelectedItemPosition();
                String[] provinces = getResources().getStringArray(
                        R.array.can_provinces_full);

                state = provinces[provinceNum].toString();
                provinces = null;
            }
            String zip = "";
            zip = edittextAddressZip.getText().toString();

            String amount = "";

            if (AppDataSingleton.getInstance().getInvoice().getPayments().size() > 0)
                amount = AppDataSingleton.getInstance().getInvoice().getPayments().get(AppDataSingleton.getInstance()
                        .getInvoice().getPayments().size() - 1).paymentAmount.toString();

            RESTCreditCard.add(firstName, lastName, cNum, expDate, cvv,
                    address1, address2, city, country, state, zip, amount);
            return true;
        }
    }

    private class CloseAppointmentTask extends BaseUiReportTask<String> {
        CloseAppointmentTask() {
            super(ActivityPaymentCreditCardInputView.this,
                    R.string.async_task_string_closing_appointment);
        }

        @Override
        protected void onSuccess() {
            Intent i = new Intent(mContext, ActivityDashboardView.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            // finish();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {

            DateFormat df = null;
            df = new SimpleDateFormat("M/d/yy h:mm a");
            Date todaysDate = new Date();// get current date time with
            // Date()
            String currentDateTime = df.format(todaysDate);

            if (AppDataSingleton.getInstance().getInvoiceViewMode() == Constants.INVOICE_VIEW_FROM_APPOINTMENT)

                RESTAppointment
                        .close(AppDataSingleton.getInstance().getAppointment().getId(),
                                UserUtilitiesSingleton.getInstance().user.getServiceProviderId(),
                                String.valueOf(SendInvoiceTask.mFromAppointmentLatitude),
                                String.valueOf(SendInvoiceTask.mFromAppointmentLongitude),
                                String.valueOf(SendInvoiceTask.mFromAppointmentAccuracy),
                                currentDateTime,
                                TimeZone.getDefault());
            else
                RESTAppointment.close(
                        AppDataSingleton.getInstance().getInvoiceList()
                                .get(InvoiceListFragment.mSelectedItem)
                                .getAppointmentId(),
                        UserUtilitiesSingleton.getInstance().user.getServiceProviderId(), "", "", "",
                        currentDateTime,
                        TimeZone.getDefault());
            return true;
        }
    }

    private Runnable doShowTimeoutMsg = new Runnable() {
        @Override
        public void run() {
            if (itemStartSC != null && _isCardData == true)
                itemStartSC.setEnabled(true);
            _isCardData = false;
            showDialog(_strMsg);
        }

    };

    private void showDialog(String strTitle) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("FieldLocate");
            builder.setMessage(strTitle);
            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ;

    private Runnable doShowTopDlg = new Runnable() {
        @Override
        public void run() {
            showTopDialog(_strMsg);
        }
    };

    private Runnable doHideTopDlg = new Runnable() {
        @Override
        public void run() {
            hideTopDialog();
        }

    };

    private Runnable doShowSwipeTopDlg = new Runnable() {
        @Override
        public void run() {
            showSwipeTopDialog(_strMsg);
        }
    };

    private Runnable doHideSwipeTopDlg = new Runnable() {
        @Override
        public void run() {
            hideSwipeTopDialog();
        }
    };

    private Runnable doUpdateTVS = new Runnable() {
        @Override
        public void run() {
            try {
                if (itemStartSC != null)
                    itemStartSC.setEnabled(true);
                // etCardData.setText(_strMSRData);

                Log.e(DEBUG_TAG, _strMSRData);

                cardNumber = _strMSRData
                        .substring(_strMSRData.indexOf("%B") + 2,
                                _strMSRData.indexOf("^"));

                String name = "";
                name = _strMSRData.substring(_strMSRData.indexOf("^"),
                        _strMSRData.lastIndexOf("^"));

                String names[] = name.split("/");
                if (names.length > 1) {
                    lastName = names[0].replace("^", "");
                    firstName = names[1].replace("^", "");
                } else {
                    lastName = names[0].replace("^", "");
                    firstName = "";
                }

                expirationMonth = _strMSRData.substring(
                        _strMSRData.lastIndexOf("^") + 3,
                        _strMSRData.lastIndexOf("^") + 5);
                expirationYear = _strMSRData.substring(
                        _strMSRData.lastIndexOf("^") + 1,
                        _strMSRData.lastIndexOf("^") + 3);

                edittextNameOnCard.setText(firstName + " " + lastName);
                edittextCardNumber.setText(cardNumber);
                edittextCardMonth.setText(expirationMonth);
                edittextCardYear.setText(expirationYear);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable doUpdateTV = new Runnable() {
        @Override
        public void run() {
        }
    };

    private Runnable doUpdateToast = new Runnable() {
        @Override
        public void run() {
            try {
                String msg = null; // "To start record the mic.";

                if (_isOK) {
                    msg = "Card Reader Connected";

                    int duration = Toast.LENGTH_LONG;
                    Toast.makeText(mContext, msg, duration).show();

                    if (itemStartSC != null)
                        itemStartSC.setEnabled(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Class: UniMagTopDialog Author: Eric Yang Date: 2010.10.12 Function: to
     * show the dialog on the top of the desktop.
     * <p/>
     * ****
     */
    private class UniMagTopDialog extends Dialog {

        public UniMagTopDialog(Context context) {
            super(context);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK
                    || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
                return false;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean onKeyMultiple(int keyCode, int repeatCount,
                                     KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK
                    || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {

                return false;
            }
            return super.onKeyMultiple(keyCode, repeatCount, event);
        }

        @Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK
                    || KeyEvent.KEYCODE_HOME == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
                return false;
            }
            return super.onKeyUp(keyCode, event);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        }
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        }
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
        }
        super.onConfigurationChanged(newConfig);
    }

    private void showTopDialog(String strTitle) {

        hideTopDialog();
        if (dlgTopShow == null)
            dlgTopShow = new UniMagTopDialog(mContext);
        try {
            Window win = dlgTopShow.getWindow();
            win.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dlgTopShow.setTitle("FieldLocate");
            dlgTopShow.setContentView(R.layout.dlgtopview);
            TextView myTV = (TextView) dlgTopShow.findViewById(R.id.TView_Info);

            myTV.setText(_strMsg);
            dlgTopShow.setOnKeyListener(new OnKeyListener() {

                // @Override
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                        return false;
                    }
                    return true;
                }

            });
            dlgTopShow.show();
        } catch (Exception e) {
            e.printStackTrace();
            dlgTopShow = null;
        }
    }

    ;

    private void hideTopDialog() {

        if (dlgTopShow != null) {

            try {
                dlgTopShow.hide();
                dlgTopShow.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            dlgTopShow = null;
        }
    }

    ;

    private void showSwipeTopDialog(String strTitle) {

        hideSwipeTopDialog();

        try {

            if (dlgSwipeTopShow == null)
                dlgSwipeTopShow = new UniMagTopDialog(mContext);

            Window win = dlgSwipeTopShow.getWindow();
            win.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dlgSwipeTopShow.setTitle("FieldLocate");
            dlgSwipeTopShow.setContentView(R.layout.dlgswipetopview);
            TextView myTV = (TextView) dlgSwipeTopShow
                    .findViewById(R.id.TView_Info);
            Button myBtn = (Button) dlgSwipeTopShow
                    .findViewById(R.id.btnCancel);

            myTV.setText(_strMsg);

            myBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemStartSC != null)
                        itemStartSC.setEnabled(true);
                    // stop swipe
                    myUniMagReader.stopSwipeCard();
                    dlgSwipeTopShow.dismiss();
                }
            });

            dlgSwipeTopShow.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                        return false;
                    }
                    return true;
                }
            });
            dlgSwipeTopShow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ;

    private void hideSwipeTopDialog() {
        try {
            if (dlgSwipeTopShow != null) {
                dlgSwipeTopShow.hide();
                dlgSwipeTopShow.dismiss();
                dlgSwipeTopShow = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ;

    private boolean showYESNOTopDialog(String strTitle) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("FieldLocate");
            builder.setMessage(strTitle);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bReturnYESNO = true;
                            getPressYESNO = true;
                            dialog.dismiss();
                        }
                    });
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bReturnYESNO = false;
                            getPressYESNO = true;
                            dialog.dismiss();
                        }
                    });
            builder.setCancelable(false);

            builder.create().show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bReturnYESNO;
    }

    ;

    // //Step7, implement the function onReceiveMsgCardData of the interface
    // uniMagReaderMsg
    // here you can receive the card data.
    @Override
    public void onReceiveMsgCardData(byte flagOfCardData, byte[] cardData) {
        byte flag = (byte) (flagOfCardData & 0x04);

        if (flag == 0x00)
            _strMSRData = new String(cardData);

        if (flag == 0x04) {
            // You need to dencrypt the data here first.
            _strMSRData = new String(cardData);
        }

        _MSRData = null;
        _MSRData = new byte[cardData.length];
        System.arraycopy(cardData, 0, _MSRData, 0, cardData.length);
        _isCardData = true;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doUpdateTVS);
    }

    // //Step8, implement the function onReceiveMsgConnected of the interface
    // uniMagReaderMsg
    // here you can receive the massage that the uniMag device connected the
    // phone.
    @Override
    public void onReceiveMsgConnected() {
        _isOK = true;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doUpdateTV);
        handler.post(doUpdateToast);
    }

    // //Step9, implement the function onReceiveMsgDisconnected of the interface
    // uniMagReaderMsg
    // here you can receive the massage that the uniMag device disconnected the
    // phone.
    @Override
    public void onReceiveMsgDisconnected() {
        _isOK = false;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doUpdateTV);
    }

    // //Step10, implement the function onReceiveMsgTimeout of the interface
    // uniMagReaderMsg
    // here you can receive the timeout massage when power up or swipe the card.
    @Override
    public void onReceiveMsgTimeout(String strTimeoutMsg) {
        _strMsg = strTimeoutMsg;
        _isCardData = true;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doShowTimeoutMsg);
    }

    // //Step11, implement the function onReceiveMsgToConnect of the interface
    // uniMagReaderMsg
    // here you can receive the massage when you power up the device.
    @Override
    public void onReceiveMsgToConnect() {
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        _strMsg = "Powering up card reader...";
        handler.post(doShowTopDlg);
    }

    // //Step12, implement the function onReceiveMsgToSwipeCard of the interface
    // uniMagReaderMsg
    // here you can receive the massage when you swipe the card.
    @Override
    public void onReceiveMsgToSwipeCard() {
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        _strMsg = "Please swipe card.";
        handler.post(doShowSwipeTopDlg);
    }

    @Override
    public void onReceiveMsgSDCardDFailed(String strSDCardFailed) {
        _strMsg = strSDCardFailed;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doShowTimeoutMsg);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    showYESNOTopDialog(_strMsg);
                    break;
                default:
                    // Nothing
                    break;
            }
            ;
        }

        ;
    };

    class MyThread implements Runnable {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public boolean getUserGrant(int type, String strMessage) {
        boolean getUserGranted = false;
        switch (type) {
            case uniMagReaderMsg.typeToPowerupUniMag:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToUpdateXML:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToOverwriteXML:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            case uniMagReaderMsg.typeToReportToIdtech:
                // pop up dialog to get the user grant
                getUserGranted = true;
                break;
            default:
                getUserGranted = false;
                break;

        }
        return getUserGranted;
    }

    @Override
    public void onReceiveMsgFailureInfo(int index, String strMessage) {
        _strMsg = "Failure[" + index + "]:" + strMessage;
        handler.post(doHideTopDlg);
        handler.post(doHideSwipeTopDlg);
        handler.post(doShowTimeoutMsg);
    }

    @Override
    public void onReceiveMsgChallengeResult(int arg0, byte[] arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveMsgUpdateFirmwareProgress(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveMsgUpdateFirmwareResult(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveMsgCommandResult(int arg0, byte[] arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReceiveMsgAutoConfigCompleted(StructConfigParameters arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onReceiveMsgAutoConfigProgress(int arg0, double arg1,
                                               String arg2) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onReceiveMsgProcessingCardData() {
        // TODO Auto-generated method stub

    }


    @Override
    public void onReceiveMsgToCalibrateReader() {
        // TODO Auto-generated method stub

    }

}