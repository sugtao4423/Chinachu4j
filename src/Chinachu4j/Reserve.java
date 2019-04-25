package Chinachu4j;

import java.io.Serializable;

public class Reserve implements Serializable{

    private static final long serialVersionUID = -2319020405784068415L;

    private Program program;
    private boolean isManualReserved;
    private boolean isConflict;
    private String recordedFormat;
    private boolean isSkip;

    public Reserve(Program program, boolean isManualReserved, boolean isConflict, String recordedFormat, boolean isSkip){
        this.program = program;
        this.isManualReserved = isManualReserved;
        this.isConflict = isConflict;
        this.recordedFormat = recordedFormat;
        this.isSkip = isSkip;
    }

    public Program getProgram(){
        return program;
    }

    public boolean getIsManualReserved(){
        return isManualReserved;
    }

    public boolean getIsConflict(){
        return isConflict;
    }

    public String getRecordedFormat(){
        return recordedFormat;
    }

    public boolean getIsSkip(){
        return isSkip;
    }

}