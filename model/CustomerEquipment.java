package com.skeds.android.phone.business.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CustomerEquipment implements Parcelable {

    public static final String KEY_CUSTOMER = "customer";

    protected static final String KEY_ID = "id";
    protected static final String KEY_ALLOW_EQUIPMENT_EDIT = "allowEquipmentEdit";
    protected static final String KEY_ALLOW_EQUIPMENT_ADD = "allowEquipmentAdd";
    protected static final String KEY_EQUIPMENT_LIST = "equipmentList";

    public static final Parcelable.Creator<CustomerEquipment> CREATOR = new Parcelable.Creator<CustomerEquipment>() {
        @Override
        public CustomerEquipment[] newArray(int size) {
            return new CustomerEquipment[size];
        }

        @Override
        public CustomerEquipment createFromParcel(Parcel source) {
            return new CustomerEquipment(source);
        }
    };

    private long id;
    private boolean allowEquipmentEdit;
    private boolean allowEquipmentAdd;
    private List<Equipment> equipmentList;

    public CustomerEquipment() {
    }

    public CustomerEquipment(Parcel source) {
        id = source.readLong();
        allowEquipmentEdit = source.readInt() == 1;
        allowEquipmentAdd = source.readInt() == 1;
        equipmentList = new ArrayList<Equipment>();
        source.readList(equipmentList, getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(allowEquipmentEdit ? 1 : 0);
        dest.writeInt(allowEquipmentAdd ? 1 : 0);
        dest.writeList(equipmentList);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long value) {
        id = value;
    }

    public boolean getAllowEquipmentEdit() {
        return this.allowEquipmentEdit;
    }

    public void setAllowEquipmentEdit(boolean value) {
        allowEquipmentEdit = value;
    }

    public boolean getAllowEquipmentAdd() {
        return this.allowEquipmentAdd;
    }

    public void setAllowEquipmentAdd(boolean value) {
        allowEquipmentAdd = value;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    public Equipment getEquipmentById(int equid) {
        if (equipmentList != null)
            for (Equipment equip : equipmentList)
                if (equip.getId() == equid)
                    return equip;
        return null;
    }
}