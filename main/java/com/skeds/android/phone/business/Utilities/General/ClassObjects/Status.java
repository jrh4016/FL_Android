package com.skeds.android.phone.business.Utilities.General.ClassObjects;

public enum Status {
    ON_ROUTE("On Route"), START_APPOINTMENT("Working"), SUSPEND_APPOINTMENT(
            "Paused"), RESTART_APPOINTMENT("Working"), FINISH_APPOINTMENT(
            "Finished"), MOVE_APPOINTMENT("Move Appointment"), CLOSE_APPOINTMENT(
            "Close Appointment"), NOT_STARTED("Not Started"), PARTS_RUN_APPOINTMENT(
            "Parts Run");

    private String value;

    private Status(String v) {
        value = v;
    }

    public String getValue() {
        return value;
    }

    public static final String COLOR_ON_ROUTE = "#3E5165";
    public static final String COLOR_START_APPOINTMENT = "#8CC63F";
    public static final String COLOR_SUSPEND_APPOINTMENT = "#A84837";
    public static final String COLOR_RESTART_APPOINTMENT = "#8CC63F";
    public static final String COLOR_FINISH_APPOINTMENT = "#5A5A5A";
    public static final String COLOR_MOVE_APPOINTMENT = "#000000";
    public static final String COLOR_CLOSE_APPOINTMENT = "#000000";
    public static final String COLOR_NOT_STARTED = "#808080";
    public static final String COLOR_PARTS_RUN = "#FFD554";
}