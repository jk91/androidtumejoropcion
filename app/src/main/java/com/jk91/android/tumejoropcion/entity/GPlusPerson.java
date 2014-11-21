package com.jk91.android.tumejoropcion.entity;

import java.util.List;

/**
 * Created by jk91 on 14-11-21.
 */
public class GPlusPerson {

    private String id;
    private String name;
    private String mail;
    private List<String> mentions;

    public GPlusPerson(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() { return mail; }

    public void setMail(String mail) { this.mail = mail; }

    public List getMentions() {
        return mentions;
    }

    public void addMention(String mention) {
        mentions.add(mention);
    }
}
