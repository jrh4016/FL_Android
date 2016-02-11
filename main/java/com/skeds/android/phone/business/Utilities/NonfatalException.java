package com.skeds.android.phone.business.Utilities;

public class NonfatalException extends Exception {
    private static final long serialVersionUID = 3940128822751647586L;

    public final String tag;
    boolean logged = false;

    public NonfatalException(String tag, String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.tag = tag;
        Logger.err(tag, this);
    }

    public NonfatalException(String tag, String detailMessage) {
        super(detailMessage);
        this.tag = tag;
        Logger.err(tag, this);
    }

}
