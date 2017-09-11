package com.example.nouno.locateme.Djikstra;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;

import com.example.nouno.locateme.Data.Coordinate;
import com.example.nouno.locateme.Data.NavigationInstruction;
import com.example.nouno.locateme.Data.Path;
import com.example.nouno.locateme.OnSearchFinishListener;
import com.example.nouno.locateme.Utils.MapGeometryUtils;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Projection;
import com.mapbox.services.commons.utils.MapboxUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nouno on 25/06/2017.
 */
public class Graph {
    private List<Vertex> vertexes;
    private List<Edge> edges;
    public static final double MAX_DISTANCE = 0.03;

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public void createGraphV2(String json) {
        vertexes = new ArrayList<>();
        edges = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject osm = jsonObject.getJSONObject("osm");
            JSONArray way = osm.getJSONArray("way");
            JSONArray node = osm.getJSONArray("Noda");
            for (long i = 0; i < way.length(); i++) {
                JSONObject wayObj = way.getJSONObject((int) i);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Graph(String json) {
        vertexes = new ArrayList<>();
        edges = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray vertexesJson = jsonObject.getJSONArray("vertexes");
            for (int i = 0; i < vertexesJson.length(); i++) {
                JSONObject vertexJson = vertexesJson.getJSONObject(i);
                //String name = vertexJson.getString("name");
                long id = vertexJson.getLong("id");
                Vertex v = new Vertex(id);
                vertexes.add(v);
            }
            JSONArray edgesJson = jsonObject.getJSONArray("edges");
            for (int i = 0; i < edgesJson.length(); i++) {
                JSONObject edgeJson = edgesJson.getJSONObject(i);
                JSONArray coordinatesJson = edgeJson.getJSONArray("coordinates");
                ArrayList<Coordinate> coordinates = new ArrayList<>();
                for (int j = 0; j < coordinatesJson.length(); j++) {
                    JSONObject coordinateJson = coordinatesJson.getJSONObject(j);
                    double longitude = coordinateJson.getDouble("longitude");
                    double latitude = coordinateJson.getDouble("latitude");
                    Coordinate coordinate = new Coordinate(latitude, longitude);
                    coordinates.add(coordinate);
                }
                JSONObject sourceJson = edgeJson.getJSONObject("source");
                long sourceId = sourceJson.getLong("id");
                JSONObject destinationJson = edgeJson.getJSONObject("destination");
                long destinationId = destinationJson.getLong("id");
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
            ArrayList<Coordinate> coordinates2 = new ArrayList<>();
            for (Coordinate coordinate : e1.getCoordinates()) {
                coordinates2.add(coordinate);
            }
            Collections.reverse(coordinates2);
            Edge e2 = new Edge(getLastEdgeId() + i, e1.getDestination(), e1.getSource(), e1.getWeight(), coordinates2);

            i++;
            newEdges.add(e2);
        }
        edges.addAll(newEdges);

    }


    private Vertex explodeEdgeAtCoordinate(Edge e, Coordinate polylineCoordinate, Coordinate explosionCoordinate, Projection projection, boolean isSource) {

        if (polylineCoordinate.equals(e.getCoordinates().get(0))) {
            Vertex userVertex;
            if (isSource) {
                if ((Vertex.getVertexById(vertexes,-5) == null)) {
                    userVertex = new Vertex(-5);
                    vertexes.add(userVertex);
                } else {
                    userVertex = Vertex.getVertexById(vertexes,-5);
                }
            } else {

                if ((Vertex.getVertexById(vertexes,-4) == null)) {
                    userVertex = new Vertex(-4);
                    vertexes.add(userVertex);
                } else {
                    userVertex = Vertex.getVertexById(vertexes,-4);
                }
            }

            ArrayList<Coordinate> newPathCoordinates = new ArrayList<>();
            newPathCoordinates.add(explosionCoordinate);
            newPathCoordinates.add(e.getCoordinates().get(0));
            Edge edge = new Edge(getLastEdgeId() + 1, userVertex, e.getSource(), MapGeometryUtils.PolylineDistance(newPathCoordinates), newPathCoordinates);
            edge.setExploaded(true);
            edges.add(edge);
            return userVertex;
        }
        if (polylineCoordinate.equals(e.getCoordinates().get(e.getCoordinates().size() - 1))) {
            Vertex userVertex;
            if (isSource) {
                if ((Vertex.getVertexById(vertexes, -5) == null)) {
                    userVertex = new Vertex(-5);
                    vertexes.add(userVertex);
                } else {
                    userVertex = Vertex.getVertexById(vertexes, -5);
                }
            } else {

                if ((Vertex.getVertexById(vertexes, -4) == null)) {
                    userVertex = new Vertex(-4);
                    vertexes.add(userVertex);
                } else {
                    userVertex = Vertex.getVertexById(vertexes, -4);
                }
            }

            ArrayList<Coordinate> newPathCoordinates = new ArrayList<>();
            newPathCoordinates.add(explosionCoordinate);
            newPathCoordinates.add(e.getCoordinates().get(e.getCoordinates().size() - 1));
            Edge edge = new Edge(getLastEdgeId() + 1, userVertex, e.getDestination(), MapGeometryUtils.PolylineDistance(newPathCoordinates), newPathCoordinates);
            edges.add(edge);

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
            if (MapGeometryUtils.getLineSegmentIntersection(userPoint, polylinePoint, previousPoint, currentPoint)) {
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
        Vertex pointVertex = new Vertex(getLastVertexId() + 1);
        Vertex userVertex;

        if (isSource) {
            if ((Vertex.getVertexById(vertexes, -5) == null)) {
                userVertex = new Vertex(-5);
                vertexes.add(userVertex);
            } else {
                userVertex = Vertex.getVertexById(vertexes, -5);
            }
        } else {

            if ((Vertex.getVertexById(vertexes, -4) == null)) {
                userVertex = new Vertex(-4);
                vertexes.add(userVertex);
            } else {
                userVertex = Vertex.getVertexById(vertexes, -4);
            }
        }

        Edge sourceToPointEdge = new Edge(getLastEdgeId() + 1, source, pointVertex, MapGeometryUtils.PolylineDistance(sourceToPoint), sourceToPoint);
        Edge PointToDestinationEdge = new Edge(getLastEdgeId() + 2, pointVertex, destination, MapGeometryUtils.PolylineDistance(pointToDestinationPath), pointToDestinationPath);
        Edge userToPointEdge = new Edge(getLastEdgeId() + 3, userVertex, pointVertex, MapGeometryUtils.PolylineDistance(userToPointPath), userToPointPath);
        edges.remove(e);
        edges.add(sourceToPointEdge);
        edges.add(PointToDestinationEdge);
        edges.add(userToPointEdge);
        vertexes.add(pointVertex);
        vertexes.add(userVertex);
        return userVertex;
    }


    public void getShortestPath(final Coordinate source, final Vertex target, final Projection projection, final OnSearchFinishListener onSearchFinishListener) {
        AsyncTask<Void, Void, Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
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

    public void getShortestPath(final Vertex source, final Coordinate target, final Projection projection, final OnSearchFinishListener onSearchFinishListener) {
        AsyncTask<Void, Void, Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
            @Override
            protected void onPostExecute(Graph graph) {
                onSearchFinishListener.OnSearchFinish(graph);
            }

            @Override
            protected Graph doInBackground(Void... params) {
                return getShortestPath(target, source, projection, false);
            }
        };
        asyncTask.execute();

    }

    public void getShortestPath(final Vertex source, final Vertex destination, final OnSearchFinishListener onSearchFinishListener) {
        AsyncTask<Void, Void, Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
            @Override
            protected void onPostExecute(Graph graph) {
                onSearchFinishListener.OnSearchFinish(graph);
            }

            @Override
            protected Graph doInBackground(Void... params) {
                Graph.this.addReverse();
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
            Coordinate polylineCoordinate = MapGeometryUtils.findNearestPoint(marker, e.getCoordinates());
            Vertex source;
            if (isSource) {
                source = graph.explodeEdgeAtCoordinate(e1, polylineCoordinate, marker, projection,true);
            } else {
                source = target;
                target = graph.explodeEdgeAtCoordinate(e1, polylineCoordinate, marker, projection,false);
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


        AsyncTask<Void, Void, Graph> asyncTask = new AsyncTask<Void, Void, Graph>() {
            @Override
            protected void onPostExecute(Graph graph) {
                onSearchFinishListener.OnSearchFinish(graph);
            }

            @Override
            protected Graph doInBackground(Void... params) {
                /*
                Graph graph1 = getShortestPath(source,destination,projection,true);
                Graph graph2 = getShortestPath(destination,source,projection,false);
                if (graph1.getWeight()<graph2.getWeight())
                {
                    return graph1;
                }
                else
                {
                    return graph2;
                }*/
                return getShortestPath(source, destination, projection, true);
            }
        };
        asyncTask.execute();

    }


    private Graph getShortestPath(Coordinate marker1, Coordinate marker2, Projection projection, Boolean marker1IsSource) {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        Vertex source = null, destination = null;
        Graph shortestGraph = null;
        Graph graph = gson.fromJson(json, Graph.class);
        double minDistance = 1000000;
        ArrayList<Edge> edgesToExplodeMarker1 = edgesToExplode(marker1);
        ArrayList<Edge> edgesToExplodeMarker2 = edgesToExplode(marker2);
        /*for (Edge edgeMarker1 : edgesToExplodeMarker1) {
            Graph graph = gson.fromJson(json, Graph.class);
            Edge e1 = Edge.getEdgeById(graph.edges, edgeMarker1.getId());
            Coordinate polylineCoordinate1 = MapGeometryUtils.findNearestPoint(marker1, edgeMarker1.getCoordinates());
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
                Coordinate polylineCoordinate2 = MapGeometryUtils.findNearestPoint(marker2, edgeMarker2.getCoordinates());
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

        }*/
        for (Edge edge : edgesToExplodeMarker1) {
            Edge e1 = Edge.getEdgeById(graph.edges, edge.getId());
            Coordinate polylineCoordinate1 = MapGeometryUtils.findNearestPoint(marker1, edge.getCoordinates());
            source = graph.explodeEdgeAtCoordinate(e1, polylineCoordinate1, marker1, projection,true);
        }
        for (Edge edge : edgesToExplodeMarker2) {
            Edge e1 = Edge.getEdgeById(graph.edges, edge.getId());
            Coordinate polylineCoordinate1 = MapGeometryUtils.findNearestPoint(marker2, edge.getCoordinates());
            if (e1!=null)
            destination = graph.explodeEdgeAtCoordinate(e1, polylineCoordinate1, marker2, projection,false);
        }
        graph.addReverse();
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph);
        dijkstraAlgorithm.execute(source);
        graph = dijkstraAlgorithm.getPath(destination);

        return graph;
    }

    private ArrayList<Edge> edgesToExplode(Coordinate userLocation) {
        ArrayList<Edge> edgesToExplode = new ArrayList<>();
        double maxDistance = MAX_DISTANCE;
        while (edgesToExplode.size() == 0) {
            for (Edge e : edges) {
                if (MapGeometryUtils.calculatePointDistanceToPolyline(userLocation, e.getCoordinates()) < maxDistance) {
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

    public void getShortestPath(Path path, Projection projection, final OnSearchFinishListener onSearchFinishListener) {

        if (path.getSource().getClass().getName().equals("com.example.nouno.locateme.Data.Place") && path.getDestination().getClass().getName().equals("com.example.nouno.locateme.Data.Place")) {

            this.getShortestPath(path.getSource().getCoordinate(), path.getDestination().getCoordinate(), projection,
                    new OnSearchFinishListener() {
                        @Override
                        public void OnSearchFinish(Graph graph) {
                            onSearchFinishListener.OnSearchFinish(graph);
                        }
                    });
        }

    }

    public ArrayList<NavigationInstruction> getNavigationInstructions(Projection projection) {
        ArrayList<Coordinate> lastPolyline = new ArrayList<>();
        ArrayList<Coordinate> savedPolyline = new ArrayList<>();
        ArrayList<NavigationInstruction> navigationInstructions = new ArrayList<>();

        int startOrder = 0;
        int endOrder = 0;
        int i = 0;
        for (Edge e : edges) {

            if (lastPolyline.size() > 0) {


                double angle = MapGeometryUtils.angleBetween2Lines(lastPolyline.get(0), lastPolyline.get(1),
                        e.getCoordinates().get(0), e.getCoordinates().get(1), projection);


                if (angle < 160 && angle >= 20) {
                    Log.e("TAGT", angle + "");
                    double distance = 0;
                    endOrder = i - 1;
                    ArrayList<Edge> edges = getEdges(startOrder, endOrder);
                    for (Edge e1 : edges) {
                        distance += MapGeometryUtils.PolylineDistance(e1.getCoordinates());
                    }
                    navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_LEFT, distance, startOrder, endOrder));
                    startOrder = i;


                }
                ;
                if (angle < 20 && angle >= -20) {

                }
                ;
                if (angle < -20 && angle >= -160) {
                    Log.e("TAGT", angle + "");
                    double distance = 0;
                    endOrder = i - 1;
                    ArrayList<Edge> edges = getEdges(startOrder, endOrder);
                    for (Edge e1 : edges) {
                        distance += MapGeometryUtils.PolylineDistance(e1.getCoordinates());
                    }
                    navigationInstructions.add(new NavigationInstruction(NavigationInstruction.DIRECTION_RIGHT, distance, startOrder, endOrder));
                    startOrder = i;

                }
                ;


            }
            lastPolyline = e.getLastPolyline();
            for (Coordinate c : e.getCoordinates()) {
                savedPolyline.add(c);
            }
            i++;
        }
        ArrayList<Coordinate> polyline = edges.get(edges.size() - 1).getCoordinates();
        NavigationInstruction navigationInstruction = new NavigationInstruction(NavigationInstruction.DIRECTION_FRONT, MapGeometryUtils.PolylineDistance(polyline), edges.size() - 1, edges.size() - 1);
        navigationInstructions.add(navigationInstruction);
        return navigationInstructions;
    }

    public ArrayList<Edge> getEdges(int startOrder, int endOrder) {
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = startOrder; i <= endOrder; i++) {
            edges.add(this.edges.get(i));
        }
        return edges;
    }

}

