package com.example.nouno.locateme.Data;

import android.util.Log;

import com.example.nouno.locateme.Utils.Haversine;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Created by nouno on 15/08/2017.
 */
public class StructureList {
    private ArrayList<Classroom> classrooms;
    private ArrayList<Structure> structures;
    private ArrayList<CenterOfInterest> centerOfInterests;
    private static final Pattern ACCENTS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public StructureList(ArrayList<Classroom> classrooms, ArrayList<Structure> structures, ArrayList<CenterOfInterest> centerOfInterests) {
        this.classrooms = classrooms;
        this.structures = structures;
        this.centerOfInterests = centerOfInterests;
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }

    public ArrayList<Structure> getStructures() {
        return structures;
    }

    public ArrayList<CenterOfInterest> getCenterOfInterests() {
        return centerOfInterests;
    }

    public ArrayList<StructureMatch> getStructures(String query) {
        ArrayList<Structure> allStructures = new ArrayList<>();
        allStructures.addAll(structures);
        allStructures.addAll(classrooms);
        allStructures.addAll(centerOfInterests);
        ArrayList<StructureMatch> structures = new ArrayList<>();
        for (Structure structure : allStructures) {
            int match = 0;
            String[] arr = query.split(" ");
            for (String s : arr) {

                if (structure.getLabel().contains(s) || containsIgnoreCaseAndAccents(structure.getLabel(), s)) {
                    match++;
                } else {
                    if (structure.getTags().contains(s) ||
                            containsIgnoreCaseAndAccents(structure.getTags(), s)) {
                        match++;
                    }
                }


            }
            if (match > 0) {
                structures.add(new StructureMatch(structure, match));
                if (match > 1) {
                    Log.i("sq", "done");
                }
            }

        }
        if (structures.size() > 0)
            Collections.sort(structures);
        return structures;
    }

    private static ArrayList<StructureMatch> filterMatches(ArrayList<StructureMatch> structureMatches) {
        ArrayList<StructureMatch> structureMatches1 = new ArrayList<>();
        if (structureMatches.size() >= 1) {
            int max = structureMatches.get(0).getMatch();

            for (StructureMatch structureMatch : structureMatches) {
                if (structureMatch.getMatch() < max) {
                    return structureMatches1;
                } else {
                    structureMatches1.add(structureMatch);
                }
            }
        }
        return structureMatches1;
    }

    public static boolean containsIgnoreCaseAndAccents(String haystack, String needle) {
        final String hsToCompare = removeAccents(haystack).toLowerCase();
        final String nToCompare = removeAccents(needle).toLowerCase();

        return hsToCompare.contains(nToCompare);
    }

    public static String removeAccents(String string) {
        return ACCENTS_PATTERN.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).replaceAll("");
    }

    public ArrayList<SearchSuggestion> getSearchSuggestions(String query) {
        ArrayList<SearchSuggestion> searchSuggestions = new ArrayList<>();
        ArrayList<StructureMatch> structures = filterMatches(getStructures(query));
        for (StructureMatch structure : structures) {
            searchSuggestions.add(new SearchSuggestion(structure.getStructure().getId(), structure.getStructure(), false));
        }
        return searchSuggestions;
    }

    public ArrayList<SearchSuggestion> getSearchSuggestions (Coordinate userCoordinate,int type)
    {
        ArrayList<Structure> structures1 = new ArrayList<>();
        for (CenterOfInterest centerOfInterest:centerOfInterests)
        {


                if (centerOfInterest.getType()==type)
                {
                    structures1.add(centerOfInterest);
                }

        }
        if (userCoordinate!=null)
        {
            for (int i=0;i<structures1.size();i++)
            {


                for (int j=i+1;j<structures1.size();j++)
                {
                    CenterOfInterest centerOfInterest1 = (CenterOfInterest)structures1.get(i);
                    double distante1 = Haversine.distance(centerOfInterest1.getCoordinate(),userCoordinate);
                    CenterOfInterest centerOfInterest2 = (CenterOfInterest)structures1.get(j);
                    double distante2 = Haversine.distance(centerOfInterest2.getCoordinate(),userCoordinate);
                    if (distante1<distante2)
                    {
                        structures1.set(i,centerOfInterest2);
                        structures1.set(j,centerOfInterest1);
                    }
                }
            }
        }
        ArrayList<SearchSuggestion> searchSuggestions = new ArrayList<>();
        for (Structure structure:structures1)
        {
            searchSuggestions.add(new SearchSuggestion(structure.getId(),structure,false));
        }
        return searchSuggestions;

    }

    public String getBlocLabel(Structure structure) {
        if (structure instanceof Classroom) {
            Classroom classroom = (Classroom) structure;
            for (Structure structure1 : structures) {
                if (classroom.getStructureId() == structure1.getId())
                    return structure1.getLabel();
            }
        }
        if (structure instanceof CenterOfInterest) {
            CenterOfInterest classroom = (CenterOfInterest) structure;
            for (Structure structure1 : structures) {
                if (classroom.getStructureId() == structure1.getId())
                    return structure1.getLabel();
            }
        }
        return " ";
    }
}
