package Chinachu4j;

public class Channel {
	
	private int n, channel, sid;
	private String type, name, id;
	
	public Channel(int n, String type, int channel, String name, String id, int sid){
		this.n = n;
		this.type = type;
		this.channel = channel;
		this.name = name;
		this.id = id;
		this.sid = sid;
	}
	
	public int getN(){
		return n;
	}
	public String getType(){
		return type;
	}
	public int getChannel(){
		return channel;
	}
	public String getName(){
		return name;
	}
	public String getId(){
		return id;
	}
	public int getSid(){
		return sid;
	}
}