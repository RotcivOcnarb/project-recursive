package com.digitalmatrix.pack.recursive.objects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.digitalmatrix.pack.recursive.recstates.MenuState;
import com.digitalmatrix.pack.recursive.recstates.RecState;
import com.digitalmatrix.pack.recursive.recstates.RecursiveGameState;
import com.digitalmatrix.pack.recursive.states.GameState;
import com.digitalmatrix.pack.recursive.states.Manager;

public class Recursive{
	
	public static final float SCALE_DIV = 45;
	public static final float SCALE_MULT = 0.02f;
	public static final float TILE_SCALE = 3.125f;
	
	public static Array<Body> forRemoval = new Array<Body>();
	
	public Recursive child;
	public Recursive parent;
	public OrthographicCamera camera;
	public GameState game;
	public FrameBuffer buffer;
	public Texture lastScreen;
	SpriteBatch sb;
	public ArrayList<RecState> states;
	int currentState = 1;
	Matrix4 normalProjection;
	public String level;
	
	float opacity = 1;
	ShapeRenderer srenderer;
	boolean intro = true;
	boolean outro = false;
	int nextState = currentState;

	
	public Recursive(Recursive parent, GameState game, SpriteBatch sb, String level, boolean isFirst){

		this.sb = sb;
		this.parent = parent;
		this.game = game;
		this.level = level;		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = 1f/Recursive.SCALE_DIV;

		states = new ArrayList<RecState>();
		states.add(new MenuState(this));
		states.add(new RecursiveGameState(this, sb, level, isFirst));
				
		if(isFirst)
		((RecursiveGameState)states.get(currentState)).firstCreate();
		
		srenderer = new ShapeRenderer();

		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());


	}
	
	public Texture getTexture(String file){
		return game.manager.assets.getTexture(file);
	}
	
	public int currentLevel(){
		return ((RecursiveGameState)states.get(1)).getCurrentLevel();
	}
	
	public void create() {
		states.get(currentState).create();
		
	}
	
	public void resetLevel(boolean first){
		((RecursiveGameState)states.get(1)).resetLevel(first);
	}
	
	public void teleport(String map){
		GameState.setLevel(map);
		game.manager.changeState(Manager.GAMESTATE);
//		level = map;
//		GameState.setLevel(map);
//		((RecursiveGameState)states.get(1)).canReload(map);
//		((RecursiveGameState)states.get(1)).firstCreate();
//		child = null;
	}
	
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		camera.zoom = 1f/Recursive.SCALE_DIV;

	}

	
	Color tempC = Color.WHITE;
	public void render(SpriteBatch sb){

		
		
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
				if(nextState == 0) createScreen();
				currentState = nextState;
				create();
			}
		}
		
		
		sb.setProjectionMatrix(camera.combined);
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
	public void update(float delta){
		states.get(currentState).update(delta);
		camera.update();
		for(Body b : forRemoval){
			if(b.getUserData() instanceof Enemy){
				((RecursiveGameState)states.get(1)).enemies.remove(b.getUserData());
			}			
			b.getWorld().destroyBody(b);
		}
		forRemoval.clear();
	}
	
	public void dispose(){
		states.get(currentState).dispose();
	}
	
	public void changeState(int state){
		if(!intro){
			outro = true;
			opacity = 0;
			nextState = state;
			}
	}
	
	public void createScreen(){
		if(parent == null){
			lastScreen = Util.copyTexture(GameState.firstBuffer.getColorBufferTexture());
		}
		else{
			lastScreen = Util.copyTexture(parent.buffer.getColorBufferTexture());
		}
	}
	
	public void forward(){
		createScreen();
		game.forward();
	}
	public boolean goingBack = false;
	public void backward(){
		goingBack = true;
		
	}
	
	public void resetGame(boolean first){

		states.set(1, new RecursiveGameState(this, sb, level, first));
		child = null;
	}
	
	public boolean hasSave(){
		return ((RecursiveGameState) states.get(1)).hasBeenCreated;
	}
	public OrthographicCamera getCamera() {
		return camera;
	}

	public void connected(Controller controller) {
		
	}

	public void disconnected(Controller controller) {
		
	}

	public boolean buttonDown(Controller controller, int buttonCode) {
		return states.get(currentState).buttonDown(controller, buttonCode);
	}

	public boolean buttonUp(Controller controller, int buttonCode) {
		return states.get(currentState).buttonUp(controller, buttonCode);
	}

	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return states.get(currentState).axisMoved(controller, axisCode, value);
	}

	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return states.get(currentState).povMoved(controller, povCode, value);
	}

	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return states.get(currentState).xSliderMoved(controller, sliderCode, value);
	}

	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return states.get(currentState).ySliderMoved(controller, sliderCode, value);
	}

	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return states.get(currentState).accelerometerMoved(controller, accelerometerCode, value);
	}

	public RecState getState() {
		return states.get(currentState);
	}

	public TextureAtlas getTextureAtlas(String particleTexture) {
		return game.manager.assets.getTextureAtlas(particleTexture);
	}


	

}
