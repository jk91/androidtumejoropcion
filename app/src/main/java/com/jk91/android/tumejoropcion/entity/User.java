package com.jk91.android.tumejoropcion.entity;

/**
 * Created by jk91 on 14-11-24.
 */
public class User {

    private String id;
    private String displayName;
    private String acountType;

    public String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAcountType() {
        return acountType;
    }

    public void setAcountType(String accountType) {
        this.acountType = accountType;
    }
}
