package com.datacollector.springboot;

import com.datacollector.springboot.controller.ClientLogController;
import com.datacollector.springboot.model.GMresponse;
import com.datacollector.springboot.service.GroupManagerService;
import com.datacollector.springboot.util.DatabaseHandler;
import crypto.GroupPublicKey;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class DataCollectorApplication implements CommandLineRunner {
    @Autowired
    GroupManagerService groupManagerService;

    public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
        DatabaseHandler dbHandler = new DatabaseHandler();
        dbHandler.initialize();
        System.out.println("Initialized called");
        SpringApplication app = new SpringApplication(DataCollectorApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
		//SpringApplication.run(DataCollectorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        GroupPublicKey gpk = groupManagerService.getGroupPublicKey();
        if (gpk == null) {
            System.err.println("fatal error in getting the group public key");
            throw new Exception("unable to get the group public key from GM");
        }
    }
}
