package sugtao4423.library.chinachu4j;

import java.io.Serializable;

public class Channel implements Serializable{

    private static final long serialVersionUID = 7396223064393983826L;

    private int n;
    private String type;
    private String channel;
    private String name;
    private String id;
    private int sid;

    public Channel(int n, String type, String channel, String name, String id, int sid){
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

    public String getChannel(){
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