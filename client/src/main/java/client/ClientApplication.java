package client;

import client.controller.DataCollServerController;
import client.controller.GmController;
import client.model.GmResponse;
import client.utils.ClientKeys;
import client.utils.MockDataGen;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ClientApplication {
    static Logger logger = Logger.getLogger(ClientApplication.class);

    public static void main(String[] args) {
        String log4jConfPath = "/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        DataCollServerController dcsController = new DataCollServerController();

        try {
            // TODO: note -> in production, private.key should not be built in the container
            ClientKeys.getInstance().setPrivateKeyPem(new File("private.key"));
            ClientKeys.getInstance().setX509Pem(new File("myx509.pem"));

            GmController gmController = new GmController();
            long startTime = System.currentTimeMillis();
            GmResponse gmResponse = gmController.registerToGm(ClientKeys.getInstance().getX509Pem());
            //for the client to register to GM (2nd column)
            logger.info("REGISTER_CLIENT_GM " + (System.currentTimeMillis() - startTime));

            ClientKeys.getInstance().setGroupPublicKey(gmResponse.getGpk());
            ClientKeys.getInstance().setGroupSecretKey(gmResponse.getGsk());
            System.out.println(ClientKeys.getInstance().getGroupPublicKey().getG1());

            String gms = System.getenv("GENERAL_MESSAGE");

            if (gms.equals("YES")) {
                MockDataGen mdg = new MockDataGen();

                String message = mdg.genRandData();
                String scheme = System.getenv("SCHEME");
                if (scheme.equals("HYBRID")) {
                    dcsController.sendDataHybridScheme(message, ClientKeys.getInstance().getGroupPublicKey(),
                            ClientKeys.getInstance().getGroupSecretKey(), ClientKeys.getInstance().getKeyPair());
                }
                else{
                    dcsController.sendDataGroupScheme(message, ClientKeys.getInstance().getGroupPublicKey(),
                       ClientKeys.getInstance().getGroupSecretKey());
                }

                System.out.println("Sending Data: " + message);

                String generalmessage = mdg.genRandData();
                for (int i = 0; i < 25; i++) {
                    dcsController.sendDataGroupScheme(generalmessage, ClientKeys.getInstance().getGroupPublicKey(),
                            ClientKeys.getInstance().getGroupSecretKey());
                    System.out.println("Sending Data of length from GS Scheme: " + generalmessage.length());

                    TimeUnit.SECONDS.sleep(2);

                    dcsController.sendDataHybridScheme(generalmessage, ClientKeys.getInstance().getGroupPublicKey(),
                            ClientKeys.getInstance().getGroupSecretKey(), ClientKeys.getInstance().getKeyPair());
                    System.out.println("Sending Data of length from Hybrid Scheme: " + generalmessage.length());

                    TimeUnit.SECONDS.sleep(2);

                    generalmessage = generalmessage + generalmessage;
                }
            }
            else {
                while (true) {
                    MockDataGen mdg = new MockDataGen();
                    String message = mdg.genRandData();
                    String scheme = System.getenv("SCHEME");
                    if (scheme.equals("HYBRID")) {
                        dcsController.sendDataHybridScheme(message, ClientKeys.getInstance().getGroupPublicKey(),
                                ClientKeys.getInstance().getGroupSecretKey(), ClientKeys.getInstance().getKeyPair());
                    }
                    else{
                        dcsController.sendDataGroupScheme(message, ClientKeys.getInstance().getGroupPublicKey(),
                                ClientKeys.getInstance().getGroupSecretKey());
                    }

                    System.out.println("Sending Data: " + message);


                    TimeUnit.SECONDS.sleep(2);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass());
            System.err.println(e.getMessage());
        }
    }
}
