package client.utils;

import org.javatuples.Pair;
import java.util.Random;

public class MockDataGen {
    private Pair<Double, Double> quadAMin;
    private Pair<Double, Double> quadAMax;

    private Pair<Double, Double> quadBMin;
    private Pair<Double, Double> quadBMax;

    private Pair<Double, Double> quadCMin;
    private Pair<Double, Double> quadCMax;

    private Pair<Double, Double> quadDMin;
    private Pair<Double, Double> quadDMax;

    public MockDataGen() {
        quadAMin = new Pair<>(59.31472164089893, 18.076612856035638);
        quadAMax = new Pair<>(59.31450224936792, 18.080174829607405);

        quadBMin = new Pair<>(59.32914782410786, 18.0664099030041);
        quadBMax = new Pair<>(59.3298004505608, 18.0666648905483788);

        quadCMin = new Pair<>(59.329986849201035, 18.067389195171916);
        quadCMax = new Pair<>(59.330016530829994, 18.06550180855872);

        quadDMin = new Pair<>(59.329972672437925, 18.065362333689933);
        quadDMax = new Pair<>(59.32966566217392, 18.06323802415013);
    }

    public String genRandData(){
        Random rand = new Random();
        int tempRand = rand.nextInt(4);
        Random r = new Random();
        double randomX, randomY;
        switch (tempRand){
            case 0:
                randomX = quadAMin.getValue0() + ( quadAMax.getValue0() -  quadAMin.getValue0()) * r.nextDouble();
                randomY = quadAMin.getValue1() + ( quadAMax.getValue1() -  quadAMin.getValue1()) * r.nextDouble();
                break;
            case 1:
                randomX = quadBMin.getValue0() + ( quadBMax.getValue0() -  quadBMin.getValue0()) * r.nextDouble();
                randomY = quadBMin.getValue1() + ( quadBMax.getValue1() -  quadBMin.getValue1()) * r.nextDouble();
                break;
            case 2:
                randomX = quadCMin.getValue0() + ( quadCMax.getValue0() -  quadCMin.getValue0()) * r.nextDouble();
                randomY = quadCMin.getValue1() + ( quadCMax.getValue1() -  quadCMin.getValue1()) * r.nextDouble();
                break;
            default:
                randomX = quadDMin.getValue0() + ( quadDMax.getValue0() -  quadDMin.getValue0()) * r.nextDouble();
                randomY = quadDMin.getValue1() + ( quadDMax.getValue1() -  quadDMin.getValue1()) * r.nextDouble();
                break;
        }
        return Double.toString(randomX) + ";" + Double.toString(randomY);
        }
    }
