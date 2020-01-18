package crypto;

import java.io.Serializable;

public class Keys implements Serializable {
    private GroupPublicKey gpk;
    private GroupSecretKey gsk[];
    private GroupMasterSecretKey gmsk;

    public Keys() {}

    public Keys(int numberOfMembers) {
        gpk = new GroupPublicKey();
        gmsk = new GroupMasterSecretKey();
        gsk = new GroupSecretKey[numberOfMembers];
        for (int i = 0; i < numberOfMembers; i++) {
            gsk[i] = new GroupSecretKey();
        }
    }

    public GroupPublicKey getGpk() {
        return gpk;
    }

    public void setGpk(GroupPublicKey gpk) {
        this.gpk = gpk;
    }

    public GroupSecretKey[] getGsk() {
        return gsk;
    }

    public void setGsk(GroupSecretKey[] gsk) {
        this.gsk = gsk;
    }

    public GroupMasterSecretKey getGmsk() {
        return gmsk;
    }

    public void setGmsk(GroupMasterSecretKey gmsk) {
        this.gmsk = gmsk;
    }
}
