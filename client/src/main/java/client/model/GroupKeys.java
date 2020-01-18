package client.model;

import client.controller.GmController;
import crypto.GroupPublicKey;
import crypto.GroupSecretKey;
import crypto.Keys;
import crypto.Oracle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GroupKeys {
    private GroupPublicKey groupPublicKey;
    private GroupSecretKey groupSecretKey;

    public GroupKeys(boolean MIDTERM_DEMO) {

        GmController gmController = new GmController();

        try {
            Keys k;
            // We check if we are in Midterm demo mode
            if (MIDTERM_DEMO) {
                System.out.println("MIDTERM_DEMO");
                // If we are in Midterm demo mode we generate our own keys,
                Oracle o = Oracle.getInstance();
                k = o.keyGenInit(10);
                k = o.keyGenInit(10);
                setGroupPublicKey(k.getGpk());
                setGroupSecretKey(k.getGsk()[0]);
            } else {
                System.out.println("TO GM");
                // We are not in Midterm demo mode, so we register to the GM conventionally
                GmResponse gmResponse = gmController.registerToGm("my x509");
                setGroupPublicKey(gmResponse.getGpk());
                setGroupSecretKey(gmResponse.getGsk());
                k = new Keys();
                k.setGpk(gmResponse.getGpk());
                GroupSecretKey gsk[] = {gmResponse.getGsk()};
                k.setGsk(gsk);
            }
            // We print the keys
            Oracle.printKeys(k);

        } catch (GmController.GmException e) {
            System.err.println("GmException: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
