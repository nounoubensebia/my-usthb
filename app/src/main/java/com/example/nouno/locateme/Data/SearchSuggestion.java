package com.example.nouno.locateme.Data;

/**
 * Created by nouno on 28/06/2017.
 */

public class SearchSuggestion {
    private String blocName;
    private String classroomName;

    public SearchSuggestion(String blocName, String classroomName) {
        this.blocName = blocName;
        this.classroomName = classroomName;
    }

    public SearchSuggestion(String blocName) {
        this.blocName = blocName;
    }

    public String getBlocName() {
        return blocName;
    }

    public void setBlocName(String blocName) {
        this.blocName = blocName;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }
}
