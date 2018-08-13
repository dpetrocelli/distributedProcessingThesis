package com.example.demo;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class UtilBase64 {
	public static String encoder(String path) {
		System.out.println("PATH IS: "+path);
				
	    File file = new File(path);
	    try (FileInputStream InFile = new FileInputStream(file)) {
	        // Reading a Image file from file system
	    	String base64Result = "";
	        byte Data[] = new byte[(int) file.length()];
	        InFile.read(Data);
	        base64Result = Base64.getEncoder().encodeToString(Data);
	        return base64Result;
	    } catch (FileNotFoundException e) {
	        System.out.println("Resource not found" + e);
	    } catch (IOException ioe) {
	        System.out.println("Exception while reading the FILE " + ioe);
	    }
	    return null;
	     
	}
	
	public static String encoder2 (String path) {
		
		String encoded = null;
		File file = new File(path);
		byte[] bytes;
		try {
			bytes = loadFile(file);
			encoded = Base64.getEncoder().encodeToString(bytes);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encoded;
	}

	private static byte[] loadFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	    byte[] bytes = new byte[(int)length];
	    
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    is.close();
	    return bytes;
	}
		
	
	public static void decoder(String base64Image, String pathFile) {
	    try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
	        // Converting a Base64 String into Image byte array
	        byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
	        imageOutFile.write(imageByteArray);
	    } catch (FileNotFoundException e) {
	        System.out.println("Image not found" + e);
	    } catch (IOException ioe) {
	        System.out.println("Exception while reading the Image " + ioe);
	    }
	}
}