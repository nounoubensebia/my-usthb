package com.example.nouno.locateme.Data;

import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.Djikstra.Graph;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Created by nouno on 13/07/2017.
 */

public class Navigator {
    private int currentItem;
    private AbstractList<NavigationInstructionItem> navigationInstructionItems;
    private Graph graph;
    public Navigator(AbstractList<NavigationInstructionItem> navigationInstructionItems,Graph graph) {
        this.navigationInstructionItems = navigationInstructionItems;
        this.graph = graph;
        currentItem = 0;
    }

    public ArrayList<Coordinate> advance ()
    {

        if (navigationInstructionItems.size()>(currentItem+1))
        {
            currentItem++;
            int startOrder = navigationInstructionItems.get(currentItem).getStartOrder();
            Edge edge = graph.getEdges(startOrder,startOrder).get(0);
            return edge.getCoordinates();
        }
        return null;
    }

    public ArrayList<Coordinate> goBack ()
    {
        if (currentItem!=0)
        {
            currentItem--;
            int startOrder = navigationInstructionItems.get(currentItem).getStartOrder();
            Edge edge = graph.getEdges(startOrder,startOrder).get(0);
            return edge.getCoordinates();
        }
        return null;
    }

    public ArrayList<Coordinate> getCurrentPolyline ()
    {
        int startOrder = navigationInstructionItems.get(currentItem).getStartOrder();
        int endOrder = navigationInstructionItems.get(currentItem).getEndOrder();
        ArrayList<Edge> edges = graph.getEdges(startOrder,endOrder);
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (Edge edge:edges)
        {
            coordinates.addAll(edge.getCoordinates());
        }
        return coordinates;
    }

    public NavigationInstructionItem getNavigationInstructionItem()
    {
        return navigationInstructionItems.get(currentItem);
    }
}
