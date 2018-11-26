package ImageCreationAndProcessing;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


public class DataObtainer 
{
    public static void main( String[] args )
    {
    	File folder = new File("C:/Users/l12082/Desktop/captures/");
    	
    	File[] listOfFiles = folder.listFiles();
    	Tesseract tesseract = new Tesseract();
    	tesseract.setDatapath("C:\\tesseract"); 
    	tesseract.setLanguage("eng");
    	
    	Pattern pt=null;
        pt = Pattern.compile("[0-9]");
        
    	for (int i = 0; i < listOfFiles.length; i++) {
    	  if (listOfFiles[i].isFile()) {
    		String time = listOfFiles[i].getName();
    	    String path = listOfFiles[i].getAbsolutePath();
    	    try {
    			String fullText = tesseract.doOCR(new File (path));
    			
    			if (fullText.length()>4) {
    				// empty space -> bye
    				fullText.trim();
    				
    				// all to lower case
    				fullText = fullText.toLowerCase();
    				
    				// if ends with whats, remove
    				String finalWatt = "";
    				
    				for (int j=0; j<fullText.length(); j++) {
    					char value = fullText.charAt(j);
    					if ((Character.isDigit(value))) {
    						finalWatt+=value;
    					}
    						
    				}
    				float firstPart = Float.parseFloat((finalWatt));
    				if (firstPart>200.0) {
    					firstPart = firstPart /100;
    					if (firstPart>200.0) firstPart = firstPart /10; 
    				}
    				
    				 
    				System.out.println("Inst. Watt Consum. "+firstPart+ " ON TIME: "+time.substring(0, (time.length()-4)));
    			}
    		} catch (TesseractException e) {
    			// TODO Auto-generated catch block
    			System.out.println(" NO TEXT IMAGE ");
    		}
    	  }
    	
    	}
    }
}
