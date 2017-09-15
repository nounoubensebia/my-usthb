package com.example.nouno.locateme.Data;

import android.util.Log;

import com.company.Coordinate;
import com.example.nouno.locateme.Djikstra.Edge;
import com.example.nouno.locateme.Djikstra.Graph;
import com.example.nouno.locateme.Utils.MapGeometryUtils;

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

    public int getItemByUserLocation (Coordinate userLocation)
    {
        long id = MapGeometryUtils.findNearestEdgeId(userLocation,graph);
        int order = 0;
        for (NavigationInstructionItem navigationInstructionItem:navigationInstructionItems)
        {
            for (Edge e:graph.getEdges(navigationInstructionItem.getStartOrder(),navigationInstructionItem.getEndOrder()))
            {
                if (e.getId()==id)
                {
                    return order;
                }
            }
            order++;
        }
        return -1;
    }

    public void goTo (int item)
    {
        currentItem = item;
    }

    public NavigationInstructionItem getNavigationInstructionItem()
    {
        return navigationInstructionItems.get(currentItem);
    }
    public int getRemainingDistance ()
    {
        double distance = 0;
        for (int i=currentItem;i<navigationInstructionItems.size();i++)
        {
            distance+=navigationInstructionItems.get(i).getDistance();
        }
        return (int)(distance*1000);
    }
    public int getRemainingDuration ()
    {
        double duration = 0;
        for (int i=currentItem;i<navigationInstructionItems.size();i++)
        {
            duration +=navigationInstructionItems.get(i).getDuration();
        }
        return (int)(duration/60);
    }

    public boolean atLastInstruction ()
    {
        Log.e("ITEM",navigationInstructionItems.size()+"");
        Log.e("ITEM",currentItem+"");
        return navigationInstructionItems.size() == (currentItem+1);
    }
}
