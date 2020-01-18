package crypto;

import java.io.Serializable;

public class GroupSecretKey implements Serializable {
    private EcPoint_Fp a;
    private int[] x;

    public GroupSecretKey() {
        a = new EcPoint_Fp();
        x = new int[8];
    }

    // public GroupSecretKey(int[] x, int[] A_x, int[] A_y){
    //     this.x = x;
    //     A = new EcPoint_Fp(A_x, A_y, (byte)0);
    // }


    public EcPoint_Fp getA() {
        return a;
    }

    public int[] getX() {
        return x;
    }
}