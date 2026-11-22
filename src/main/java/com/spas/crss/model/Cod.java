package com.spas.crss.model;

public class Cod {

    private String codId;
    private String name;
    private String email;

    public Cod() {
    }

    public Cod(String codId, String name, String email) {
        this.codId = codId;
        this.name = name;
        this.email = email;
    }

    public String getCodId() {
        return codId;
    }

    public void setCodId(String codId) {
        this.codId = codId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CoD{" +
                "codId='" + codId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}