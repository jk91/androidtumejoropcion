package com.jk91.android.tumejoropcion.entity;

/**
 * Created by jk91 on 14-11-21.
 */
public class Store {

    private String name;
    private int mentions;

    public Store (String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getMentions() { return mentions; }

    public void setMentions() { this.mentions = mentions;}
}
