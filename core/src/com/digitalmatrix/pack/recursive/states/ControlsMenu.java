package com.digitalmatrix.pack.recursive.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ControlsMenu extends State{
	
	BitmapFont font;
	Matrix4 normalProjection;

	public ControlsMenu(Manager manager) {
		super(manager);
	}

	@SuppressWarnings("deprecation")
	public void create() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(36);
		font.setColor(Color.RED);
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		idt.set(new float[]{
				1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0 , 1});
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
			
			offset = 0;
			drawCenter(sb, "Keyboard controls",Gdx.graphics.getHeight() - 50);
			drawCenter(sb, "Arrows -> Move character", Gdx.graphics.getHeight() - 200);
			drawCenter(sb, "Spacebar -> Jump", Gdx.graphics.getHeight() - 250);
			drawCenter(sb, "Z -> Action", Gdx.graphics.getHeight() - 300);
			drawCenter(sb, "X -> Throw items", Gdx.graphics.getHeight() - 350);
			drawCenter(sb, "C -> Release item", Gdx.graphics.getHeight() - 400);
			drawCenter(sb, "Press any key to go back to menu", Gdx.graphics.getHeight() - 550);
			
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
		if(Gdx.input.isKeyJustPressed(Keys.ANY_KEY)){
			manager.changeState(3);
		}
		angle += delta;
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
