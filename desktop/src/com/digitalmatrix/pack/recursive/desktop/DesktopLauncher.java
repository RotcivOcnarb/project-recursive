package com.digitalmatrix.pack.recursive.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.digitalmatrix.pack.recursive.RecursiveGameSetup;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		boolean debug = false;
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) d.getWidth();
		config.height = (int) d.getHeight();
		config.fullscreen = true;
		config.title = "Recursive";
		
		if(debug){
			config.width = 1280;
			config.height = 720;
			config.fullscreen = false;
		}
		
		new LwjglApplication(new RecursiveGameSetup(), config);
	}
}
