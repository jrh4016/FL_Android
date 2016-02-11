package com.skeds.android.phone.business.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ServiceCall implements Parcelable {

    public static final String KEY_SERVICE_CALL = "serviceCall";
    protected static final String KEY_SERVICE_CALL_ID = "id";
    protected static final String KEY_SERVICE_CALL_DESCRIPTION = "serviceCallDescription";
    protected static final String KEY_SERVICE_CALL_TECHNICIAN = "serviceCallTechnician";
    protected static final String KEY_SERVICE_CALL_TECHNICIAN_ID = "id";
    protected static final String KEY_SERVICE_CALL_DATE = "serviceCallDate";
    protected static final String KEY_SERVICE_CALL_EQUIPMENT_CONDITION = "equipmentCondition";


    public static final Parcelable.Creator<ServiceCall> CREATOR = new Parcelable.Creator<ServiceCall>() {
        @Override
        public ServiceCall[] newArray(int size) {
            return new ServiceCall[size];
        }

        @Override
        public ServiceCall createFromParcel(Parcel source) {
            return new ServiceCall(source);
        }
    };

    private long id;
    private String description;
    private long technicianId;
    private String technicianName;
    private String date;
    private String condition;

    public ServiceCall() {
    }

    public ServiceCall(Parcel source) {
        id = source.readLong();
        description = source.readString();
        technicianId = source.readLong();
        technicianName = source.readString();
        date = source.readString();
        condition = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(description);
        dest.writeLong(technicianId);
        dest.writeString(technicianName);
        dest.writeString(date);
        dest.writeString(condition);
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String value) {
        this.condition = value;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long value) {
        id = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        description = value;
    }

    public long getTechnicianId() {
        return this.technicianId;
    }

    public void setTechnicianId(long value) {
        technicianId = value;
    }

    public String getTechnicianName() {
        return this.technicianName;
    }

    public void setTechnicianName(String value) {
        technicianName = value;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String value) {
        date = value;
    }
}