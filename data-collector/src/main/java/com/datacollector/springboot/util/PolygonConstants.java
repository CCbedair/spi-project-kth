package com.datacollector.springboot.util;

import com.datacollector.springboot.model.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class PolygonConstants {

    public static final String A = "A";
    public static final String B = "B";
    public static final String C = "C";
    public static final String D = "D";

    private DCPolygon PolygonA;
    private DCPolygon PolygonB;
    private DCPolygon PolygonC;
    private DCPolygon PolygonD;
    private List<DCPolygon> allPolygons;

    public PolygonConstants(){
        allPolygons = new ArrayList<>();
        List<Coordinate> pointsA = new ArrayList<>();
        List<Coordinate> pointsB = new ArrayList<>();
        List<Coordinate> pointsC = new ArrayList<>();
        List<Coordinate> pointsD = new ArrayList<>();

        pointsA.add(new Coordinate(59.31472164089893, 18.076612856035638));
        pointsA.add(new Coordinate(59.31480939711357, 18.077406789904057));
        pointsA.add(new Coordinate(59.31491909206222, 18.078329469805155));
        pointsA.add(new Coordinate(59.31501781721235, 18.07912340367357));
        pointsA.add(new Coordinate(59.315105572659846, 18.079767133837144));
        pointsA.add(new Coordinate(59.31489715310091, 18.079917337541985));
        pointsA.add(new Coordinate(59.31472164089893, 18.080024625902563));
        pointsA.add(new Coordinate(59.31450224936792, 18.080174829607405));
        pointsA.add(new Coordinate(59.31442546199645, 18.0795954724602));
        pointsA.add(new Coordinate(59.31432399269407, 18.07890346253436));
        pointsA.add(new Coordinate(59.314263659456564, 18.07845821583789));
        pointsA.add(new Coordinate(59.31419235639213, 18.077970053797156));
        pointsA.add(new Coordinate(59.31411556831836, 18.07744970524824));
        pointsA.add(new Coordinate(59.31405523471853, 18.076956178789533));
        pointsA.add(new Coordinate(59.31472164089893, 18.076612856035638));

        pointsB.add(new Coordinate(59.32914782410786, 18.0664099030041));
        pointsB.add(new Coordinate(59.32953181531392, 18.067893450466702));
        pointsB.add(new Coordinate(59.329986849201035, 18.067389195171916));
        pointsB.add(new Coordinate(59.3298004505168, 18.066648905483788));
        pointsB.add(new Coordinate(59.3296853214051, 18.066123192516873));
        pointsB.add(new Coordinate(59.32961405080645, 18.065897886959636));
        pointsB.add(new Coordinate(59.32914782410786, 18.0664099030041));

        pointsC.add(new Coordinate(59.329986849201035, 18.067389195171916));
        pointsC.add(new Coordinate(59.33038932479341, 18.066928743754634));
        pointsC.add(new Coordinate(59.330016530829994, 18.06550180855872));
        pointsC.add(new Coordinate(59.32974789734141, 18.06570565644385));
        pointsC.add(new Coordinate(59.329610838617995, 18.06583440247656));
        pointsC.add(new Coordinate(59.329986849201035, 18.067389195171916));

        pointsD.add(new Coordinate(59.329972672437925, 18.065362333689933));
        pointsD.add(new Coordinate(59.33040028925959, 18.06493318024754));
        pointsD.add(new Coordinate(59.33011521197557, 18.06401050034644));
        pointsD.add(new Coordinate(59.32993429631493, 18.063323854838604));
        pointsD.add(new Coordinate(59.32985206160014, 18.06304490510108));
        pointsD.add(new Coordinate(59.32966566217392, 18.06323802415013));
        pointsD.add(new Coordinate(59.32950119123984, 18.063409685527102));
        pointsD.add(new Coordinate(59.32958890917075, 18.06383883896947));
        pointsD.add(new Coordinate(59.32975337967879, 18.064589857493647));
        pointsD.add(new Coordinate(59.32985206160014, 18.064986824427855));
        pointsD.add(new Coordinate(59.329972672437925, 18.065362333689933));

        PolygonA = new DCPolygon(A, pointsA);
        PolygonB = new DCPolygon(B, pointsB);
        PolygonC = new DCPolygon(C, pointsC);
        PolygonD = new DCPolygon(D, pointsD);

        allPolygons.add(PolygonA);
        allPolygons.add(PolygonB);
        allPolygons.add(PolygonC);
        allPolygons.add(PolygonD);

    }

    public List<DCPolygon> getAllPolygons(){
        return allPolygons;
    }

}
