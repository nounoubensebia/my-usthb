package com.example.nouno.locateme.Djikstra;

import android.graphics.PointF;
import android.os.AsyncTask;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.OnSearchFinishListener;
import com.example.nouno.locateme.Utils.MapUtils;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Projection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nouno on 25/06/2017.
 */
public class Graph {
    private List<Vertex> vertexes;
    private List<Edge> edges;
    public static final double MAX_DISTANCE = 0.05;

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public Graph(String json) {
        vertexes = new ArrayList<>();
        edges = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray vertexesJson = jsonObject.getJSONArray("vertexes");
            for (int i = 0; i < vertexesJson.length(); i++) {
                JSONObject vertexJson = vertexesJson.getJSONObject(i);
                String name = vertexJson.getString("name");
                long id = vertexJson.getLong("id");
                Vertex v = new Vertex(id, name);
                vertexes.add(v);
            }
            JSONArray edgesJson = jsonObject.getJSONArray("edges");
            for (int i = 0; i < edgesJson.length(); i++) {
                JSONObject edgeJson = edgesJson.getJSONObject(i);
                JSONArray coordinatesJson = edgeJson.getJSONArray("coordinates");
                ArrayList<Coordinate> coordinates = new ArrayList<>();
                for (int j = 0; j < coordinatesJson.length(); j++) {
                    JSONObject coordinateJson = coordinatesJson.getJSONObject(j);
                    double latitude = coordinateJson.getDouble("longitude");
                    double longitude = coordinateJson.getDouble("latitude");
                    Coordinate coordinate = new Coordinate(latitude, longitude);
                    coordinates.add(coordinate);
                }
                JSONObject sourceJson = edgeJson.getJSONObject("source");
                long sourceId = sourceJson.getInt("id");
                JSONObject destinationJson = edgeJson.getJSONObject("destination");
                long destinationId = destinationJson.getInt("id");
                Vertex source = Vertex.getVertexById(vertexes, sourceId);
                Vertex destination = Vertex.getVertexById(vertexes, destinationId);
                long id = edgeJson.getLong("id");
                double weight = edgeJson.getDouble("weight");
                Edge edge = new Edge(id, source, destination, weight, coordinates);
                edges.add(edge);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public long getLastEdgeId() {
        long id = 0;
        for (Edge e : edges) {
            if (id < e.getId()) {
                id = e.getId();
            }
        }
        return id;
    }

    public long getLastVertexId() {
        long id = 0;
        for (Vertex v : vertexes) {
            if (id < v.getId()) {
                id = v.getId();
            }
        }
        return id;
    }

    private void addReverse() {
        List<Edge> newEdges = new ArrayList<>();
        int i = 1;
        for (Edge e1 : edges) {
            Edge e2 = new Edge(getLastEdgeId() + i, e1.getDestination(), e1.getSource(), e1.getWeight(), e1.getCoordinates());
            i++;
            newEdges.add(e2);
        }
        edges.addAll(newEdges);

    }

    private Vertex explodeEdgeAtCoordinate(Edge e, Coordinate polylineCoordinate, Coordinate explosionCoordinate, Projection projection) {

        if (polylineCoordinate.equals(e.getCoordinates().get(0))) {
            Vertex userVertex = new Vertex(getLastVertexId() + 1, (getLastVertexId() + 1) + "");
            ArrayList<Coordinate> newPathCoordinates = new ArrayList<>();
            newPathCoordinates.add(explosionCoordinate);
            newPathCoordinates.add(e.getCoordinates().get(0));
            Edge edge = new Edge(getLastEdgeId() + 1, userVertex, e.getSource(), MapUtils.calculatePathDistance(newPathCoordinates), newPathCoordinates);
            edges.add(edge);
            vertexes.add(userVertex);
            return userVertex;
        }
        if (polylineCoordinate.equals(e.getCoordinates().get(e.getCoordinates().size() - 1))) {
            Vertex userVertex = new Vertex(getLastVertexId() + 1, (getLastVertexId() + 1) + "");
            ArrayList<Coordinate> newPathCoordinates = new ArrayList<>();
            newPathCoordinates.add(explosionCoordinate);
            newPathCoordinates.add(e.getCoordinates().get(e.getCoordinates().size() - 1));
            Edge edge = new Edge(getLastEdgeId() + 1, userVertex, e.getDestination(), MapUtils.calculatePathDistance(newPathCoordinates), newPathCoordinates);
            edges.add(edge);
            vertexes.add(userVertex);
            return userVertex;
        }
        int order1 = 0;
        int i = 0;
        PointF userPoint = projection.toScreenLocation(new LatLng(explosionCoordinate.getLatitude(), explosionCoordinate.getLongitude()));
        PointF polylinePoint = projection.toScreenLocation(new LatLng(polylineCoordinate.getLatitude(), polylineCoordinate.getLongitude()));
        Coordinate previousCoord = e.getCoordinates().get(0);
        for (i = 1; i < e.getCoordinates().size(); i++) {
            PointF previousPoint = projection.toScreenLocation(new LatLng(previousCoord.getLatitude(), previousCoord.getLongitude()));
            PointF currentPoint = projection.toScreenLocation(new LatLng(e.getCoordinates().get(i).getLatitude(), e.getCoordinates().get(i).getLongitude()));
            previousCoord = e.getCoordinates().get(i);
            if (MapUtils.getLineSegmentIntersection(userPoint, polylinePoint, previousPoint, currentPoint)) {
                order1 = i - 1;
            }
        }
        ArrayList<Coordinate> sourceToPoint = new ArrayList<>();
        for (i = 0; i <= order1; i++) {
            sourceToPoint.add(e.getCoordinates().get(i));
        }
        sourceToPoint.add(polylineCoordinate);
        ArrayList<Coordinate> pointToDestinationPath = new ArrayList<>();
        pointToDestinationPath.add(polylineCoordinate);
        for (i = order1 + 1; i < e.getCoordinates().size(); i++) {
            pointToDestinationPath.add(e.getCoordinates().get(i));
        }
        ArrayList<Coordinate> userToPointPath = new ArrayList<>();
        userToPointPath.add(explosionCoordinate);
        userToPointPath.add(polylineCoordinate);
        Vertex source = e.getSource();
        Vertex destination = e.getDestination();
        Vertex pointVertex = new Vertex(getLastVertexId() + 1, getLastVertexId() + 2 + "");
        Vertex userVertex = new Vertex(getLastVertexId() + 2, getLastVertexId() + 2 + "");
        Edge sourceToPointEdge = new Edge(getLastEdgeId() + 1, source, pointVertex, MapUtils.calculatePathDistance(sourceToPoint), sourceToPoint);
        Edge PointToDestinationEdge = new Edge(getLastEdgeId() + 2, pointVertex, destination, MapUtils.calculatePathDistance(pointToDestinationPath), pointToDestinationPath);
        Edge userToPointEdge = new Edge(getLastEdgeId() + 3, userVertex, pointVertex, MapUtils.calculatePathDistance(userToPointPath), userToPointPath);
        edges.remove(e);
        edges.add(sourceToPointEdge);
        edges.add(PointToDestinationEdge);
        edges.add(userToPointEdge);
        vertexes.add(pointVertex);
        vertexes.add(userVertex);
        return userVertex;
    }


    public void getShortestPath(final Coordinate source, final Vertex target, final Projection projection, final OnSearchFinishListener onSearchFinishListener) {
        AsyncTask<Void,Void,Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
            @Override
            protected void onPostExecute(Graph graph) {
                onSearchFinishListener.OnSearchFinish(graph);
            }

            @Override
            protected Graph doInBackground(Void... params) {
                return getShortestPath(source, target, projection, true);
            }
        };

    }

    public void getShortestPath (final Vertex source, final Coordinate target, final Projection projection, final OnSearchFinishListener onSearchFinishListener)
    {
        AsyncTask<Void,Void,Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
            @Override
            protected void onPostExecute(Graph graph) {
                onSearchFinishListener.OnSearchFinish(graph);
            }

            @Override
            protected Graph doInBackground(Void... params) {
                return getShortestPath(target,source,projection,false);
            }
        };
        asyncTask.execute();

    }

    public void getShortestPath(final Vertex source, final Vertex destination, final OnSearchFinishListener onSearchFinishListener)
    {
        AsyncTask<Void,Void,Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
            @Override
            protected void onPostExecute(Graph graph) {
                onSearchFinishListener.OnSearchFinish(graph);
            }

            @Override
            protected Graph doInBackground(Void... params) {
                DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(Graph.this);
                dijkstraAlgorithm.execute(source);
                return dijkstraAlgorithm.getPath(destination);
            }
        };
        asyncTask.execute();

    }

    private Graph getShortestPath(Coordinate marker, Vertex target, Projection projection, Boolean isSource) {

        Gson gson = new Gson();
        String json = gson.toJson(this);
        float minWeight = 100000;
        Graph shortestGraph = null;
        double minDistance = 1000000;
        ArrayList<Edge> edgesToExplode = edgesToExplode(marker);
        for (Edge e : edgesToExplode) {
            Graph graph = gson.fromJson(json, Graph.class);
            Edge e1 = Edge.getEdgeById(graph.edges, e.getId());
            Coordinate polylineCoordinate = MapUtils.findNearestPoint(marker, e.getCoordinates());
            Vertex source;
            if (isSource) {
                source = graph.explodeEdgeAtCoordinate(e1, polylineCoordinate, marker, projection);
            } else {
                source = target;
                target = graph.explodeEdgeAtCoordinate(e1, polylineCoordinate, marker, projection);
            }
            graph.addReverse();
            DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph);
            dijkstraAlgorithm.execute(source);
            graph = dijkstraAlgorithm.getPath(target);
            if (graph.getWeight() < minDistance) {
                minDistance = graph.getWeight();
                shortestGraph = graph;
            }
        }
        return shortestGraph;
    }

    public void getShortestPath(final Coordinate source, final Coordinate destination, final Projection projection
    , final OnSearchFinishListener onSearchFinishListener) {


        AsyncTask<Void,Void,Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
            @Override
            protected void onPostExecute(Graph graph) {
                onSearchFinishListener.OnSearchFinish(graph);
            }

            @Override
            protected Graph doInBackground(Void... params) {
                Graph graph1 = getShortestPath(source,destination,projection,true);
                Graph graph2 = getShortestPath(destination,source,projection,true);
                if (graph1.getWeight()<graph2.getWeight())
                {
                    return graph1;
                }
                else
                {
                    return graph2;
                }
            }
        };
        asyncTask.execute();

    }



    private Graph getShortestPath(Coordinate marker1, Coordinate marker2, Projection projection, Boolean marker1IsSource) {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        Vertex source=null, destination=null;
        Graph shortestGraph = null;
        double minDistance = 1000000;
        ArrayList<Edge> edgesToExplodeMarker1 = edgesToExplode(marker1);
        for (Edge edgeMarker1 : edgesToExplodeMarker1) {
            Graph graph = gson.fromJson(json, Graph.class);
            Edge e1 = Edge.getEdgeById(graph.edges, edgeMarker1.getId());
            Coordinate polylineCoordinate1 = MapUtils.findNearestPoint(marker1, edgeMarker1.getCoordinates());
            Vertex vertex = graph.explodeEdgeAtCoordinate(e1, polylineCoordinate1, marker1, projection);
            if (marker1IsSource)
                source = vertex;
            else
                destination = vertex;
                ArrayList<Edge> edgesToExplodeMarker2 = graph.edgesToExplode(marker2);
                String explodedGraphJson = gson.toJson(graph);
            for (Edge edgeMarker2 : edgesToExplodeMarker2) {
                Graph explodedGraph = gson.fromJson(explodedGraphJson, Graph.class);
                Edge e2 = Edge.getEdgeById(explodedGraph.edges, edgeMarker2.getId());
                Coordinate polylineCoordinate2 = MapUtils.findNearestPoint(marker2, edgeMarker2.getCoordinates());
                vertex = explodedGraph.explodeEdgeAtCoordinate(e2, polylineCoordinate2, marker2, projection);
                if (marker1IsSource)
                    destination = vertex;
                else
                    source = vertex;
                explodedGraph.addReverse();
                DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(explodedGraph);
                dijkstraAlgorithm.execute(source);
                graph = dijkstraAlgorithm.getPath(destination);
                if (graph != null) {
                    if (graph.getWeight() < minDistance) {
                        minDistance = graph.getWeight();
                        shortestGraph = graph;
                    }
                }
            }

        }

        return shortestGraph;
    }




    private ArrayList<Edge> edgesToExplode(Coordinate userLocation) {
        ArrayList<Edge> edgesToExplode = new ArrayList<>();
        double maxDistance = MAX_DISTANCE;
        while (edgesToExplode.size() == 0) {
            for (Edge e : edges) {
                if (MapUtils.calculatePointDistanceToPolyline(userLocation, e.getCoordinates()) < maxDistance) {
                    edgesToExplode.add(e);
                }
            }
            maxDistance += 0.01;
        }
        return edgesToExplode;
    }

    public double getWeight() {
        double weight = 0;
        for (Edge e : edges) {
            weight += e.getWeight();
        }
        return weight;
    }




}

