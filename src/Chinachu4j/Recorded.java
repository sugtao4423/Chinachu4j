package Chinachu4j;

import java.io.Serializable;

public class Recorded implements Serializable {

	private static final long serialVersionUID = -6697338348418052209L;

	private Program program;
	private boolean isManualReserved;
	private boolean isConflict;
	private String recordedFormat;
	private boolean isSigTerm;
	private Tuner tuner;
	private String recorded;
	private String command;
	
	public Recorded(Program program, boolean isManualReserved, boolean isConflict, String recordedFormat, boolean isSigTerm,
			Tuner tuner, String recorded, String command){
		this.program = program;
		this.isManualReserved = isManualReserved;
		this.isConflict = isConflict;
		this.recordedFormat = recordedFormat;
		this.isSigTerm = isSigTerm;
		this.tuner = tuner;
		this.recorded = recorded;
		this.command = command;
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
	public boolean getIsSigTerm(){
		return isSigTerm;
	}
	public Tuner getTuner(){
		return tuner;
	}
	public String getRecorded(){
		return recorded;
	}
	public String getCommand(){
		return command;
	}
}