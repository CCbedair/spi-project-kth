package crypto;

import java.io.Serializable;

class EcPoint_Fp2 implements Serializable {
    private int[][] x = new int[2][8];
    private int[][] y = new int[2][8];
    private byte infinity;

    public EcPoint_Fp2() {
        infinity = (byte)0;
    }

    public EcPoint_Fp2(int[][] x, int[][] y, byte infinity) {
        this.x = x;
        this.y = y;
        this.infinity = infinity;
    }

    public int[][] getX() {
        return x;
    }

    public int[][] getY() {
        return y;
    }

    public byte getInfinity() {
        return infinity;
    }
}
