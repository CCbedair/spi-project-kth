package com.datacollector.springboot.util;

import com.datacollector.springboot.model.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class PolygonUtil {

    private PolygonConstants polygonConstants = new PolygonConstants();
    private List<DCPolygon> allPolygons = polygonConstants.getAllPolygons();

    public DCPolygon getWhichPolygon(Coordinate testPoint) {

        DCPolygon result = null;
        for (int i = allPolygons.size() - 1; i >= 0; i--) {
            DCPolygon currentPolygon = allPolygons.get(i);
            if (currentPolygon.contains(testPoint))
                result = currentPolygon;

        }
        List<Coordinate> wronglist = new ArrayList<>();
        wronglist.add(new Coordinate(0,0));
        if (result == null) return new DCPolygon("F", wronglist);
        return result;
    }
}
