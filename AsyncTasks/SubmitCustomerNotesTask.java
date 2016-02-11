package com.skeds.android.phone.business.AsyncTasks;

import android.app.Activity;

import com.skeds.android.phone.business.Utilities.REST.Objects.RESTCustomer;

public class SubmitCustomerNotesTask extends BaseUiReportTask<String> {
    /* Variables to send off */
    private int customerId;
    private String notes;

    public SubmitCustomerNotesTask(Activity activity) {
        super(activity, "Submitting Notes...");
    }

    @Override
    protected boolean taskBody(final String... args) throws Exception {
        RESTCustomer.addNotes(customerId, notes);
        return true;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}