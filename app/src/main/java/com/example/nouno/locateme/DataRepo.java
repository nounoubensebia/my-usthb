package com.example.nouno.locateme;

import android.content.Context;

import com.company.StructureList;
import com.example.nouno.locateme.Activities.SearchQueryTwoActivity;

import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.Utils.FileUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by nouno on 11/09/2017.
 */

public class DataRepo {
    private static Graph graph;
    private static StructureList structureList;

    public static Graph getGraphInstance(Context context) {
        if (graph == null) {
            try {
                String graphJson = FileUtils.readFile(context.getAssets().open("GraphJson.txt"));
                //Graph graph = GraphCreator.createGraph(graphJson);
                graph = new Graph(graphJson);


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return graph;
        }
        return graph;
    }

    public static StructureList getStructureListInstance(Context context) {
        if (structureList == null) {
            InputStream inputStream = null;
            try {
                inputStream = context.getResources().getAssets().open("LocalsFile");
                ObjectInputStream oi = new ObjectInputStream(inputStream);
                StructureList structureList = (StructureList) oi.readObject();
                oi.close();
                inputStream.close();
                DataRepo.structureList = structureList;
                return structureList;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            return structureList;
        }
        return structureList;
    }
}
