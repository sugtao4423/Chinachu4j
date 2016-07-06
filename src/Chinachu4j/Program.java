package Chinachu4j;

import java.io.Serializable;

public class Program implements Serializable {

	private static final long serialVersionUID = -5714630381556871901L;

	private String id;
	private String category;
	private String title;
	private String subTitle;
	private String fullTitle;
	private String detail;
	private String episode;
	private long start;
	private long end;
	private int seconds;
	private String[] flags;
	private Channel channel;

	public Program(String id, String category, String title, String subTitle, String fullTitle, String detail, String episode,
							long start, long end, int seconds, String[] flags, Channel channel){
		this.id = id;
		this.category = category;
		this.title = title;
		this.subTitle = subTitle;
		this.fullTitle = fullTitle;
		this.detail = detail;
		this.episode = episode;
		this.start = start;
		this.end = end;
		this.seconds = seconds;
		this.flags = flags;
		this.channel = channel;
	}

	public String getId(){
		return id;
	}
	public String getCategory(){
		return category;
	}
	public String getTitle(){
		return title;
	}
	public String getSubTitle(){
		return subTitle;
	}
	public String getFullTitle(){
		return fullTitle;
	}
	public String getDetail(){
		return detail;
	}
	public String getEpisode(){
		return episode;
	}
	public long getStart(){
		return start;
	}
	public long getEnd(){
		return end;
	}
	public int getSeconds(){
		return seconds;
	}
	public String[] getFlags(){
		return flags;
	}
	public Channel getChannel(){
		return channel;
	}
}
