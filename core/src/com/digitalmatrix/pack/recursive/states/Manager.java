package com.digitalmatrix.pack.recursive.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.digitalmatrix.pack.recursive.objects.Assets;

public class Manager implements ControllerListener{
	
	ArrayList<State> states = new ArrayList<State>();
	int currentState = CUTSCENE1;
	public Assets assets;
	
	float opacity = 1;
	
	ShapeRenderer srenderer;
	Matrix4 normalProjection;
	
	boolean intro = true;
	boolean outro = false;
	int nextState = currentState;
	
	public static final int GAMESTATE = 0;
	public static final int INPUTCONFIGSTATE = 1;
	public static final int SELECTLEVEL = 2;
	public static final int MAINMENU = 3;
	public static final int CUTSCENE1 = 4;
	public static final int CONTROLSMENU = 5;
	
	public Manager(SpriteBatch sb){
		assets = new Assets();

		states = new ArrayList<State>();
		
		states.add(new GameState(this, sb));  //0
		states.add(new InputConfigState(this));  //1
		states.add(new SelectLevel(this));  //2
		states.add(new MainMenu(this));  //3
		states.add(new Cutscene1(this));  //4
		states.add(new ControlsMenu(this)); //5
		
		Controllers.addListener(this);
		
		srenderer = new ShapeRenderer();
		
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());

	}
	
	public void create(){
		states.get(currentState).create();
	}
	Color tempC = Color.WHITE;
	public void render(SpriteBatch sb){
		if(assets.manager.update()){
			
			if(intro){
				opacity -= Gdx.graphics.getDeltaTime()*2f;
				
				if(opacity < 0){
					opacity = 0;
					intro = false;
				}
			}
			if(outro){
				opacity += Gdx.graphics.getDeltaTime()*2f;
				
				if(opacity > 1){
					opacity = 1;
					outro = false;
					intro = true;
					dispose();
					currentState = nextState;
					create();
				}
			}
			
			
			states.get(currentState).render(sb);
			
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			tempC.set(0, 0, 0, opacity);
			srenderer.setProjectionMatrix(normalProjection);
			srenderer.begin(ShapeType.Filled);
			srenderer.setColor(tempC);
			srenderer.box(0, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
			srenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
			
			tempC.set(1, 1, 1, 1);
			sb.setColor(tempC);
		}
		else{
			//loading screen
		}
		
		
	}
	public void update(float delta){
		states.get(currentState).update(delta);
	}
	public void dispose(){
		states.get(currentState).dispose();
	}
	
	public void changeState(int state){
		if(!intro){
		outro = true;
		nextState = state;
		}
	}

	public void resize(int width, int height) {
		states.get(currentState).resize(width, height);
	}

	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) {
		return states.get(currentState).accelerometerMoved(arg0, arg1, arg2);
	}

	public boolean axisMoved(Controller arg0, int arg1, float arg2) {
		return states.get(currentState).axisMoved(arg0, arg1, arg2);
	}

	public boolean buttonDown(Controller arg0, int arg1) {
		return states.get(currentState).buttonDown(arg0, arg1);
	}

	public boolean buttonUp(Controller arg0, int arg1) {
		return states.get(currentState).buttonUp(arg0, arg1);
	}

	public void connected(Controller arg0) {
		states.get(currentState).connected(arg0);
	}

	public void disconnected(Controller arg0) {
		states.get(currentState).disconnected(arg0);
	}

	public boolean povMoved(Controller arg0, int arg1, PovDirection arg2) {
		return states.get(currentState).povMoved(arg0, arg1, arg2);
	}

	public boolean xSliderMoved(Controller arg0, int arg1, boolean arg2) {
		return states.get(currentState).xSliderMoved(arg0, arg1, arg2);
	}

	public boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) {
		return states.get(currentState).ySliderMoved(arg0, arg1, arg2);
	}

	public State getState() {
		return states.get(currentState);
	}


}
