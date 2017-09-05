package com.example.nouno.locateme.Utils;

import com.mapbox.mapboxsdk.annotations.Marker;

/**
 * Created by nouno on 05/09/2017.
 */

public class UiMarkerUtils {
    private Marker marker;
    private Object tag;

    public UiMarkerUtils(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
