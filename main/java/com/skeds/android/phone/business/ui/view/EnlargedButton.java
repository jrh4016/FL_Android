package com.skeds.android.phone.business.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;

public class EnlargedButton extends Button {

    private static final int TOUCH_ADDITION = 20;

    private int mPreviousWidth = -1;
    private int mPreviousHeight = -1;

    private final Rect mDelegateArea = new Rect();

    public EnlargedButton(final Context context) {
        this(context, null);
    }

    public EnlargedButton(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnlargedButton(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        final int width = r - l;
        final int height = b - t;

        /*
         * We can't use onSizeChanged here as this is called before the layout
         * of child View is actually done ... Because we need the size of some
         * child children we need to check for View size change manually
         */
        if (width != mPreviousWidth || height != mPreviousHeight) {

            mPreviousWidth = width;
            mPreviousHeight = height;

            // The hit rectangle for the ImageButton
            getHitRect(mDelegateArea);

            // Extend the touch area of the ImageButton beyond its bounds.
            mDelegateArea.inset(-TOUCH_ADDITION, -TOUCH_ADDITION);

            final TouchDelegate touchDelegate = new TouchDelegate(mDelegateArea, this);
            // Sets the TouchDelegate on the parent view, such that touches
            // within the touch delegate bounds are routed to the child.
            if (View.class.isInstance(getParent())) {
                ((View) getParent()).setTouchDelegate(touchDelegate);
            }
        }
    }
}
