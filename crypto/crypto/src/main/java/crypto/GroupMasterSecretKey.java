package crypto;

public class GroupMasterSecretKey {
    private int[] xi1;
    private int[] xi2;

    public GroupMasterSecretKey() {
        xi1 = new int[8];
        xi2 = new int[8];
    }

    public int[] getXi1() {
        return xi1;
    }

    public int[] getXi2() {
        return xi2;
    }
}
