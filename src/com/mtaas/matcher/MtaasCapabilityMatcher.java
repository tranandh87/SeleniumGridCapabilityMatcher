package com.mtaas.matcher;

import java.util.Map;
import java.util.logging.Logger;

import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;
 
public class MtaasCapabilityMatcher extends DefaultCapabilityMatcher {
    private static final String MODEL = "model";
	private static final String MANUFACTURER = "manufacturer";
	private static final String IS_EMULATOR = "isEmulator";
	private static final Logger log = Logger.getLogger(MtaasCapabilityMatcher.class.getName());
    @Override
    public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
    	try {
    		
    	log.info("inside custom matcher");
        boolean basicChecks = super.matches(nodeCapability, requestedCapability);
        //basic checks are browserName, device, platform, version
        
        if (!basicChecks){
        	//if basic check already failed, no need to check for further capabilities
        	log.info("basic check failed, returning");
        	return basicChecks;
        }
        
        log.info("basic checks passed, performing custom checks");

        //if isEmulator = true, then just check whether the current node is emulator, ignore manufacturer and model
        //if isEmulator = false, then have to check the manufacturer & model
        
        boolean isEmulatorRequested = ((Boolean) requestedCapability.get(IS_EMULATOR)).booleanValue();
        
        boolean isEmulatorNode = ((Boolean) nodeCapability.get(IS_EMULATOR)).booleanValue();
        if (isEmulatorRequested){
        	log.info("isEmulatorRequested:" + isEmulatorRequested + "; isEmulatorNode:" + isEmulatorNode);
        	return isEmulatorNode;
        }
        else {
        	log.info("real device requested");
        	
        	//real device requested, check that:
        	//1. current node is NOT emulator
        	//2. check for matching model & manufacturer
        	if (isEmulatorNode){
        		log.info("real device requested, but this is an emulator node, no match");
        		return false;
        	}
        	
        	
        	String requestedManufacturer = (String) requestedCapability.get(MANUFACTURER);
        	String requestedModel = (String) requestedCapability.get(MODEL);
        	if (requestedManufacturer == null || requestedModel == null){
        		log.info("real device requested, but manufacturer or model not specified, no match");
        		return false;
        	}
        	
        	String nodeManufacturer = (String) nodeCapability.get(MANUFACTURER);
        	String nodeModel = (String) nodeCapability.get(MODEL);
        	
        	log.info(String.format("requested manufacturer:%s, nodeManufacturer:%s, requestedModel:%s, nodeModel:%s",
        			requestedManufacturer, nodeManufacturer, requestedModel, nodeModel));
        	
        	boolean isMatch = nodeManufacturer.equalsIgnoreCase(requestedManufacturer) && nodeModel.equalsIgnoreCase(requestedModel);
        	
        	log.info("isMatch: " + isMatch);
        	return isMatch;
        }
        
        
    	} catch (Exception e){
    		log.severe(e.getMessage());
    		return false;
    	}
    	
    	
    }
 
}