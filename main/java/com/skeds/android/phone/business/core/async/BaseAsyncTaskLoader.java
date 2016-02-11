package com.skeds.android.phone.business.core.async;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class BaseAsyncTaskLoader<T> extends AsyncTaskLoader<T> {

    protected Exception mLoadException;

    private T mResult;

    public BaseAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        T oldData = mResult;
        mResult = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mResult != null) {
            deliverResult(mResult);
        }

        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mResult != null) {
            releaseResources(mResult);
            mResult = null;
        }

        mLoadException = null;
    }

    protected void releaseResources(T data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    public Exception getLoadException() {
        return mLoadException;
    }
}
