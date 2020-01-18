package crypto;

import java.io.Serializable;

public class GroupPublicKey implements Serializable {
    private EcPoint_Fp g1;
    private EcPoint_Fp h;
    private EcPoint_Fp u;
    private EcPoint_Fp v;
    private EcPoint_Fp2 g2;
    private EcPoint_Fp2 w;

    public GroupPublicKey() {
        g1 = new EcPoint_Fp();
        h = new EcPoint_Fp();
        u = new EcPoint_Fp();
        v = new EcPoint_Fp();
        g2 = new EcPoint_Fp2();
        w = new EcPoint_Fp2();
    }

    public EcPoint_Fp getG1() {
        return g1;
    }

    public EcPoint_Fp getH() {
        return h;
    }

    public EcPoint_Fp getU() {
        return u;
    }

    public EcPoint_Fp getV() {
        return v;
    }

    public EcPoint_Fp2 getG2() {
        return g2;
    }

    public EcPoint_Fp2 getW() {
        return w;
    }
}
