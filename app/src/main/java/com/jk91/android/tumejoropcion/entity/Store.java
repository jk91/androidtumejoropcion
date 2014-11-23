package com.jk91.android.tumejoropcion.entity;

/**
 * Created by jk91 on 14-11-22.
 */
public class Store {

    private String login;
    private int numMencionesLink;
    private int numMenciones;

    /*public Store(String login, int numMencionesLink, int numMenciones) {
        this.login = login;
        this.numMencionesLink = numMencionesLink;
        this.numMenciones = numMenciones;
    } */

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public int getNumMencionesLink() { return numMencionesLink; }

    public void setNumMencionesLink(int numMencionesLink) { this.numMencionesLink = numMencionesLink; }

    public int getNumMenciones() { return numMenciones; }

    public void setNumMenciones(int numMenciones) { this.numMenciones = numMenciones; }
}