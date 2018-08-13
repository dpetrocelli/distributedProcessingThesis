package ClientSide;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Objects.Video;

public class TrascoderThread implements Runnable{
	Socket s;
	ObjectOutputStream out;
	ObjectInputStream in;
	String ffmpegPath;
	
	public TrascoderThread (Socket sock, String ffmpegPath){
		this.s = sock;
		this.ffmpegPath = ffmpegPath;
	}
	
	@Override
	public void run() {
		// Here is listening as a Server (receiving video file and trascode)
		while (true){
					
					try{
						
						this.out = new ObjectOutputStream (this.s.getOutputStream());
						this.in = new ObjectInputStream (this.s.getInputStream());
						
						// Read Remote Object (from server)
						Video receivedVideo = (Video) in.readObject();
						
						// Obtain data object
						String videoPath = (receivedVideo.getVideoPath().split("/"))[1];
						videoPath = "src/trans-"+videoPath;
						System.out.println("Received video is: "+receivedVideo.videoLenght());
						
						// Save received video as a file.  Then use FFMPEG
						receivedVideo.structureToVideoFile(videoPath);
						
						String result = videoPath.split("/")[1];
						result = "src/result-"+result;
						// now transcode
						File f = new File(videoPath);
					  	if (!(f.exists()&&f.isFile())) {
					  		System.out.println("Incorrect path or not a file");
					  	}else{
					  		// transrate video File
					  		String params = this.ffmpegPath+"ffmpeg -loglevel quiet -y -i "+videoPath+" -s 320x180 -aspect 16:9 -c:v libx264 -g 50 -b:v 220k -profile:v baseline -level 3.0 -r 15 -preset ultrafast -threads 0 -c:a aac -strict experimental -b:a 64k -ar 44100 -ac 2 "+result;
					  		try {
								Process powerShellProcess = Runtime.getRuntime().exec(params);
					  			System.out.println("TRANSCODING "+params);
								powerShellProcess.waitFor();
					  			// after finish -> read object (byte array) -> send via socket
								Video transcodedVideo = new Video(result);
								out.writeObject(transcodedVideo);
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					  	}
					  	System.out.println("video transcoded and returned");
					  	
						
					}catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			}

}
