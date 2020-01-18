package com.datacollector.springboot.util;

import com.datacollector.springboot.controller.ClientLogController;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private Connection connection = null;
    static Logger logger = Logger.getLogger(ClientLogController.class);

    private  long lastupdatetime = 0;

    public DatabaseHandler(){
    }

    public void initialize() {
        this.getConnection();
        this.makeLocationsTable();
    }

    public void getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully for the project");
    }

    public boolean tableExists(String tableName){
        /*try{
            this.getConnection();
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, tableName, null);
            return rs.getRow() > 0;
        }catch(Exception ex){
           ex.printStackTrace();
        }
        return false;*/
        boolean result = false;
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "LOCATIONS", null);
            if (tables.next()) {
                result = true;
            } else {
               result = false;
            }
        } catch (SQLException e){

        }
        return result;
    }

    public void makeLocationsTable(){
        Statement createStmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
            System.out.println("Opened database successfully to create the table");
            createStmt = connection.createStatement();
            if (!tableExists("LOCATIONS")) {
                String sql = "CREATE TABLE LOCATIONS (" +
                        " LONGITUDE      REAL, " +
                        " GRATITUDE      REAL, " +
                        " AREA         TEXT, " +
                        " ZONE         TEXT); ";
                createStmt.executeUpdate(sql);
            }
            createStmt.close();
            connection.close();
        } catch ( Exception e ) {
            System.out.println("Here is an error: " + e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Table Locations created successfully");
    }

    public int countTableSize(){
        Statement stmt = null;
        int size = 0;
        try {
            this.getConnection();
            System.out.println("Opened database successfully to get the size");
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) AS RESULT FROM LOCATIONS;" );

            while ( rs.next() ) {
                size = Integer.parseInt(rs.getString("RESULT"));
                System.out.println( "SIZE = " + rs.getString("RESULT"));
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return size;
    }

    public void addToLocationsTable(List<Object> clientMessageParts){

        try {
            this.getConnection();
            System.out.println("Opened database successfully to add to Locations");
            String sql = "INSERT INTO LOCATIONS (LONGITUDE, GRATITUDE, AREA, ZONE) VALUES (?, ?, ?, ?)";

            PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setDouble(1, (Double) clientMessageParts.get(0));
                pstmt.setDouble(2, (Double) clientMessageParts.get(1));
                pstmt.setString(3, (String) clientMessageParts.get(2));
                pstmt.setString(4, (String) clientMessageParts.get(3));
                pstmt.executeUpdate();
                connection.close();

        } catch ( Exception e ) {
            System.out.println("There was an error inserting into LOCATIONS "+ e.getClass().getName() + ": " + e.getMessage() );

        }

        logger.info(String.format("UPDATE_DB " + String.valueOf((System.nanoTime()- lastupdatetime)/1000000)));
        lastupdatetime = System.nanoTime();
        System.out.println("Table Locations was updated with new log successfully");
    }

    public List<JSONObject> getLocations(){
        Statement stmt = null;
        List<JSONObject> areas = new ArrayList<>();
        try {
            this.getConnection();
            System.out.println("Opened database successfully to get area");

            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT AREA, ZONE, COUNT(*) as total  FROM LOCATIONS GROUP BY AREA, ZONE" );

            while ( rs.next() ) {
                JSONObject object = new JSONObject();
                object.put("group", rs.getString("AREA"));
                object.put("variable", rs.getString("ZONE"));
                object.put("value", rs.getInt("total"));
                areas.add(object);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return areas;
    }

}