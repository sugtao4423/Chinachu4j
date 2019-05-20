package Chinachu4j;

import java.io.Serializable;

public class Rule implements Serializable{

    private static final long serialVersionUID = 9079891690081352420L;

    private String[] types;
    private String[] categories;
    private String[] channels;
    private String[] ignoreChannels;
    private String[] reserveFlags;
    private String[] ignoreFlags;
    private int start;
    private int end;
    private int min;
    private int max;
    private String[] reserveTitles;
    private String[] ignoreTitles;
    private String[] reserveDescriptions;
    private String[] ignoreDescriptions;
    private String recordedFormat;
    private boolean isDisabled;

    public Rule(String[] types, String[] categories, String[] channels, String[] ignoreChannels,
                String[] reserveFlags, String[] ignoreFlags,
                int start, int end, int min, int max, String[] reserveTitles, String[] ignoreTitles,
                String[] reserveDescriptions, String[] ignoreDescriptions, String recordedFormat, boolean isDisabled){
        this.types = types;
        this.categories = categories;
        this.channels = channels;
        this.ignoreChannels = ignoreChannels;
        this.reserveFlags = reserveFlags;
        this.ignoreFlags = ignoreFlags;
        this.start = start;
        this.end = end;
        this.min = min;
        this.max = max;
        this.reserveTitles = reserveTitles;
        this.ignoreTitles = ignoreTitles;
        this.reserveDescriptions = reserveDescriptions;
        this.ignoreDescriptions = ignoreDescriptions;
        this.recordedFormat = recordedFormat;
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

    public String[] getIgnoreChannels(){
        return ignoreChannels;
    }

    public String[] getReserveFlags(){
        return reserveFlags;
    }

    public String[] getIgnoreFlags(){
        return ignoreFlags;
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

    public String[] getReserveTitles(){
        return reserveTitles;
    }

    public String[] getIgnoreTitles(){
        return ignoreTitles;
    }

    public String[] getReserveDescriptions(){
        return reserveDescriptions;
    }

    public String[] getIgnoreDescriptions(){
        return ignoreDescriptions;
    }

    public String getRecordedFormat(){
        return recordedFormat;
    }

    public boolean getIsDisabled(){
        return isDisabled;
    }

}