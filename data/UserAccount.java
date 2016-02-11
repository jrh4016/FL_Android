package com.skeds.android.phone.business.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class UserAccount implements Parcelable {

    public static final Parcelable.Creator<UserAccount> CREATOR = new Parcelable.Creator<UserAccount>() {
        @Override
        public UserAccount[] newArray(int size) {
            return new UserAccount[size];
        }

        @Override
        public UserAccount createFromParcel(Parcel source) {
            return new UserAccount(source);
        }
    };

    private long userId;
    private String socialId;
    private String password;
    private String email;
    private String userName;
    private String zipCode;
    private Date birthday;
    private boolean dirty;
    private boolean facebookAccount;
    // identify whether the user logs in first time or not. Date will represent last time when
    // user logs into application
    private String lastTimeUserUpdate;


    public UserAccount() {

    }

    private UserAccount(Parcel source) {
        userId = source.readLong();
        socialId = source.readString();
        password = source.readString();
        email = source.readString();
        userName = source.readString();
        zipCode = source.readString();
        dirty = source.readInt() != 0;
        facebookAccount = source.readInt() != 0;
        long timestamp = source.readLong();
        if (timestamp != -1) {
            birthday = new Date(timestamp);
        }
        lastTimeUserUpdate = source.readString();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isFacebookAccount() {
        return facebookAccount;
    }

    public void setFacebookAccount(boolean facebookAccount) {
        this.facebookAccount = facebookAccount;
    }

    public String getLastTimeUserUpdate() {
        return lastTimeUserUpdate;
    }

    public void setLastTimeUserUpdate(String lastTimeUserUpdate) {
        this.lastTimeUserUpdate = lastTimeUserUpdate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(userName).append("[email=").append(email).append("]");
        return builder.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(socialId);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(userName);
        dest.writeString(zipCode);
        dest.writeInt(dirty ? 1 : 0);
        dest.writeInt(facebookAccount ? 1 : 0);
        dest.writeLong(birthday != null ? birthday.getTime() : -1);
        dest.writeString(lastTimeUserUpdate);
    }

}
