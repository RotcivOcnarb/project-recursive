package com.digitalmatrix.pack.recursive.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.digitalmatrix.pack.recursive.objects.InputConfig;

public class MainMenu extends State{
	
	Texture background;
	Texture bigBall;
	Texture wave1, wave2, wave3;
	Texture logo;
	float bgAR;
	float fadeInAnimation = 0;
	BitmapFont font;
	SpriteBatch sb;
	Matrix4 normalProjection;
	
	int selection = 0;
	
	String options[] = {"Play game", "Input configuration", "Controls", "Exit"};
	
	SpriteBatch blendBatch;
	
	@SuppressWarnings("deprecation")
	public MainMenu(Manager manager) {
		super(manager);
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/moonhouse.ttf"));
		font = generator.generateFont(72);
		font.setColor(new Color(1, 1, 1, 0.5f));
		
		background = new Texture("mainmenu/bg.png");
		bgAR = background.getWidth()/(float)background.getHeight();
		bigBall = new Texture("mainmenu/ball.png");
		
		wave1 = new Texture("mainmenu/wave1.png");
		wave2 = new Texture("mainmenu/wave2.png");
		wave3 = new Texture("mainmenu/wave3.png");
		logo = new Texture("mainmenu/logo.png");
		
		blendBatch = new SpriteBatch();
		
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
	}

	public void create() {
	}
	
	float timer = 0;
	Color tempC = Color.WHITE;
	public void render(SpriteBatch sb) {
		this.sb = sb;
		blendBatch.setProjectionMatrix(normalProjection);
		blendBatch.begin();
		tempC.set(fadeInAnimation, fadeInAnimation, fadeInAnimation, 1);
		blendBatch.setColor(tempC);
		blendBatch.draw(background, 0, 0, Gdx.graphics.getHeight() * bgAR, Gdx.graphics.getHeight());
		blendBatch.enableBlending();
		int src = blendBatch.getBlendSrcFunc();
		int dst = blendBatch.getBlendDstFunc();
		blendBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_COLOR);
		
		blendBatch.draw(
				bigBall,
				0, 0,
				(Gdx.graphics.getHeight() * bgAR)/2, Gdx.graphics.getHeight()/2,
				Gdx.graphics.getHeight() * bgAR, Gdx.graphics.getHeight(),
				(float)Math.sin(timer)*0.1f + 1.5f, (float)Math.sin(timer)*0.1f + 1.5f,
				0, //ROTATION
				0, 0,
				(int)(Gdx.graphics.getHeight() * bgAR), Gdx.graphics.getHeight(),
				false, false);
		blendBatch.draw(
				wave1,
				0, 0,
				(Gdx.graphics.getHeight() * bgAR)/2, Gdx.graphics.getHeight()/2,
				Gdx.graphics.getHeight() * bgAR, Gdx.graphics.getHeight(),
				1, 1,
				timer*3, //ROTATION
				0, 0,
				(int)(Gdx.graphics.getHeight() * bgAR), Gdx.graphics.getHeight(),
				false, false);
		
		blendBatch.draw(
				wave2,
				0, 0,
				(Gdx.graphics.getHeight() * bgAR)/2, Gdx.graphics.getHeight()/2,
				Gdx.graphics.getHeight() * bgAR, Gdx.graphics.getHeight(),
				1, 1,
				-timer*2, //ROTATION
				0, 0,
				(int)(Gdx.graphics.getHeight() * bgAR), Gdx.graphics.getHeight(),
				false, false);
		
		blendBatch.draw(
				wave3,
				0, 0,
				(Gdx.graphics.getHeight() * bgAR)/2, Gdx.graphics.getHeight()/2,
				Gdx.graphics.getHeight() * bgAR, Gdx.graphics.getHeight(),
				1, 1,
				timer*2, //ROTATION
				0, 0,
				(int)(Gdx.graphics.getHeight() * bgAR), Gdx.graphics.getHeight(),
				false, false);

		blendBatch.draw(logo, 50, Gdx.graphics.getHeight() - logo.getHeight() - 50);
		
		blendBatch.setBlendFunction(src, dst);
		
		blendBatch.disableBlending();
		
		tempC.set(1, 1, 1, 1);
		blendBatch.setColor(tempC);
		
		blendBatch.end();
		
		sb.begin();
		sb.setProjectionMatrix(normalProjection);
		int i = 0;
		for(String s : options){
			tempC.set(1, 1, 1, .5f);
			font.setColor(tempC);
			if(i == selection){
				tempC.set(1, 0, 1, .5f);
				font.setColor(tempC);
			}
			font.draw(sb, s, Gdx.graphics.getWidth()/2 - (font.getSpaceWidth() * s.length())/2, Gdx.graphics.getHeight()/2 - i*60);
			i++;
		}
		
		sb.end();
		
		if(Gdx.input.isKeyJustPressed(InputConfig.DOWN)){
			selection ++;
			if(selection == options.length) selection = options.length - 1;
		}
		if(Gdx.input.isKeyJustPressed(InputConfig.UP)){
			selection --;
			if(selection == -1) selection = 0;
		}
		if(Gdx.input.isKeyJustPressed(InputConfig.ENTER)){
			switch(selection){
			case 0:
				manager.changeState(Manager.SELECTLEVEL);
				break;
			case 1:
				manager.changeState(Manager.INPUTCONFIGSTATE);
				break;
			case 2:
				manager.changeState(Manager.CONTROLSMENU);
				break;
			case 3:
				System.exit(0);
				break;
			}
		}
		
		
	}

	public void update(float delta) {
		fadeInAnimation += delta;
		
		if(fadeInAnimation > 255){
			fadeInAnimation = 255;
		}

		timer += delta;
	}

	public void dispose() {
		
	}

	public void resize(int width, int height) {
		if(sb != null){
			normalProjection = new Matrix4().setToOrtho2D(0, 0, width, height);
			sb.setProjectionMatrix(normalProjection);
		}
	}

	@Override
	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean axisXMoved = false;
	boolean axisYMoved = false;
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		if(axisCode == InputConfig.J_AXIS_X){
			if(Math.abs(value) > 0.5f){
				if(!axisXMoved){
				axisXMoved = true;
				//axisX JUST MOVED
				
				
				}
			}
			else{
				axisXMoved = false;
			}
		}
		if(axisCode == InputConfig.J_AXIS_Y){
			if(Math.abs(value) > 0.5f){
				if(!axisYMoved){
				axisYMoved = true;
				//axis y JUST MOVED
				if(value < 0){
					selection --;
					if(selection == -1) selection = 0;
				}
				else{
					selection ++;
					if(selection == options.length) selection = options.length - 1;
				}
				}
			}
			else{
				axisYMoved = false;
			}
		}
		return false;
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if(buttonCode == InputConfig.J_ENTER){
			switch(selection){
			case 0:
				manager.changeState(Manager.SELECTLEVEL);
				break;
			case 1:
				manager.changeState(Manager.INPUTCONFIGSTATE);
				break;
			case 2:
				manager.changeState(Manager.CONTROLSMENU);
				break;
			case 3:
				System.exit(0);
				break;
			}
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connected(Controller arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean povMoved(Controller arg0, int arg1, PovDirection arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return false;
	}

}
