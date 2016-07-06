package Chinachu4j;

import java.io.Serializable;

public class Rule implements Serializable {

	private static final long serialVersionUID = 9079891690081352420L;

	private String[] types;
	private String[] categories;
	private String[] channels;
	private String[] ignore_channels;
	private String[] reserve_flags;
	private String[] ignore_flags;
	private int start;
	private int end;
	private int min;
	private int max;
	private String[] reserve_titles;
	private String[] ignore_titles;
	private String[] reserve_descriptions;
	private String[] ignore_descriptions;
	private String recorded_format;
	private boolean isDisabled;

	public Rule(String[] types, String[] categories, String[] channels, String[] ignore_channels, 
			String[] reserve_flags, String[] ignore_flags,
			int start, int end, int min, int max, String[] reserve_titles, String[] ignore_titles,
			String[] reserve_descriptions, String[] ignore_descriptions, String recorded_format, boolean isDisabled){
		this.types = types;
		this.categories = categories;
		this.channels = channels;
		this.ignore_channels = ignore_channels;
		this.reserve_flags = reserve_flags;
		this.ignore_flags = ignore_flags;
		this.start = start;
		this.end = end;
		this.min = min;
		this.max = max;
		this.reserve_titles = reserve_titles;
		this.ignore_titles = ignore_titles;
		this.reserve_descriptions = reserve_descriptions;
		this.ignore_descriptions = ignore_descriptions;
		this.recorded_format = recorded_format;
		this.isDisabled = isDisabled;
	}

	public String[] getTypes(){
		return types;
	}
	public String[] getCategories(){
		return categories;
	}
	public String[] getChannels(){
		return channels;
	}
	public String[] getIgnore_channels(){
		return ignore_channels;
	}
	public String[] getReserve_flags(){
		return reserve_flags;
	}
	public String[] getIgnore_flags(){
		return ignore_flags;
	}
	public int getStart(){
		return start;
	}
	public int getEnd(){
		return end;
	}
	public int getMin(){
		return min;
	}
	public int getMax(){
		return max;
	}
	public String[] getReserve_titles(){
		return reserve_titles;
	}
	public String[] getIgnore_titles(){
		return ignore_titles;
	}
	public String[] getReserve_descriptions(){
		return reserve_descriptions;
	}
	public String[] getIgnore_descriptions(){
		return ignore_descriptions;
	}
	public String getRecorded_format(){
		return recorded_format;
	}
	public boolean getIsDisabled(){
		return isDisabled;
	}
}