package com.example.nouno.locateme.Data;

/**
 * Created by nouno on 09/09/2017.
 */

public class DoneSeance {
    private String module;
    private String local;
    private String groupe;
    private String prof;
    private String type;

    public DoneSeance(String module, String local, String groupe, String prof, String type) {
        this.module = module;
        this.local = local;
        this.groupe = groupe;
        this.prof = prof;
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public String getLocal() {
        return local;
    }

    public String getGroupe() {
        return groupe;
    }

    public String getProf() {
        return prof;
    }

    public String getType() {
        return type;
    }
}
