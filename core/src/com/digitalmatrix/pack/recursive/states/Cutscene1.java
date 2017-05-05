package com.digitalmatrix.pack.recursive.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Cutscene1 extends State{
	
	Texture comic[] = new Texture[8];
	Sound sounds[] = new Sound[6];

	public Cutscene1(Manager manager) {
		super(manager);
	}
	
	float timer = 0;
	
	int currentTex = 0;
	int currentSound = 0;
	int nextTex = currentTex;

	long id = -1;
	
	float opacity = 0;
	boolean transition = false;
	
	boolean steps[] = new boolean[8];
	boolean played[] = new boolean[6];
	
	OrthographicCamera camera;
	
	float nextX;
	float nextY;
	float nextZoom;

	public void create() {
		for(int i = 0; i < 8; i ++){
			comic[i] = new Texture("cutscene1/comic" + (i+1) + ".png");
		}
		for(int i = 0; i < 6; i ++){
			sounds[i] = Gdx.audio.newSound(Gdx.files.internal("cutscene1/som" + (i+1) + ".wav"));
		}
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(175, comic[0].getHeight() - 180, 0);
		camera.zoom = 0.35f;
	}
	Color tempC = Color.WHITE;
	public void render(SpriteBatch sb) {
		
		
		if(!played[currentSound]){
			id = sounds[currentSound].play(1.0f);
			played[currentSound] = true;
		}
		
		timer += Gdx.graphics.getDeltaTime();
		
		if(timer > 0){
			if(!steps[0]){
				opacity = 0;
				steps[0] = true;
				transition = true;
				nextZoom = 0.35f;
				nextX = 175;
				nextY = comic[0].getHeight() - 180;
			}
		}
		if(timer > 3){
			if(!steps[1]){
				opacity = 0;
				nextTex = 1;
				steps[1] = true;
				transition = true;
				nextX = 593;
			}
		}
		if(timer > 5f){
			currentSound = 1;
		}
		if(timer > 5.5){
			if(!steps[2]){
				opacity = 0;
				nextTex = 2;
				steps[2] = true;
				transition = true;
				nextX = 363;
				nextY = comic[0].getHeight() - 532;
				nextZoom = 0.5f;
			}
		}
		if(timer > 10){
			if(!steps[3]){
				opacity = 0;
				nextTex = 3;
				steps[3] = true;
				transition = true;
				nextY = comic[0].getHeight() - 952;
			}
		}
		if(timer > 12){
			currentSound = 2;
		}
		if(timer > 13){
			if(!steps[4]){
				opacity = 0;
				nextTex = 4;
				steps[4] = true;
				transition = true;
				nextY = comic[0].getHeight() - 1314;
				nextX = 186;
				nextZoom = 0.35f;
			}
		}
		if(timer > 17){
			currentSound = 3;
			if(!steps[5]){
				opacity = 0;
				nextTex = 5;
				steps[5] = true;
				transition = true;
				nextX = 608;
				nextY = comic[0].getHeight() - 1264;
			}
		}
		
		if(timer > 25){
			currentSound = 4;
			if(!steps[6]){
				opacity = 0;
				nextTex = 6;
				steps[6] = true;
				transition = true;
				nextX = 141;
				nextY = comic[0].getHeight() - 1601;
				nextZoom = 0.25f;
			}
		}
		
		if(timer > 31){
			currentSound = 5;
			if(!steps[7]){
				opacity = 0;
				nextTex = 7;
				steps[7] = true;
				transition = true;
				nextX = 560;
				nextY = comic[0].getHeight() - 1550;
				nextZoom = 0.4f;
			}
		}
		
		if(timer > 38){
			manager.changeState(Manager.MAINMENU);
		}
		
		moveX(nextX);
		moveY(nextY);
		changeScale(nextZoom);
		
		sb.setProjectionMatrix(camera.combined);
				
		sb.begin();
			if(transition){
				opacity += Gdx.graphics.getDeltaTime();
				if(opacity > 1){
					transition = false;
					currentTex = nextTex;
					opacity = 0;
				}
			}
			sb.draw(comic[currentTex], 0, 0);
			
			if(transition){
				tempC.set(1, 1, 1, opacity);
				sb.setColor(tempC);
				sb.draw(comic[nextTex], 0, 0);
				tempC.set(1, 1, 1, 1);
				sb.setColor(tempC);
				
			}
		sb.end();
		
		if(skip){
			volume -= Gdx.graphics.getDeltaTime();
			if(volume < 0){
				volume = 0;
				manager.changeState(Manager.MAINMENU);
			}
			sounds[currentSound].setVolume(id, volume);
		}
	}

	float volume = 1;
	boolean skip = false;
	
	public void moveX(float x){
		camera.position.x += (x - camera.position.x)/50.0;
	}
	
	public void moveY(float y){
		camera.position.y += (y - camera.position.y)/50.0;
	}
	
	public void changeScale(float scale){
		camera.zoom += (scale - camera.zoom)/50.0;
	}
	
	public void update(float delta) {
		if(Gdx.input.isKeyPressed(Keys.ANY_KEY)){
			skip = true;
		}
		camera.update();
	}

	public void dispose() {
		
	}

	public void resize(int width, int height) {
		
	}

	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) {
		return false;
	}

	public boolean axisMoved(Controller arg0, int arg1, float arg2) {
		return false;
	}

	public boolean buttonDown(Controller arg0, int arg1) {
		skip = true;
		
		return false;
	}

	public boolean buttonUp(Controller arg0, int arg1) {
		return false;
	}

	public void connected(Controller arg0) {
		
	}

	public void disconnected(Controller arg0) {
		
	}

	public boolean povMoved(Controller arg0, int arg1, PovDirection arg2) {
		return false;
	}

	public boolean xSliderMoved(Controller arg0, int arg1, boolean arg2) {
		return false;
	}

	public boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) {
		return false;
	}

}
