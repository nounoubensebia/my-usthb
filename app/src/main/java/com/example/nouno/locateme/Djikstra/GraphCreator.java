package com.example.nouno.locateme.Djikstra;


import com.company.Coordinate;
import com.example.nouno.locateme.Utils.MapGeometryUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nouno on 13/08/2017.
 */

public class GraphCreator {

    public static Graph createGraph (String json)
    {
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject osm = jsonObject.getJSONObject("osm");
            JSONArray way = osm.getJSONArray("way");
            JSONArray nodes = osm.getJSONArray("node");
            ArrayList<Noda> countedNodes = new ArrayList<>();
            countedNodes = countNodes(way,nodes);
            for (long i=0;i<way.length();i++)
            {
                JSONObject wayObj = way.getJSONObject((int)i);
                JSONArray wayNodesJson = wayObj.getJSONArray("nd");
                ArrayList<Noda> wayNodes = new ArrayList<>();

                if (isARoad(wayObj))
                {
                    /*wayNodes = getWayNodes(nodes,wayNodesJson);
                    Noda firstnode = wayNodes.get(0);

                    for (int j=0;j<wayNodes.size();j++)
                    {
                        Vertex source;
                        Vertex destination;
                        if (Vertex.getVertexById(vertices,firstnode.id)==null)
                        {
                            source= new Vertex(firstnode.id);
                            vertices.add(source);
                        }
                        else
                        {
                            source = Vertex.getVertexById(vertices,firstnode.id);
                        }
                        if (Vertex.getVertexById(vertices,wayNodes.get(j).id)==null)
                        {
                            destination= new Vertex(wayNodes.get(j).id);
                            vertices.add(destination);
                        }
                        else
                        {
                            destination = Vertex.getVertexById(vertices,wayNodes.get(j).id);
                        }
                        ArrayList<Coordinate> polyline = new ArrayList<>();
                        polyline.add(firstnode.coordinate);
                        polyline.add(wayNodes.get(j).coordinate);
                        Edge edge = new Edge(i*10+j,source,destination,MapGeometryUtils.PolylineDistance(polyline),polyline);
                        edges.add(edge);
                        //vertices.add(source);
                        //vertices.add(destination);
                        firstnode = wayNodes.get(j);
                    }*/


                    //ArrayList<Coordinate> coordinates = getWayNodes(nodes,wayNodes);
                    ArrayList<Noda> nodes1 = getWayNodes(nodes,wayNodesJson);
                    wayNodes = new ArrayList<>();
                    wayNodes.add(nodes1.get(0));
                    for (int j=1;j<nodes1.size();j++)
                    {
                        Vertex source = null;
                        Vertex destination = null;
                        Noda node = nodes1.get(j);
                        if (Noda.getNodeById(node.id,countedNodes).counter<=1)
                        {
                            wayNodes.add(node);
                        }
                        else
                        {
                            wayNodes.add(node);
                            Noda sourceNode = wayNodes.get(0);
                            Noda destinationNode = wayNodes.get(wayNodes.size()-1);
                            if (Vertex.getVertexById(vertices,sourceNode.id)==null)
                            {
                                source= new Vertex(sourceNode.id);
                                vertices.add(source);
                            }
                            else
                            {
                                source = Vertex.getVertexById(vertices,sourceNode.id);
                            }
                            if (Vertex.getVertexById(vertices,destinationNode.id)==null)
                            {
                                destination= new Vertex(destinationNode.id);
                                vertices.add(destination);
                            }
                            else
                            {
                                destination = Vertex.getVertexById(vertices,destinationNode.id);
                            }
                            ArrayList<Coordinate> polyline = new ArrayList<>();
                            for (Noda node1 : wayNodes)
                            {
                                polyline.add(node1.coordinate);
                            }
                            Edge edge = new Edge(i*10+j,source,destination,MapGeometryUtils.PolylineDistance(polyline),polyline);
                            edges.add(edge);
                            wayNodes = new ArrayList<>();
                            wayNodes.add(node);
                        }

                    }
                    Vertex source = null;
                    Vertex destination = null;
                    if (wayNodes.size()>1)
                    {
                        Noda sourceNode = wayNodes.get(0);
                        Noda destinationNode = wayNodes.get(wayNodes.size()-1);
                        if (Vertex.getVertexById(vertices,sourceNode.id)==null)
                        {
                            source= new Vertex(sourceNode.id);
                            vertices.add(source);
                        }
                        else
                        {
                            source = Vertex.getVertexById(vertices,sourceNode.id);
                        }
                        if (Vertex.getVertexById(vertices,destinationNode.id)==null)
                        {
                            destination= new Vertex(destinationNode.id);
                            vertices.add(destination);
                        }
                        else
                        {
                            destination = Vertex.getVertexById(vertices,destinationNode.id);
                        }
                        ArrayList<Coordinate> polyline = new ArrayList<>();
                        for (Noda node1 : wayNodes)
                        {
                            polyline.add(node1.coordinate);
                        }
                        Edge edge = new Edge(i*10+nodes1.size()+1,source,destination,MapGeometryUtils.PolylineDistance(polyline),polyline);
                        edges.add(edge);

                    }
                }



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Graph(vertices,edges);
    }




    private static ArrayList<Noda> countNodes(JSONArray way,JSONArray nodesJson)
    {
        ArrayList<Noda> nodes = new ArrayList<>();

        for (int i=0;i<way.length();i++)
        {
            try {
                JSONObject wayJson = way.getJSONObject(i);
                if (isARoad(wayJson))
                {
                    JSONArray nd = wayJson.getJSONArray("nd");
                    for (int j=0;j<nd.length();j++)
                    {
                        //Noda noda = getNode(nd.getJSONObject(j).getLong("-ref"),nodesJson);
                        Noda other = Noda.getNodeById(nd.getJSONObject(j).getLong("-ref"),nodes);
                        if (other==null)
                        {
                            nodes.add(getNode(nd.getJSONObject(j).getLong("-ref"),nodesJson));
                        }
                        else
                        {
                            other.setCounter(2);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nodes;
    }



    private static  ArrayList<Noda> getWayNodes(JSONArray nodes, JSONArray wayNodes)
    {
        ArrayList<Noda> nodes1 = new ArrayList<>();
        for (int i=0;i<wayNodes.length();i++)
        {
            try {

                Noda node = getNode(wayNodes.getJSONObject(i).getLong("-ref"),nodes);

                nodes1.add(node);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nodes1;
    }

    private static Noda getNode(long ref, JSONArray nodes)
    {
        for (int i=0;i<nodes.length();i++)
        {
            try {
                JSONObject node = nodes.getJSONObject(i);
                long id = node.getLong("-id");
                if (id==ref)
                {
                    Coordinate coordinate = new Coordinate(node.getDouble("-lat"),node.getDouble("-lon"));
                    return new Noda(node.getLong("-id"),0,coordinate);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static boolean isARoad (JSONObject wayObj)
    {
        try {
            JSONObject tag = wayObj.getJSONObject("tag");
            String k = tag.getString("-k");
            if (k.equals("highway"))
            {
                return true;
            }
            else {
                return false;
            }
        } catch (JSONException e) {

        }
        try {
            JSONArray tag = wayObj.getJSONArray("tag");
            for (int i=0;i<tag.length();i++)
            {
                JSONObject jsonObject =  tag.getJSONObject(i);
                String k = jsonObject.getString("-k");
                if (k.equals("highway"))
                    return true;
            }
        } catch (JSONException e) {

        }
        return false;
    }

    private static class Noda {
        long id;
        int counter;
        private Coordinate coordinate;
        public Noda(long id, int counter, Coordinate coordinate) {
            this.id = id;
            this.counter = counter;
            this.coordinate = coordinate;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        public void incrementCounter()
        {
            counter++;
        }

        public static Noda getNodeById (long id, ArrayList<Noda> nodes)
        {
            for (Noda node:nodes)
            {
                if (node.getId()==id)
                {
                    return node;
                }
            }
            return null;
        }
    }
    private static class way {
        long id;
        ArrayList<Noda> nodes;

        public way(long id, ArrayList<Noda> nodes) {
            this.id = id;
            this.nodes = nodes;
        }
    }


}
