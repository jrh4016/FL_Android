package com.skeds.android.phone.business.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.skeds.android.phone.business.R;

/**
 * Relative layout with predefined checkbox that can be safely used as a row layout of the ListView.
 *
 * @attr ref dk.releaze.coop.generic.R.styleable#CheckableRelativeLayout_checked
 * @attr ref dk.releaze.coop.generic.R.styleable#CheckableRelativeLayout_checkMark
 * @attr ref dk.releaze.coop.generic.R.styleable#CheckableRelativeLayout_checkMarkGravity
 */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    public static final int CHECK_MARK_LEFT = 0x1;
    public static final int CHECK_MARK_RIGHT = 0x2;
    public static final int CHECK_MARK_CENTER_VERTICAL = 0x4;
    public static final int CHECK_MARK_BOTTOM = 0x8;
    public static final int CHECK_MARK_TOP = 0x10;

    private static final int DEFAULT_GRAVITY = CHECK_MARK_LEFT | CHECK_MARK_CENTER_VERTICAL;

    private CheckBox mCheckBox;
    // area that holds checkbox in coordinates of current layout
    private final Rect mCheckBoxHitRect = new Rect();

    private int mCheckBoxGravity = DEFAULT_GRAVITY;

    public CheckableRelativeLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        // read view attributes
        TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.CheckableRelativeLayout);
        // resource id being set as checkbox button
        int mCheckMarkDrawableId = attributes.getResourceId(
                R.styleable.CheckableRelativeLayout_checkMark, -1);
        // flag indicating if checkbox button is checked or not when inflating view
        boolean checked = attributes.getBoolean(R.styleable.CheckableRelativeLayout_checked, false);

        mCheckBoxGravity = attributes.getInt(R.styleable.CheckableRelativeLayout_checkMarkGravity, DEFAULT_GRAVITY);

        attributes.recycle();

        // define checkbox position in the current layout

        // initialize the check box button
        mCheckBox = new CheckBox(context);
        mCheckBox.setId(R.id.checkbox);
        if (mCheckMarkDrawableId > 0) {
            mCheckBox.setButtonDrawable(mCheckMarkDrawableId);
        }
        mCheckBox.setChecked(checked);
        // add the checkbox to layout with default layout params
        // checkbox will be layouted before onMeasure
        addView(mCheckBox, generateDefaultLayoutParams());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mCheckBox.setEnabled(enabled);
    }

    @Override
    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public void setCheckMarkDrawable(Drawable d) {
        mCheckBox.setButtonDrawable(d);
    }

    public void setCheckMarkDrawable(int resId) {
        mCheckBox.setButtonDrawable(resId);
    }

    public void setCheckMarkGravity(int gravity) {
        if (gravity > 0 && gravity != mCheckBoxGravity) {
            mCheckBoxGravity = gravity;
            requestLayout();
        }
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mCheckBox.setOnCheckedChangeListener(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setupCheckMarkLayoutParams((LayoutParams) mCheckBox.getLayoutParams());

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // define rectangle that holds check box button
        mCheckBox.getHitRect(mCheckBoxHitRect);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int touchX = Math.round(ev.getX());
        int touchY = Math.round(ev.getY());
        boolean intercept;
        // if touch event occurred on the check box we delegate the event to its parent
        // otherwise process event as usual
        if (mCheckBoxHitRect.contains(touchX, touchY)) {
            intercept = true;
        } else {
            intercept = super.onInterceptTouchEvent(ev);
        }
        return intercept;
    }

    private void setupCheckMarkLayoutParams(LayoutParams params) {

        boolean isLeft = (mCheckBoxGravity & CHECK_MARK_LEFT) != 0;

        // left gravity has the highest priority if concurent flags set or not set at all
        if (!isLeft && (mCheckBoxGravity & CHECK_MARK_RIGHT) != 0) {
            params.addRule(ALIGN_PARENT_RIGHT);
        } else {
            params.addRule(ALIGN_PARENT_LEFT);
        }

        boolean isCenter = (mCheckBoxGravity & CHECK_MARK_CENTER_VERTICAL) != 0;
        boolean isTop = (mCheckBoxGravity & CHECK_MARK_TOP) != 0;
        // center_vertical gravity has the highest priority if concurent flags set or noghing is set
        // top gravity has higher priority than bottom
        // bottom gravity has the lowest priority in case concurent flags set
        if (!isCenter && !isTop && (mCheckBoxGravity & CHECK_MARK_BOTTOM) != 0) {
            params.addRule(ALIGN_PARENT_BOTTOM);
        } else if (!isCenter && isTop) {
            params.addRule(ALIGN_PARENT_TOP);
        } else {
            params.addRule(CENTER_VERTICAL);
        }
    }
}
