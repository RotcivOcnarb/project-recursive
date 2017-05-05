package com.digitalmatrix.pack.recursive;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.digitalmatrix.pack.recursive.devanalysis.ErrorElement;
import com.digitalmatrix.pack.recursive.states.GameState;
import com.digitalmatrix.pack.recursive.states.Manager;

public class RecursiveGameSetup extends ApplicationAdapter {
	SpriteBatch batch;
	Manager manager;
	public static InputMultiplexer processor; 

	public void create () {
		try{
		processor = new InputMultiplexer();
		batch = new SpriteBatch();
		manager = new Manager(batch);
		manager.create();
		Gdx.input.setCursorCatched(true);
		Gdx.input.setInputProcessor(processor);
		
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			for(StackTraceElement st : e.getStackTrace()){
				System.out.println("Error in class " + st.getClassName() + ", method " + st.getMethodName() + " line " + st.getLineNumber());
			}
			System.exit(1);
		}
	}

	@Override
	public void render () {
		try{
		manager.update(Gdx.graphics.getDeltaTime());
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		manager.render(batch);
		}
		catch(Exception e){
			
			int totalLayers = 0;
			int activeLayer = -1;
			String currentLayerState = null;
			String level = "";
			if(manager.getState() instanceof GameState){
				GameState gs = (GameState) manager.getState();
				totalLayers = gs.countTotal();
				if(gs.countTotal() > 0){
					activeLayer = gs.countBackward();
					currentLayerState = gs.current.getState().getClass().getName();
					level = gs.current.level;
				}
				
			}
			
			
			ErrorElement error = new ErrorElement(
					e.getClass().getSimpleName(),
					manager.getState().getClass().getSimpleName(),
					totalLayers,
					activeLayer,
					currentLayerState,
					e.getStackTrace(),
					"",
					level
					);
			
			
			
			System.out.println(error.toString());
			
			error.sendToDev();
			
			Gdx.app.exit();
			
			//JOptionPane.showMessageDialog(null, "Oops, it seems the game has crashed\n Dont worry, the details of the crash were already\nsent do the dev (if you have internet connection),\nThanks for playing the game.\nIf this crash keeps happening, you can send\nhim a message at the official game site");

			
			System.exit(1);
		}
	}
	
	@Override
	public void resize(int width, int height){
		manager.resize(width, height);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		manager.dispose();
		
	}
}
