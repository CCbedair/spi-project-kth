package com.datacollector.springboot.util;


import com.datacollector.springboot.model.Coordinate;

import java.util.List;

public class DCPolygon {

    public static final String V1 = "v1";
    public static final String V2 = "v2";
    public static final String V3 = "v3";
    public static final String V4 = "v4";

    private List<Coordinate> points;
    private String label;

    public DCPolygon(String label, List<Coordinate> points ){
        this.label = label;
        this.points = points;
    }

    public boolean contains(Coordinate test) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
           if ((points.get(i).getY() > test.getY()) != (points.get(j).getY() > test.getY()) &&
                   (test.getX() < (points.get(j).getX() - points.get(i).getX()) * (test.getY() - points.get(i).getY()) / (points.get(j).getY()-points.get(i).getY()) + points.get(i).getX())
           ) {
                result = !result;
            }
        }
        return result;
    }

    public String getVZone(double longitude, double gratitude){
        int division = ((int) (longitude + gratitude)) % 4;
        String vzone = "";
        switch (division){
            case 0:
                vzone = V1;
            case 1:
                vzone = V2;
            case 2:
                vzone = V3;
            case 3:
                vzone = V4;

        }
        return vzone;
    }

    public String getArea(double longitude, double gratitude){
        return getLabel() + ";" + getVZone(longitude, gratitude);

    }

    public String getLabel(){
        return label;
    }
}
