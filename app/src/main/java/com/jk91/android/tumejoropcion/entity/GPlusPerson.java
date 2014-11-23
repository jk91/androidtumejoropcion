package com.jk91.android.tumejoropcion.entity;

/**
 * Created by jk91 on 14-11-21.
 */
public class GPlusPerson {

    private String idSocial;
    private String displayName;
    private String tipoCuenta;
    private Store[] tiendasMencionadas;

    public String getIdSocial() { return idSocial; }

    public void setIdSocial(String idSocial) { this.idSocial = idSocial;}

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getTipoCuenta() { return tipoCuenta; }

    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public Store[] getTiendasMencionadas() { return tiendasMencionadas; }

    public void setTiendasMencionadas(Store[] tiendasMencionadas) { this.tiendasMencionadas = tiendasMencionadas; }
}
