package com.skeds.android.phone.business.core.async;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Base {@link android.os.Handler} implementation designed for handling background tasks during fragment
 * lifecycle.
 */
public abstract class BaseWorkerHandler extends Handler {

    private volatile boolean mStoppedOutside;
    private final int[] mSupportedMessages;

    public BaseWorkerHandler(Looper looper, int... supportedMessages) {
        super(looper);
        // if supported messages are not provided we check for default 0 message id
        mSupportedMessages = supportedMessages.length > 0 ? supportedMessages : new int[]{0};
    }

    @Override
    abstract public void handleMessage(Message msg);

    /**
     * Performs attempt to quit looper associated with this handler and stop the worker thread. Will
     * succeed only if it was stopped outside first and there were no pending supported messages in
     * a queue. If method called from the {@link #handleMessage(android.os.Message)} first the handler
     * continues running until attempt to quit comes outside of the handler. If method called
     * outside of the handler but any of supported messages pending in a queue handler will try to
     * quit later when this method called with <code>outside == false</code>
     *
     * @param outside must be <code>true</code> if stopped outside from the handler, <code>false</code>
     *                handler stopped from the {@link #handleMessage(android.os.Message)} of the handler
     *                implementation
     * @return <code>true</code> if handler succeed to quit. Otherwise it returns <code>false</code>
     */
    public boolean tryQuit(boolean outside) {
        if (outside) {
            mStoppedOutside = true;
        }
        // stops looper only if handler were stopped outside (from #onDestroy method of
        // fragment) and there are no pending messages in the queue.
        if (mStoppedOutside && !hasPendingMessages()) {
            getLooper().quit();
            return true;
        }
        return false;
    }

    /**
     * Check if there are any pending posts of messages with one of the 'supportedMessages' in the
     * message queue.
     *
     * @return <code>true</code> if at least one of the supported message ids pending in the message
     * queue. <code>false</code> if message queue doesn't hold any of the supported message
     * ids.
     */
    protected final boolean hasPendingMessages() {
        boolean hasPendingMessage = false;
        for (int message : mSupportedMessages) {
            if (hasMessages(message)) {
                hasPendingMessage = true;
                break;
            }
        }
        return hasPendingMessage;
    }

    /**
     * Convenient worker handler message payload that supports {@link #onFinished()} and
     * {@link #onError(Exception)} callbacks to notify message sender that payload processing
     * finished successfully or not. Instance of this class can be used as {@link android.os.Message#obj}
     * message parameter.
     * <p/>
     * <b>NOTE</b>: {@link BaseWorkerHandler} doesn't require that all messages must be payloaded
     * with the instance of this class. If you don't need to handle finish state of payload
     * processing, your message can contain any {@link android.os.Message#obj} payload as you need. You're also
     * free to decide how to call the finish callbacks in the subclass implementation of
     * {@link BaseWorkerHandler} (in MainLooper or in the looper of current worker handler).
     *
     * @param <T> type of the request payload that must be processed by the worker handler
     */
    public static class WorkerHandlerRequest<T> implements Parcelable {

        private final T mData;
        private final Handler mReplyToHandler;

        /**
         * Creates an instance of handler request.
         *
         * @param data request payload
         */
        public WorkerHandlerRequest(T data) {
            this(data, null);
        }

        /**
         * Creates an instance of handler request that receives {@link #onFinished()} and
         * {@link #onError(Exception)} callbacks to the thread associated with given replyToHandler.
         *
         * @param data           request payload
         * @param replyToHandler handler associated with the thread in which {@link #onFinished()} and
         *                       {@link #onError(Exception)} callbacks executed.
         */
        public WorkerHandlerRequest(T data, Handler replyToHandler) {
            mData = data;
            mReplyToHandler = replyToHandler;
        }

        /**
         * This method can be called to notify requestor that data processing is finished. If
         * <code>replayToHandler</code> specified in constructor then {@link #onFinished()} will be
         * executed in the thread of this replyToHandler. Otherwise it will be called in the same
         * thread the worker handler associated with.
         */
        public final void postFinish() {
            if (mReplyToHandler != null) {
                mReplyToHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFinished();
                    }
                });
            } else {
                onFinished();
            }
        }

        /**
         * This method can be called to notify requestor that some error occured during data
         * processing. If <code>replayToHandler</code> specified in constructor then
         * {@link #onError(Exception)} will be executed in the thread of this replyToHandler.
         * Otherwise it will be called in the same thread the worker handler associated with.
         *
         * @param e exception occured while processing request payload
         */
        public final void postError(final Exception e) {
            if (mReplyToHandler != null) {
                mReplyToHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(e);
                    }
                });
            } else {
                onError(e);
            }
        }

        /**
         * Returns worker handler request payload that can be processed in
         * {@link BaseWorkerHandler#handleMessage(android.os.Message)}.
         *
         * @return payload of the given request
         */
        public final T getData() {
            return mData;
        }

        /**
         * Callback that is called from {@link BaseWorkerHandler#postFinish()} when data processing
         * finished.
         */
        public void onFinished() {
            // NO-OP
        }

        /**
         * Callback that is called from {@link BaseWorkerHandler#postError(Exception)} when any
         * exception occured while processing request payload.
         *
         * @param e exception occured while processing request payload
         */
        public void onError(Exception e) {
            // NO-OP
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }
}
