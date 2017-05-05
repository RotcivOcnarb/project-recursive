package com.digitalmatrix.pack.recursive.states;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.digitalmatrix.pack.recursive.objects.InputConfig;

public class SelectLevel extends State{

	ArrayList<String> levels = new ArrayList<String>();
	int selection = 0;
	
	BitmapFont font;
	Matrix4 normalProjection;

	public SelectLevel(Manager manager) {
		super(manager);
	}

	@SuppressWarnings("deprecation")
	public void create() {

		levels.clear();
		for(FileHandle fh : Gdx.files.internal("./bin/maps/").list()){
			if(!fh.isDirectory() && fh.name().endsWith(".tmx"))
			levels.add(fh.name().split(".tmx")[0]);
		}
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(38);
		font.setColor(Color.RED);
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		selection = 0;
		idt.set(new float[]{
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0 , 1});
	}

	public void render(SpriteBatch sb) {
		sb.begin();
		sb.setProjectionMatrix(normalProjection);
		offset = 0;
		font.setColor(Color.RED);
		drawCenter(sb, "Level select", Gdx.graphics.getHeight() - 50);
		
		for(int i = 0; i < levels.size(); i ++){
			font.setColor(selection == i ? Color.BLUE : Color.RED);
			drawCenter(sb, levels.get(i), Gdx.graphics.getHeight()  - 150 - 50*i);
		}

		sb.end();
	}
	float offset = 0;
	float angle = 0;
	Matrix4 mFont = new Matrix4();
	Matrix4 idt = new Matrix4();
	public void drawCenter(SpriteBatch sb, String s, float height){
		offset += 0.3f;
		mFont.setToTranslation(Gdx.graphics.getWidth()/2 , height, 0);
		mFont.rotate(0, 0, 1, (float)(Math.sin((angle+offset)*1.5f)*3f));
		sb.setTransformMatrix(mFont);
			font.draw(sb, s, - font.getSpaceWidth()*s.length()/2, 0);
		sb.setTransformMatrix(idt);
	}

	public void update(float delta) {
		angle += delta;
		if(Gdx.input.isKeyJustPressed(InputConfig.UP)){
			selection --;
			if(selection == -1) selection = 0;
		}
		if(Gdx.input.isKeyJustPressed(InputConfig.DOWN)){
			selection ++;
			if(selection == levels.size()) selection = levels.size() - 1;
		}
		
		if(Gdx.input.isKeyJustPressed(InputConfig.ENTER)){
			GameState.setLevel(levels.get(selection));
			manager.changeState(Manager.GAMESTATE);
		}
	}

	public void dispose() {
		
	}

	public void resize(int width, int height) {
		
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
					if(selection == levels.size()) selection = levels.size() - 1;;
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
	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if(buttonCode == InputConfig.J_ENTER){
			GameState.setLevel(levels.get(selection));
			manager.changeState(Manager.GAMESTATE);
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
