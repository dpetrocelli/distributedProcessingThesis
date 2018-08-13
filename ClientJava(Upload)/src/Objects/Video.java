package Objects;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

// Reference byte array[] :->  http://www.rgagnon.com/javadetails/java-0542.html
public class Video implements Serializable {
	private String videoPath;
	private int videoLenght;
	private byte[] video;
	
	public Video (String videoPath) throws IOException{
		this.videoPath = videoPath;
		videoFileToStructure();
		
	}
	
	public void videoFileToStructure () throws IOException{
		// archivo 
		File myFile = new File (this.videoPath);
			
		// buffer en bytes -> enviaria x la red
		this.videoLenght = (int) myFile.length();
		this.video = new byte [this.videoLenght];
		        
		// es como voy a leer
		FileInputStream fis = new FileInputStream(myFile);
		BufferedInputStream bis = new BufferedInputStream(fis);
		// destino es el bytearray
		bis.read(this.video,0,this.video.length);
		bis.close();
		fis.close();
	}
	
	public void structureToVideoFile (String path) throws IOException{
		 FileOutputStream fos = new FileOutputStream(path);
	     BufferedOutputStream bos = new BufferedOutputStream(fos);
	     bos.write(this.video, 0 , this.videoLenght);
	     bos.flush();
	     fos.close();
	     bos.close();
	     
	}
	public int videoLenght (){
		return this.videoLenght;
	}
	
	public String getVideoPath (){
		return this.videoPath;
	}
	public byte[] getFileContent (){
		return video;
	}
	
	public void setFileContent (byte[] array){
		this.video = array;
		this.videoLenght = array.length;
	}
}