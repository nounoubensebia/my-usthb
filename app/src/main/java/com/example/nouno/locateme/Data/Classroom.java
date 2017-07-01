package com.example.nouno.locateme.Data;

/**
 * Created by nouno on 28/06/2017.
 */

public class Classroom {

    private String name;
    private String description;

    public Classroom(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
