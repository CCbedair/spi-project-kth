package com.datacollector.springboot.service;

import static org.junit.jupiter.api.Assertions.*;

import com.datacollector.springboot.model.ClientLog;
import com.datacollector.springboot.util.DatabaseHandler;
import crypto.SdhSignature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public class ClientLogServiceTest {

    @Autowired
    private ClientLogService clientLogService = new ClientLogService();

    @Test
    void test_extractParts() {
//        List<Object> expectedResult = new ArrayList<>();
//        expectedResult.add(59.31442546199645);
//        expectedResult.add(18.0795954724602);
//        expectedResult.add("A");
//        expectedResult.add("v4");
//        SdhSignature testSign = new SdhSignature();
//        ClientLog testClientLog = new ClientLog(1040505555, 17, "59.31442546199645;18.0795954724602", testSign);
//        assertEquals(expectedResult, clientLogService.extractParts(testClientLog));
    }

    @Test
    void addLogToDBTest(){
        /*int size = 0;
        DatabaseHandler databaseHandler = new DatabaseHandler();
        List<Object> expectedResult = new ArrayList<>();
        expectedResult.add(59.31442546199645);
        expectedResult.add(18.0795954724602);
        expectedResult.add("A");
        expectedResult.add("v4");
        databaseHandler.addToLocationsTable(expectedResult);
        size = databaseHandler.countTableSize();
        assertEquals(1, size);*/
    }

    @Test
    void getAllAreasTest(){
//        SdhSignature testSign = new SdhSignature();
//        ClientLog testClientLog = new ClientLog(1040505555, 17, "59.31442546199645;18.0795954724602", testSign);
//
//        DatabaseHandler databaseHandler = new DatabaseHandler();
//        List<Object> expectedResult = new ArrayList<>();
//        expectedResult.add(59.31442546199645);
//        expectedResult.add(18.0795954724602);
//        expectedResult.add("A");
//        expectedResult.add("v4");
//        databaseHandler.addToLocationsTable(expectedResult);
//        List<String> result = databaseHandler.getArea();
//        assertEquals("A;v4", result);
    }

}
