package BoostrapRat;



import java.util.Arrays;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;




/**
 * The Class SystemInfoTest.
 *
 * @author dblock[at]dblock[dot]org
 */
public class HardwareInfo {

   
     
    public static void main(String[] args) {
    	//System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO"); 
    	SystemInfo si = new SystemInfo();
    	OperatingSystem os = si.getOperatingSystem();
    	String OS = os.toString();
    	
    	HardwareAbstractionLayer hal = si.getHardware();
    	
    	/*
    	 * STEP 1 - Processor Stats
    	 * logcpu
    	 * physcpu
    	 * freq
    	 * name 
    	 */

    	int logicalCPU = hal.getProcessor().getLogicalProcessorCount();
    	int physicalCPU = hal.getProcessor().getPhysicalProcessorCount();
    	float freqGhz = (float) (hal.getProcessor().getVendorFreq()*0.000000001);
    	String procName = hal.getProcessor().getName();
    	
    	/*
    	 * STEP 2 - Memory Stats
    	
    	 */
    	float totalMemory = ((float)(hal.getMemory().getTotal())/1024/1024/1024);
    	
         }
}
