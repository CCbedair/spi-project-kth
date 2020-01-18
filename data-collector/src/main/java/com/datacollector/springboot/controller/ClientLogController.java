package com.datacollector.springboot.controller;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import com.datacollector.springboot.model.ClientLog;
import com.datacollector.springboot.model.HybridClientLog;
import com.datacollector.springboot.model.ResponsePOJO;
import com.datacollector.springboot.service.ClientLogService;
import com.datacollector.springboot.service.GroupManagerService;
import crypto.Keys;
import crypto.Oracle;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

//import lombok.extern.slf4j.Slf4j;
//import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

@RestController
//@Slf4j
public class ClientLogController {

	@Autowired
	private ClientLogService clientLogService;

    @Autowired
    private GroupManagerService groupManagerService;

	public final static String SUCCESS = "Success";
	public final static String FAILURE = "Failure";

    static Logger logger = Logger.getLogger(ClientLogController.class);

	/*
	*
	* The client send a message where the location longitude and gratitude have a form "longitude;gratitude"
	*
	*/


	@GetMapping(value = "/locations/", produces = "application/json")
	public String getLocationsAjax() {
		List<JSONObject> locations = clientLogService.getLocationObjects();
		return locations.toString();
	}

	@PostMapping(path = "/locations/", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public String addClientLog(@RequestBody ClientLog clientLog) throws Exception {
		String gms = System.getenv("GENERAL_MESSAGE");
        JSONObject result = new JSONObject();
		System.out.println(clientLog.getMessage());
		//System.out.println(clientLog.getTimestamp());

		if (groupManagerService.getGroupPublicKey() == null) {
			return result.put("status", "The group manager didn't send a group key").toString();
		}
		if (clientLog == null) {
			return result.put("status", "Client didn't send the proper log").toString();
		}

		Oracle oracle = Oracle.getInstance();
		Keys key = new Keys();
		key.setGpk(groupManagerService.getGroupPublicKey());
		Oracle.printKeys(key);
		long startTime = System.nanoTime();

		if (oracle.verify(clientLog.getMessage(), groupManagerService.getGroupPublicKey(),
				clientLog.getClientSignature()) == 0) {
			if (gms.equals("NO")){
				clientLogService.addClientLog(clientLog);
				logger.info(String.format("VERIFY_GS " + (System.nanoTime() - startTime)/1000000));
			}
			else
			{
				logger.info(String.format("VERIFY_GS " + (System.nanoTime() - startTime)/1000000 + " " + clientLog.getMessage().length()));
			}
			result.put("status", SUCCESS);
			System.out.println("VERIFICATION SUCCESSFUL!");
		}
		else {
			result.put("status", FAILURE);
			System.out.println("VERIFICATION FAILED!");
		}

        return result.toString();
	}

	/**
	 * POST /locations/hybrid
	 *
	 * This controller handler they location (using hybrid keys) endpoint.
	 * This endpoint is meant to be used by Clients. The client would send his location,
	 * signed by a user generated key, following the hybrid scheme.
	 *
	 * Request:
	 *
	 * {
	 *		message: <String>
	 *		clientGeneratedPublicKeySignature: <SdhSignature>
	 *		messageSignature: <String>
	 *		clientGeneratedPublicKey: <String>
	 *		scheme: <Oracle.Scheme >
	 * }
	 *
	 * Response:
	 *
	 *  // TODO: Add example response
	 * 200 OK { status: "SUCCESS" }
	 * 400 Bad Request { status: <String> }
	 * 				   { status: "Missing Parameter"}: Request is missing a required parameter.
	 * 				   { status: "Bad Key Spec/Algo"}: scheme/clientGeneratedPublicKey are malformed/invalid.
	 * 				   { status: "Invalid Key/Signature"}: messageSignature is malformed
	 * 401 Unauthorized { status: "Verification failed" }
	 * 503 Service Unavailable { status: "GPK not found!" }
	 *
	 * @return Serialized ResponsePOJO
	 */

	@PostMapping(path = "/locations/hybrid", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ResponsePOJO> addHybridClientLog(@RequestBody HybridClientLog hybridClientLog) {
		String gms = System.getenv("GENERAL_MESSAGE");
		ResponsePOJO response = new ResponsePOJO();
		Integer statusCode;
		response.setStatus("SUCCESS");
		statusCode = 200;
		// TODO: Change response code to 503 Unavailable and message shouldn't have unnecessary information
		if (groupManagerService.getGroupPublicKey() == null) {
			statusCode = 503;
			response.setStatus("GPK not found!");
		} else if (
			hybridClientLog.getMessage() == null||
			hybridClientLog.getMessageSignature() == null||
			hybridClientLog.getClientGeneratedPublicKeySignature() == null ||
			hybridClientLog.getScheme()  == null
		) {
			statusCode = 400;
			response.setStatus("Missing Parameter");
		} else {
			long startTime = System.nanoTime();
			// TODO: DO NOT CHECK CLIENT LOG FOR NULL
			try {
				int verificationStatus;
				verificationStatus = Oracle.getInstance().verify(
						hybridClientLog.getMessage(),
						hybridClientLog.getMessageSignature(),
						groupManagerService.getGroupPublicKey(),
						hybridClientLog.getClientGeneratedPublicKey(),
						hybridClientLog.getClientGeneratedPublicKeySignature(),
						hybridClientLog.getScheme()
				);
				if (verificationStatus == -1) {
					statusCode = 401;
					response.setStatus("Verification failed");
				}
				if (gms.equals("YES"))
				{
					logger.info(String.format("VERIFY_HY " + (System.nanoTime() - startTime)/1000000 + " " + hybridClientLog.getMessage().length()));
				}
				else {
					ClientLog clientLog = new ClientLog();
					clientLog.setMessage(hybridClientLog.getMessage());
					clientLogService.addClientLog(clientLog);
					logger.info(String.format("VERIFY_HY " + (System.nanoTime() - startTime)/1000000));
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				// TODO: Stack Trace in Logs...or ignore
				e.printStackTrace();
				statusCode = 400;
				response.setStatus("Bad Key Spec/Algo");
			} catch (InvalidKeyException | SignatureException e) {
				statusCode = 400;
				response.setStatus("Invalid Key/Signature");
				e.printStackTrace();
			}
		}
		return ResponseEntity.status(statusCode).body(response);
	}

	@GetMapping("/")
	public ModelAndView plotAllClients() throws Exception {
		ModelAndView mv = new ModelAndView();
		long startTime = System.nanoTime();
		// assign template
		mv.setViewName("clientlogs");
		// add context variables to template
		mv.getModel().put("areazone", clientLogService.getLocationObjects());
		return mv;
	}
}
