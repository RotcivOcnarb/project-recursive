package com.digitalmatrix.pack.recursive.states;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class State{
	
	public Manager manager;
	
	public State(Manager manager){
		this.manager = manager;
	}
	
	protected void changeState(int state){
		manager.changeState(state);
	}
	
	public abstract void create();
	public abstract void render(SpriteBatch sb);
	public abstract void update(float delta);
	public abstract void dispose();
	public abstract void resize(int width, int height);
	

	public abstract boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2);
	public abstract boolean axisMoved(Controller arg0, int arg1, float arg2);
	public abstract boolean buttonDown(Controller arg0, int arg1);
	public abstract boolean buttonUp(Controller arg0, int arg1);
	public abstract void connected(Controller arg0);
	public abstract void disconnected(Controller arg0);
	public abstract boolean povMoved(Controller arg0, int arg1, PovDirection arg2);
	public abstract boolean xSliderMoved(Controller arg0, int arg1, boolean arg2);
	public abstract boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) ;

}
