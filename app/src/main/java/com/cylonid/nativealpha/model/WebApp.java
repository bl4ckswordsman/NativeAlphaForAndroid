package com.cylonid.nativealpha.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WebApp implements Parcelable {
    private boolean passwordAutofillEnabled;

    public WebApp() {
        // Default constructor
    }

    public boolean isPasswordAutofillEnabled() {
        return passwordAutofillEnabled;
    }

    public void setPasswordAutofillEnabled(boolean passwordAutofillEnabled) {
        this.passwordAutofillEnabled = passwordAutofillEnabled;
    }

    protected WebApp(Parcel in) {
        passwordAutofillEnabled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (passwordAutofillEnabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WebApp> CREATOR = new Creator<WebApp>() {
        @Override
        public WebApp createFromParcel(Parcel in) {
            return new WebApp(in);
        }

        @Override
        public WebApp[] newArray(int size) {
            return new WebApp[size];
        }
    };
}
