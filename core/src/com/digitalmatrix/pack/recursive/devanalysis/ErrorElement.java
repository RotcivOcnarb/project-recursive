package com.digitalmatrix.pack.recursive.devanalysis;

import java.sql.Connection;

public class ErrorElement {
	
	public String exceptionName;
	public String generalState = "";
	public int totalLayers = 0;
	public int activeLayer = -1;
	public String currentLayerState = "";
	float FPS = -1;
	String userText = "";
	StackTraceElement[] stackTrace = null;
	String level;
	
	static Connection conn;
	
	public ErrorElement(String exceptionName, String generalState, int totalLayers, int activeLayer, String currentLayerState, StackTraceElement[] stackTrace, String userText, String level){
		this.exceptionName = exceptionName;
		this.level = level;
		this.generalState = generalState;
		this.totalLayers = totalLayers;
		this.activeLayer = activeLayer;
		this.currentLayerState = currentLayerState;
		this.stackTrace = stackTrace;
		this.userText = userText;
	}
	
	public void set(String exceptionName, String generalState, int totalLayers, int activeLayer, String currentLayerState, float FPS, StackTraceElement[] stackTrace, String userText, String level){
		this.exceptionName = exceptionName;
		this.level = level;
		this.generalState = generalState;
		this.totalLayers = totalLayers;
		this.activeLayer = activeLayer;
		this.currentLayerState = currentLayerState;
		this.stackTrace = stackTrace;
		this.userText = userText;
	}
	
	public void set(String actionSlow, float FPS, int totalLayers, int activeLayer, String level){
		this.exceptionName = actionSlow + " too slow";
		this.level = level;
		this.FPS = FPS;
		this.totalLayers = totalLayers;
		this.activeLayer = activeLayer;
		stackTrace = null;
	}
	
	public ErrorElement(String actionSlow, float FPS, int totalLayers, int activeLayer, String level){
		this.exceptionName = actionSlow + " too slow";
		this.level = level;
		this.FPS = FPS;
		this.totalLayers = totalLayers;
		this.activeLayer = activeLayer;
	}
	
	public void sendToDev(){
		if(conn == null){
			conn = ConnectionFactory.getConnection();
		}
		if(conn != null){
			if(stackTrace != null)
				ConnectionFactory.sendCrashError(this);
			else
				ConnectionFactory.sendSlowError(this);
		}
	}
	
	public String toString(){
		String text = "";
		if(stackTrace != null){
		
			text += "Exception: " + exceptionName + "\n";
			text += "General state: " + generalState + "\n";
			if(generalState.equals("GameState")){
				text += totalLayers + " recursive layers in total\n";
				if(totalLayers > 0){
					text += "Active layer: " + activeLayer + "\n";
					text += "Current layer state: " + currentLayerState + "\n";
					text += "Level: " + level;
				}
			}
			text += "Stack trace: \n";
			text += stackTraceText();
		}
		else{
			text += exceptionName + "\n";
			text += "Total layers: " + totalLayers + "\n";
			text += "Active layer: " + activeLayer + "\n";
			text += "Level: " + level;
		}
		
		return text;
		
	}

	public String stackTraceText() {
		String text = "";
		for(StackTraceElement st : stackTrace){
			String className = st.getClassName();
			text += "Error in class " + className.split("\\.")[className.split("\\.").length-1] + ", method " + st.getMethodName() + " line " + st.getLineNumber() + "\n";
		}
		return text;
	}

}
