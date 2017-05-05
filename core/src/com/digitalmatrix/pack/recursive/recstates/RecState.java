package com.digitalmatrix.pack.recursive.recstates;

import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.digitalmatrix.pack.recursive.objects.Recursive;

public abstract class RecState implements ControllerListener{
	
	public Recursive rec;
	
	public RecState(Recursive rec){
		this.rec = rec;
	}
	
	public abstract void create();
	public abstract void update(float delta);
	public abstract void render(SpriteBatch sb);
	public abstract void dispose();
	
	public OrthographicCamera getCamera(){
		return rec.getCamera();
	}

}
