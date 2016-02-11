package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import java.io.Serializable;

public class ServiceProvider implements Serializable {
    private int id;
    private String name;
    private String status;
    private String email;
    private boolean clockedIn;
    private String clockInTime;

    public ParticipantType participant;
    public TimesWorked todayTimesWorked;
    public TimesWorked thisweekTimesWorked;
    public TimesWorked lastweekTimesWorked;
    public TimesWorked todaysTimeClockRecords;
    public TimesWorked thisweekTimeClockRecords;
    public TimesWorked lastweekTimeClockRecords;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return this.email;
    }

    public void setId(int value) {
        id = value;
    }

    public void setName(String value) {
        name = value;
    }

    public void setStatus(String value) {
        status = value;
    }

    public void setEmail(String value) {
        email = value;
    }

    public ServiceProvider() {
        setId(0);
        setName("");
        setStatus("");
        setEmail("");
        setClockedIn(false);
        setClockInTime("");

        participant = new ParticipantType();
        todayTimesWorked = new TimesWorked();
        thisweekTimesWorked = new TimesWorked();
        lastweekTimesWorked = new TimesWorked();
        todaysTimeClockRecords = new TimesWorked();
        thisweekTimeClockRecords = new TimesWorked();
        lastweekTimeClockRecords = new TimesWorked();
    }

    public boolean isClockedIn() {
        return clockedIn;
    }

    public void setClockedIn(boolean value) {
        this.clockedIn = value;
    }

    public String getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(String value) {
        this.clockInTime = value;
    }
}