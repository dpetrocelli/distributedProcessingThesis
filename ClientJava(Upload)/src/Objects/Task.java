package Objects;

import java.io.Serializable;

public class Task implements Serializable{
	public String client;
	public String parameters;
	public Video videoPart;
	public String state;
	public String videoName;
	
	public Task (String client, String parameters, Video videoPart, String state, String videoName){
		this.client = client;
		this.parameters = parameters;
		this.videoName=videoName;
		this.videoPart= videoPart;
		this.state = state;
	}
	
	
}
