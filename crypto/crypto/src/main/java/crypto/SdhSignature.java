package crypto;

import java.io.Serializable;

public class SdhSignature implements Serializable {
    private int[] c, s_alpha, s_beta, s_x, s_delta1, s_delta2;
    private EcPoint_Fp t1, t2, t3;
    public SdhSignature() {
        c = new int[8];
        s_alpha = new int[8];
        s_beta = new int[8];
        s_x = new int[8];
        s_delta1 = new int[8];
        s_delta2 = new int[8];

        t1 = new EcPoint_Fp();
        t2 = new EcPoint_Fp();
        t3 = new EcPoint_Fp();
    }

    public int[] getC() {
        return c;
    }

    public int[] getS_alpha() {
        return s_alpha;
    }

    public int[] getS_beta() {
        return s_beta;
    }

    public int[] getS_x() {
        return s_x;
    }

    public int[] getS_delta1() {
        return s_delta1;
    }

    public int[] getS_delta2() {
        return s_delta2;
    }

    public EcPoint_Fp getT1() {
        return t1;
    }

    public EcPoint_Fp getT2() {
        return t2;
    }

    public EcPoint_Fp getT3() {
        return t3;
    }
}
