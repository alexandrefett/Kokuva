package com.kokuva.model;

import java.util.Date;

/**
 * Created by Alexandre on 16/10/2016.
 */

public class Photo {
    private String file;
    private String mName;
    private String mUid;
    private Date mTimestamp;

    public Photo() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public Date getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(Date mTimestamp) {
        this.mTimestamp = mTimestamp;
    }
}
