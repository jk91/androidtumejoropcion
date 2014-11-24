package com.jk91.android.tumejoropcion.entity;

import java.util.List;

/**
 * Created by jk91 on 14-11-24.
 */
public class RequestAmigos {

    private String googleId;
    private String acountType;
    private List<Friend> friends;

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getAcountType() {
        return acountType;
    }

    public void setAcountType(String acountType) {
        this.acountType = acountType;
    }

    public List getFriends() {
        return friends;
    }

    public void setFriends(List friends) {
        this.friends = friends;
    }



}
