package com.groupmanager.springboot;

import crypto.Keys;
import crypto.Oracle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
@Slf4j
public class GroupManagerServer implements CommandLineRunner {
    // TODO: Implement last_index in database
    private final String indexConfigFile = "conf/last_index.txt";

    @Value("${gm.csr_file}")
    private String csrFileName;

    @Value("${gm.private_key_file}")
    private String privateKeyFileName;

    @Value("${gm.gm_x509}")
    private String x509FileName;

    @Value("${gm.intermediate_cert}")
    private String intermediateCertFileName;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GroupManagerServer.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
        //SpringApplication.run(GroupManagerServer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO: note -> in production, private.key should not be built in the container
        SingletonKeyHandler.getInstance().setCsrPem(new File(csrFileName));
        SingletonKeyHandler.getInstance().setPrivateKeyPem(new File(privateKeyFileName));
        SingletonKeyHandler.getInstance().setX509(new File(x509FileName));
        SingletonKeyHandler.getInstance().setIntermediateCert(new File(intermediateCertFileName));

        try {
            FileInputStream fin = new FileInputStream(indexConfigFile);
            DataInputStream din = new DataInputStream(fin);
            int index = din.readInt();

            // TODO: read from vault, give from the index

            din.close();
        }
        catch (FileNotFoundException fe) {
            log.error(fe.getMessage());
            Oracle oracle = Oracle.getInstance();

            long startTime = System.nanoTime();
            Keys keys = oracle.keyGenInit(Integer.parseInt(System.getenv("N_CLIENT")));
            log.info(String.format("N_CLIENTS " + (System.nanoTime() - startTime)/1000000)+ " " +Integer.parseInt(System.getenv("N_CLIENT")));

            log.debug("number of client: " + Integer.parseInt(System.getenv("N_CLIENT")));
            Oracle.printKeys(keys);

            SingletonKeyHandler.getInstance().setKeys(keys);

            File file = new File(indexConfigFile);
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);

            writer.write("-1");

            writer.close();

            // TODO: write to Vault
        }
    }

}
