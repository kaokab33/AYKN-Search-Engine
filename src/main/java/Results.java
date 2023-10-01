package com.example;

public class Results {
    public String h;
    public String p;
    public String l;
    public String timo;
    public Results (String link,String h, String p)
    {
        this.l=link;
        this.h=h;
        this.p =p;
    }

    public String getH() {
        return h;
    }

    public String getP() {
        return p;
    }

    public void setTimo(String timo) {
        this.timo = timo;
    }
}