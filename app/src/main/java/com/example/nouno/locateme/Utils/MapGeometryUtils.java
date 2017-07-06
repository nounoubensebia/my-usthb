package com.example.nouno.locateme.Utils;


import android.graphics.PointF;

import com.example.nouno.locateme.Data.Coordinate;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.mapbox.mapboxsdk.maps.Projection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nouno on 26/06/2017.
 */

public class MapGeometryUtils {

    public static Coordinate findNearestPoint(Coordinate testa, List<Coordinate> targeta) {
        double distance = -1;
        LatLng test = testa.getGoogleLatLng();
        ArrayList<LatLng> target = new ArrayList<>();
        for (Coordinate c: targeta)
        {
            target.add(c.getGoogleLatLng());
        }
        LatLng minimumDistancePoint = test;

        if (test == null || target == null) {
            return new Coordinate(minimumDistancePoint);
        }

        for (int i = 0; i < target.size(); i++) {
            LatLng point = target.get(i);

            int segmentPoint = i + 1;
            if (segmentPoint >= target.size()) {
                segmentPoint = 0;
            }

            double currentDistance = PolyUtil.distanceToLine(test, point, target.get(segmentPoint));
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;
                minimumDistancePoint = findNearestPoint(test, point, target.get(segmentPoint));
            }
        }

        return new Coordinate(minimumDistancePoint);
    }


    private static LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end) {
        if (start.equals(end)) {
            return start;
        }

        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }

        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));


    }

    public static Double calculatePathDistance (ArrayList<Coordinate> coordinates)
    {
        Coordinate startCoordinate = coordinates.get(0);
        double distance = 0;
        for (int i=1;i<coordinates.size();i++)
        {
            distance+=Haversine.distance(startCoordinate,coordinates.get(i));
            startCoordinate = coordinates.get(i);
        }
        return distance;
    }

    public static Double calculatePointDistanceToPolyline (Coordinate point,ArrayList<Coordinate> polyline)
    {
        ArrayList<Coordinate> latLngs = new ArrayList<>();
        for (Coordinate c:polyline)
        {
            latLngs.add(c);
        }
        Coordinate nearestPoint = findNearestPoint(point,latLngs);
        return Haversine.distance(point,nearestPoint);
    }




    private static double getA (PointF a, PointF b)
    {
        return ((a.y-b.y)/(a.x-b.x));
    }
    private static double getB(PointF a,PointF b)
    {
        return (a.y-getA(a,b)*a.x);
    }

    private static PointF getIntersectionPoint(PointF a, PointF b, PointF c, PointF d)
    {
        double x = (getB(c,d)-getB(a,b))/(getA(a,b)-getA(c,d));
        return new PointF((float)x,(float)(getA(a,b)*x+getB(a,b)));
    }

    private static boolean isPointInsideSegment (PointF p,PointF a,PointF b)
    {
        float maxX = a.x;
        float minX = b.x;
        if (b.x>maxX)
        {
            maxX = b.x;
            minX = a.x;
        }
        float maxY = a.y;
        float minY = b.y;
        if (b.y>maxY)
        {
            maxY = b.y;
            minY = a.y;
        }
        return ((p.x<maxX&&p.x>minX&&p.y<maxY&&p.y>minY));
    }

    public static boolean getLineSegmentIntersection (PointF a, PointF b, PointF c, PointF d)
    {
        PointF intersectionPoint = getIntersectionPoint(a,b,c,d);
        return isPointInsideSegment(intersectionPoint,c,d);
    }

    public static Coordinate getMiddle (Coordinate coordinate1, Coordinate coordinate2)
    {
        return midPoint(coordinate1.getLatitude(),coordinate1.getLongitude(),coordinate2.getLatitude(),coordinate2.getLongitude());
    }

    public static Coordinate midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);
        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        return new Coordinate(Math.toDegrees(lat3),Math.toDegrees(lon3));
    }

    public static double angleBetween2Lines(Coordinate coordinate1, Coordinate coordinate2,Coordinate coordinate3,Coordinate coordinate4,Projection projection)
    {
        PointF X1 = projection.toScreenLocation(coordinate1.getMapBoxLatLng());
        PointF X2 = projection.toScreenLocation(coordinate2.getMapBoxLatLng());
        PointF X4 = projection.toScreenLocation(coordinate3.getMapBoxLatLng());
        PointF X3 = projection.toScreenLocation(coordinate4.getMapBoxLatLng());

        float x1 = X2.x-X1.x;
        float x2 = X4.x-X3.x;
        float y1 = X2.y-X1.y;
        float y2 = X4.y-X3.y;


        float dot = x1*x2 + y1*y2;
        float det = x1*y2 - y1*x2;

        return  Math.toDegrees(Math.atan2(det, dot));
    }

}
