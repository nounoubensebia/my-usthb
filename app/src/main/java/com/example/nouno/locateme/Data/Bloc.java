package com.example.nouno.locateme.Data;

import com.example.nouno.locateme.Data.Classroom;
import com.example.nouno.locateme.Data.Coordinate;

import java.util.ArrayList;

/**
 * Created by nouno on 28/06/2017.
 */

public class Bloc {
    private Coordinate coordinate;
    private String name;
    private ArrayList<Classroom> classrooms;

    public Bloc(Coordinate coordinate, String name, ArrayList<Classroom> classrooms) {
        this.coordinate = coordinate;
        this.name = name;
        this.classrooms = classrooms;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(ArrayList<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public boolean containsString (String string)
    {
        return name.contains(string);
    }
    public ArrayList<Classroom> containsClassrooms (String string)
    {
        ArrayList<Classroom> classrooms1 = new ArrayList<>();
        for (Classroom classroom:classrooms)
        {
            if (classroom.getName().contains(string))
            {
                classrooms1.add(classroom);
            }
        }
        return classrooms1;
    }
}
