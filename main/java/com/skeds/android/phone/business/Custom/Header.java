package com.skeds.android.phone.business.Custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppSettingsUtilities;
import com.skeds.android.phone.business.Utilities.General.Constants;

public class Header extends LinearLayout {

    private TextView leftButton;
    private TextView rightButton;

    public Header(Context context) {
        super(context);

        if (isInEditMode()) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.header_layout_standard, this);

        if (AppSettingsUtilities.isBetaServerMode()) {
            final ImageView logo = (ImageView) findViewById(R.id.header_imageview_logo);
            logo.setImageResource(R.drawable.fieldlocate_logo_beta);
        }
    }

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }

        TypedArray styleAttributes = context.obtainStyledAttributes(attrs,
                R.styleable.Header);

        switch (AppSettingsUtilities.getApplicationMode()) {
            case Constants.APPLICATION_MODE_PHONE_SERVICE:
                setupPhoneHeader(context, styleAttributes);
                break;
            case Constants.APPLICATION_MODE_TABLET_7_SERVICE:
                // TODO - Blank
                break;
            case Constants.APPLICATION_MODE_TABLET_101_SERVICE:
                setupLargeTabletHeader(context, styleAttributes);
                break;
            default:
                // Nothing
                break;
        }

    }

    private void setupPhoneHeader(Context context, TypedArray styleAttributes) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int buttonLeftType = styleAttributes.getInt(
                R.styleable.Header_left_button_type, 0);
        int buttonRightType = styleAttributes.getInt(
                R.styleable.Header_right_button_type, 0);
        int navType = styleAttributes.getInt(R.styleable.Header_nav_type, 0);

		/* Action buttons across the top */
        int userButton = styleAttributes.getInt(R.styleable.Header_user_button,
                0);
        int backButton = styleAttributes.getInt(R.styleable.Header_back_button,
                0);

        switch (navType) {
            case HeaderTypes.NAVIGATION_TYPE_NONE:
            case HeaderTypes.NAVIGATION_TYPE_STANDARD_EXTENDED:

                if (navType == HeaderTypes.NAVIGATION_TYPE_NONE)
                    inflater.inflate(R.layout.header_layout_standard, this);
                else if (navType == HeaderTypes.NAVIGATION_TYPE_STANDARD_EXTENDED)
                    inflater.inflate(
                            R.layout.header_layout_nav_standard_with_blue_gradient,
                            this);

                rightButton = (TextView) findViewById(R.id.header_standard_button_right);
                leftButton = (TextView) findViewById(R.id.header_standard_button_left);

                switch (buttonLeftType) {
                    case HeaderTypes.BUTTON_TYPE_NONE:
                        leftButton.setVisibility(View.GONE);
                        break;

                    case HeaderTypes.BUTTON_TYPE_SAVE:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_save));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SERVICE_CALL:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_service_record));
                        break;

                    case HeaderTypes.BUTTON_TYPE_EDIT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_edit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD_COMMENTS:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_comment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEW_APPOINTMENT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_new_appointment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_GO_BACK:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_go_back));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEXT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_next));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SUBMIT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_submit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SWIPE_CREDIT_CARD:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_swipe_card));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_ESTIMATE:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_estimate));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_LOCATION:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_location));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_AGREEMENT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_agreement));
                        break;
                    case HeaderTypes.BUTTON_TYPE_NEW_EQUIPMENT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_new_equipment));
                        break;
                    case HeaderTypes.BUTTON_TYPE_CUSTOMER_VIEW:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_customer_view));
                        break;
                    default:
                        // Nothing
                        break;
                }

                switch (buttonRightType) {
                    case HeaderTypes.BUTTON_TYPE_NONE:
                        rightButton.setVisibility(View.GONE);
                        break;

                    case HeaderTypes.BUTTON_TYPE_SAVE:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_save));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SERVICE_CALL:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_service_record));
                        break;

                    case HeaderTypes.BUTTON_TYPE_EDIT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_edit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD_COMMENTS:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_comment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEW_APPOINTMENT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_new_appointment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_GO_BACK:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_go_back));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEXT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_next));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SUBMIT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_submit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SWIPE_CREDIT_CARD:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_swipe_card));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_ESTIMATE:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_estimate));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_LOCATION:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_location));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_AGREEMENT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_agreement));
                        break;
                    case HeaderTypes.BUTTON_TYPE_NEW_EQUIPMENT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_new_equipment));
                        break;
                    case HeaderTypes.BUTTON_TYPE_NEW:
                        rightButton.setText(getResources().getString(
                                R.string.new_title));
                        break;
                    default:
                        // Nothing
                        break;
                }
                break;
            case HeaderTypes.NAVIGATION_TYPE_MYHOURS:
                inflater.inflate(R.layout.header_layout_nav_hours_worked, this);
                break;

            case HeaderTypes.NAVIGATION_TYPE_CUSTOMER:
                inflater.inflate(R.layout.header_layout_nav_customer, this);
                break;

            case HeaderTypes.NAVIGATION_TYPE_SEARCH:
                inflater.inflate(R.layout.header_layout_search, this);
                break;

            case HeaderTypes.NAVIGATION_TYPE_APPOINTMENT:
                inflater.inflate(R.layout.header_layout_nav_appointment, this);
                break;
            case HeaderTypes.NAVIGATION_TYPE_CUSTOMER_SEARCH:
                inflater.inflate(R.layout.header_layout_customer_search, this);
                break;
            default:
                // Nothing
                break;
        }

        // Whether or not to display the "User" button at the top-right
        ImageView userImage = (ImageView) findViewById(R.id.header_button_user);
        if (userButton == 0) {
            userImage.setVisibility(View.GONE);
        } else {
            userImage.setVisibility(View.VISIBLE);
        }

        // Whether or not to display the "Back" button at the top-right
        ImageView backImage = (ImageView) findViewById(R.id.header_button_back);
        if (backButton == 0) {
            backImage.setVisibility(View.GONE);
        } else {
            backImage.setVisibility(View.VISIBLE);
        }

        if (AppSettingsUtilities.isBetaServerMode()) {
            ImageView logo = (ImageView) findViewById(R.id.header_imageview_logo);
            logo.setImageResource(R.drawable.fieldlocate_logo_beta);
        }
    }

    private void setupLargeTabletHeader(Context context,
                                        TypedArray styleAttributes) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int buttonLeftType = styleAttributes.getInt(
                R.styleable.Header_left_button_type, 0);
        int buttonRightType = styleAttributes.getInt(
                R.styleable.Header_right_button_type, 0);
        int navType = styleAttributes.getInt(R.styleable.Header_nav_type, 0);

		/* Action buttons across the top */
        int userButton = styleAttributes.getInt(R.styleable.Header_user_button,
                0);
        int backButton = styleAttributes.getInt(R.styleable.Header_back_button,
                0);

        switch (navType) {
            case HeaderTypes.NAVIGATION_TYPE_NONE:
            case HeaderTypes.NAVIGATION_TYPE_STANDARD_EXTENDED:

                if (navType == HeaderTypes.NAVIGATION_TYPE_NONE)
                    inflater.inflate(R.layout.header_layout_standard, this);
                else if (navType == HeaderTypes.NAVIGATION_TYPE_STANDARD_EXTENDED)
                    inflater.inflate(
                            R.layout.header_layout_nav_standard_with_blue_gradient,
                            this);

                rightButton = (TextView) findViewById(R.id.header_standard_button_right);
                leftButton = (TextView) findViewById(R.id.header_standard_button_left);

                switch (buttonLeftType) {
                    case HeaderTypes.BUTTON_TYPE_NONE:
                        leftButton.setVisibility(View.GONE);
                        break;

                    case HeaderTypes.BUTTON_TYPE_SAVE:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_save));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SERVICE_CALL:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_service_record));
                        break;

                    case HeaderTypes.BUTTON_TYPE_EDIT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_edit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD_COMMENTS:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_comment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEW_APPOINTMENT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_new_appointment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_GO_BACK:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_go_back));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEXT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_next));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SUBMIT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_submit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SWIPE_CREDIT_CARD:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_swipe_card));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_ESTIMATE:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_estimate));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_LOCATION:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_location));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_AGREEMENT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_add_agreement));
                        break;
                    case HeaderTypes.BUTTON_TYPE_NEW_EQUIPMENT:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_new_equipment));
                        break;
                    case HeaderTypes.BUTTON_TYPE_CUSTOMER_VIEW:
                        leftButton.setText(getResources().getString(
                                R.string.button_string_customer_view));
                        break;
                    default:
                        // Nothing
                        break;
                }

                switch (buttonRightType) {
                    case HeaderTypes.BUTTON_TYPE_NONE:
                        rightButton.setVisibility(View.GONE);
                        break;

                    case HeaderTypes.BUTTON_TYPE_SAVE:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_save));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SERVICE_CALL:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_service_record));
                        break;

                    case HeaderTypes.BUTTON_TYPE_EDIT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_edit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_ADD_COMMENTS:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_comment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEW_APPOINTMENT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_new_appointment));
                        break;

                    case HeaderTypes.BUTTON_TYPE_GO_BACK:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_go_back));
                        break;

                    case HeaderTypes.BUTTON_TYPE_NEXT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_next));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SUBMIT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_submit));
                        break;

                    case HeaderTypes.BUTTON_TYPE_SWIPE_CREDIT_CARD:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_swipe_card));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_ESTIMATE:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_estimate));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_LOCATION:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_location));
                        break;
                    case HeaderTypes.BUTTON_TYPE_ADD_AGREEMENT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_add_agreement));
                        break;
                    case HeaderTypes.BUTTON_TYPE_NEW_EQUIPMENT:
                        rightButton.setText(getResources().getString(
                                R.string.button_string_new_equipment));
                        break;
                    case HeaderTypes.BUTTON_TYPE_NEW:
                        rightButton.setText(getResources().getString(
                                R.string.new_title));
                        break;
                    default:
                        // Nothing
                        break;
                }

                break;
            case HeaderTypes.NAVIGATION_TYPE_MYHOURS:
                inflater.inflate(R.layout.header_layout_nav_hours_worked, this);
                break;

            case HeaderTypes.NAVIGATION_TYPE_CUSTOMER:
                inflater.inflate(R.layout.header_layout_nav_customer, this);
                break;

            case HeaderTypes.NAVIGATION_TYPE_SEARCH:
                inflater.inflate(R.layout.header_layout_search, this);
                break;

            case HeaderTypes.NAVIGATION_TYPE_APPOINTMENT:
                inflater.inflate(R.layout.header_layout_nav_appointment, this);
                break;
            case HeaderTypes.NAVIGATION_TYPE_CUSTOMER_SEARCH:
                inflater.inflate(R.layout.header_layout_customer_search, this);
                break;
            default:
                // Nothing
                break;
        }

        // Whether or not to display the "User" button at the top-right
        ImageView userImage = (ImageView) findViewById(R.id.header_button_user);
        if (userButton == 0) {
            userImage.setVisibility(View.GONE);
        } else {
            userImage.setVisibility(View.VISIBLE);
            userImage
                    .setImageResource(R.drawable.custom_action_bar_button_user);
        }

        // Whether or not to display the "Back" button at the top-right
        ImageView backImage = (ImageView) findViewById(R.id.header_button_back);
        if (backButton == 0) {
            backImage.setVisibility(View.GONE);
        } else {
            backImage.setVisibility(View.VISIBLE);
            backImage
                    .setImageResource(R.drawable.custom_action_bar_button_back);
        }

        if (AppSettingsUtilities.isBetaServerMode()) {
            ImageView logo = (ImageView) findViewById(R.id.header_imageview_logo);
            logo.setImageResource(R.drawable.fieldlocate_logo_beta);
        }
    }
}