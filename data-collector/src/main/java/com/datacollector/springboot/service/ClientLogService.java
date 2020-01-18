package com.datacollector.springboot.service;

import java.util.ArrayList;
import java.util.List;

import com.datacollector.springboot.model.ClientLog;
import com.datacollector.springboot.model.Coordinate;
import com.datacollector.springboot.util.DCPolygon;
import com.datacollector.springboot.util.DatabaseHandler;
import com.datacollector.springboot.util.PolygonConstants;

import com.datacollector.springboot.util.PolygonUtil;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class ClientLogService {

//	private static List<ClientLog> clientLogs = new ArrayList<>();
	private PolygonUtil polygonUtil = new PolygonUtil();

	private DatabaseHandler databaseHandler = new DatabaseHandler();

    public void addClientLog(ClientLog clientLog){
//		clientLogs.add(clientLog);
        databaseHandler.addToLocationsTable(extractParts(clientLog));
    }

    public int getLocationsCount() {
    	return databaseHandler.countTableSize();
	}

	public List<Object> extractParts(ClientLog clientLog){
    	List<Object> result = new ArrayList<>();
		String parts[] = clientLog.getMessage().split(";");
		double longitude = 0.0;
		double graditude = 0.0;
		try {
			longitude = Double.valueOf(parts[0]);
			graditude = Double.valueOf(parts[1]);
			result.add(longitude);
			result.add(graditude);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DCPolygon polygon = null;
		polygon = polygonUtil.getWhichPolygon(new Coordinate(longitude, graditude));
		String area = "";
		area = polygon.getLabel();
		result.add(area);
		String zone = "";
		zone = polygon.getVZone(longitude, graditude);
		result.add(zone);
		return result;
	}

	public List<JSONObject> getLocationObjects(){
		return databaseHandler.getLocations();
	}

}