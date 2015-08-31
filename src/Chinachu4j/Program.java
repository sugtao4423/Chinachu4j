package Chinachu4j;

public class Program {
	
	private String id, category, title, subTitle, fullTitle, detail, episode;
	private long start, end, seconds;
	private String[] flags;
	private Channel channel;
	
	public Program(String id, String category, String title, String subTitle, String fullTitle, String detail, String episode,
							long start, long end, long seconds, String[] flags, Channel channel){
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
	public long getSeconds(){
		return seconds;
	}
	public String[] getFlags(){
		return flags;
	}
	public Channel getChannel(){
		return channel;
	}
}
