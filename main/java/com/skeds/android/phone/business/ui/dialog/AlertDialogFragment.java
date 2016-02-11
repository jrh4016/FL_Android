package com.skeds.android.phone.business.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.skeds.android.phone.business.R;

public class AlertDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_VIEW_STUB_LAYOUT_ID = "view_stub_layout_id";
    public static final String EXTRA_POSITIVE_BUTTON = "positive_button";
    public static final String EXTRA_NEGATIVE_BUTTON = "negative_button";
    public static final String EXTRA_NEUTRAL_BUTTON = "neutral_button";

    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;
    private OnClickListener mNeutralListener;
    private OnBodyInflatedListener mBodyInflatedListener;
    private OnViewCreatedListener mViewCreated;
    private OnValidateValuesBeforeDismissListener mValidateValuesBeforeDismissListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_alert_dialog, container, false);
    }

    @Override
    public void onDetach() {
        //remove listeners
        mPositiveListener = null;
        mNegativeListener = null;
        mNeutralListener = null;
        mBodyInflatedListener = null;
        mViewCreated = null;
        mValidateValuesBeforeDismissListener = null;
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Bundle args = getArguments();
        if (args == null || args.isEmpty()) {
            throw new IllegalStateException("Arguments must be supplied to build alert dialog");
        }

        final CharSequence title = args.getCharSequence(EXTRA_TITLE);
        final CharSequence message = args.getCharSequence(EXTRA_MESSAGE);
        final CharSequence positiveButtonTitle = args.getCharSequence(EXTRA_POSITIVE_BUTTON);
        final CharSequence negativeButtonTitle = args.getCharSequence(EXTRA_NEGATIVE_BUTTON);
        final CharSequence neutralButtonTitle = args.getCharSequence(EXTRA_NEUTRAL_BUTTON);
        final int bodyLayoutId = args.getInt(EXTRA_VIEW_STUB_LAYOUT_ID);

        if (TextUtils.isEmpty(title) && bodyLayoutId == 0) {
            throw new IllegalStateException(
                    "At least title or body must be supplied to build alert dialog");
        }

        final View fragmentView = getView();

        if (mViewCreated != null) {
            mViewCreated.onViewCreated(fragmentView);
        }

        final TextView titleView = (TextView) fragmentView.findViewById(R.id.title);

//        final View neutralDivider = fragmentView.findViewById(R.id.negative_neutral_divider);
//        final View positiveDivider = fragmentView.findViewById(R.id.neutral_positive_divider);

        final ViewStub stub = (ViewStub) fragmentView.findViewById(R.id.view_stub);
        final TextView positiveButton = (TextView) fragmentView.findViewById(R.id.button_positive);
        final TextView negativeButton = (TextView) fragmentView.findViewById(R.id.button_negative);
        final TextView neutralButton = (TextView) fragmentView.findViewById(R.id.button_neutral);

        final View divider = fragmentView.findViewById(R.id.divider);
        final View buttonContainer = fragmentView.findViewById(R.id.button_container);

        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
        neutralButton.setOnClickListener(this);

        stub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                if (mBodyInflatedListener != null) {
                    mBodyInflatedListener.onBodyInflated(getTag(), inflated);
                }
            }
        });

        // setup dialog view
        setupText(title, titleView);
        if (TextUtils.isEmpty(title)) {
            fragmentView.findViewById(R.id.divider_title).setVisibility(View.GONE);
        }

        // generally we show message in case if it is not empty and use basic layout that contains
        // only TextView
        if (TextUtils.isEmpty(message)) {
            setupDialogBody(bodyLayoutId, stub);
        } else {
            setupMessage(stub, message);
        }

        setupText(positiveButtonTitle, positiveButton);
        setupText(negativeButtonTitle, negativeButton);
        setupText(neutralButtonTitle, neutralButton);

        // hide divider if at least one button is hidden.
        boolean hasPositive = !TextUtils.isEmpty(positiveButtonTitle);
        boolean hasNegative = !TextUtils.isEmpty(negativeButtonTitle);
        boolean hasNeutral = !TextUtils.isEmpty(neutralButtonTitle);
        if (hasPositive && hasNegative && hasNeutral) {
//            neutralDivider.setVisibility(View.VISIBLE);
//            positiveDivider.setVisibility(View.VISIBLE);
        } else if (hasPositive && hasNegative || hasPositive && hasNeutral || hasNeutral
                && hasNegative) {
//            positiveDivider.setVisibility(View.VISIBLE);
//            neutralDivider.setVisibility(View.GONE);
        } else if (!hasNegative && !hasNeutral && !hasPositive) {
            divider.setVisibility(View.GONE);
            buttonContainer.setVisibility(View.GONE);
        } else {
//            positiveDivider.setVisibility(View.GONE);
//            neutralDivider.setVisibility(View.GONE);
        }
    }

    public void setOnPositiveButtonListener(final OnClickListener listener) {
        mPositiveListener = listener;
    }

    public void setOnNegativeButtonListener(final OnClickListener listener) {
        mNegativeListener = listener;
    }

    public void setOnNeutralButtonListener(final OnClickListener listener) {
        mNeutralListener = listener;
    }

    public void setOnBodyInflatedListener(final OnBodyInflatedListener listener) {
        mBodyInflatedListener = listener;
    }

    public void setOnValidateValuesBeforeDismissListener(final OnValidateValuesBeforeDismissListener listener) {
        mValidateValuesBeforeDismissListener = listener;
    }

    public void setOnViewCreated(final OnViewCreatedListener listener) {
        mViewCreated = listener;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        final String fragmentTag = getTag();
        if (viewId == R.id.button_positive && mPositiveListener != null) {
            mPositiveListener.onClick(v, fragmentTag);
        } else if (viewId == R.id.button_negative && mNegativeListener != null) {
            mNegativeListener.onClick(v, fragmentTag);
        } else if (viewId == R.id.button_neutral && mNeutralListener != null) {
            mNeutralListener.onClick(v, fragmentTag);
        }

        //in case if we want to validate input fields then should set validation listener otherwise dialog will be dismissed on any action
        if ((viewId == R.id.button_positive || viewId == R.id.button_neutral) && mValidateValuesBeforeDismissListener != null) {
            if (!mValidateValuesBeforeDismissListener.onValidate(getView(), fragmentTag)) {
                return;
            }
        }

        // dismiss dialog on any action performed
        dismiss();
    }

    private void setupText(CharSequence text, TextView view) {
        if (TextUtils.isEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
        }
    }

    // the body of alert dialog is view stub in order to make it general for use. You can define
    // any view to make your custom dialog
    private void setupDialogBody(final int bodyLayoutId, final ViewStub stub) {
        if (bodyLayoutId > 0) {
            stub.setLayoutResource(bodyLayoutId);
            stub.inflate();
        } else {
            stub.setVisibility(View.GONE);
        }
    }

    private void setupMessage(final ViewStub stub, final CharSequence message) {
        stub.setLayoutResource(R.layout.v_dialog_message);
        TextView view = (TextView) stub.inflate();
        view.setText(message);
    }

    // notify parent when the custom body of the alert dialog was inflated
    public static interface OnBodyInflatedListener {
        void onBodyInflated(String fragmentTag, View bodyView);
    }

    // notify parent when fragment view was measured and layouted
    public static interface OnViewCreatedListener {
        void onViewCreated(View fragmentView);
    }

    public static interface OnClickListener {
        void onClick(View v, String fragmentTag);
    }

    public static interface OnValidateValuesBeforeDismissListener {
        boolean onValidate(View fragmentView, String fragmentTag);
    }

    public static class DialogBuilder {
        private final Context mContext;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButton;
        private CharSequence mNegativeButton;
        private CharSequence mNeutralButton;
        private int mViewStubLayoutId;
        private OnClickListener mPositiveListener;
        private OnClickListener mNegativeListener;
        private OnClickListener mNeutralListener;
        private OnValidateValuesBeforeDismissListener mOnValidateValuesBeforeDismissListener;
        // handle dialog general behaviour
        private OnBodyInflatedListener mOnBodyInflatedListener;

        public DialogBuilder(Context context) {
            mContext = context;
        }

        public DialogBuilder setTitle(final CharSequence title) {
            mTitle = title;
            return this;
        }

        public DialogBuilder setMessage(final CharSequence message) {
            mMessage = message;
            return this;
        }

        public DialogBuilder setTitle(int title) {
            return setTitle(mContext.getText(title));
        }

        public DialogBuilder setMessage(int message) {
            return setMessage(mContext.getText(message));
        }

        public DialogBuilder setPositiveButton(CharSequence buttonTitle) {
            return setPositiveButton(buttonTitle, null);
        }

        public DialogBuilder setPositiveButton(CharSequence buttonTitle,
                                               OnClickListener listener) {
            mPositiveButton = buttonTitle;
            mPositiveListener = listener;
            return this;
        }

        public DialogBuilder setPositiveButton(int buttonTitle, OnClickListener listener) {
            return setPositiveButton(mContext.getText(buttonTitle), listener);
        }

        public DialogBuilder setNegativeButton(CharSequence buttonTitle) {
            return setNegativeButton(buttonTitle, null);
        }

        public DialogBuilder setNegativeButton(CharSequence buttonTitle,
                                               OnClickListener listener) {
            mNegativeButton = buttonTitle;
            mNegativeListener = listener;
            return this;
        }

        public DialogBuilder setNegativeButton(int buttonTitle, OnClickListener listener) {
            return setNegativeButton(mContext.getText(buttonTitle), listener);
        }

        public DialogBuilder setNeutralButton(CharSequence buttonTitle,
                                              OnClickListener listener) {
            mNeutralButton = buttonTitle;
            mNeutralListener = listener;
            return this;
        }

        public DialogBuilder setNeutralButton(int buttonTitle, OnClickListener listener) {
            return setNeutralButton(mContext.getText(buttonTitle), listener);
        }

        public DialogBuilder setBodyLayoutId(final int viewStubLayoutId) {
            this.mViewStubLayoutId = viewStubLayoutId;
            return this;
        }

        public DialogBuilder setOnBodyInflatedListener(OnBodyInflatedListener listener) {
            mOnBodyInflatedListener = listener;
            return this;
        }

        public DialogBuilder setOnValidateValuesBeforeDismissListener(OnValidateValuesBeforeDismissListener listener) {
            mOnValidateValuesBeforeDismissListener = listener;
            return this;
        }

        public AlertDialogFragment build() {
            AlertDialogFragment dialog = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putCharSequence(EXTRA_TITLE, mTitle);
            args.putCharSequence(EXTRA_MESSAGE, mMessage);
            args.putInt(EXTRA_VIEW_STUB_LAYOUT_ID, mViewStubLayoutId);
            args.putCharSequence(EXTRA_POSITIVE_BUTTON, mPositiveButton);
            args.putCharSequence(EXTRA_NEGATIVE_BUTTON, mNegativeButton);
            args.putCharSequence(EXTRA_NEUTRAL_BUTTON, mNeutralButton);
            dialog.setArguments(args);

            dialog.mPositiveListener = mPositiveListener;
            dialog.mNegativeListener = mNegativeListener;
            dialog.mNeutralListener = mNeutralListener;
            dialog.mBodyInflatedListener = mOnBodyInflatedListener;
            dialog.mValidateValuesBeforeDismissListener = mOnValidateValuesBeforeDismissListener;
            return dialog;
        }
    }
}
