package com.digitalmatrix.pack.recursive.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.digitalmatrix.pack.recursive.objects.InputConfig;

public class InputConfigState extends State{

	BitmapFont font;
	Matrix4 normalProjection;
	
	int btn = 0;
	boolean next = false;
	public InputConfigState(Manager manager) {
		super(manager);
	}

	@SuppressWarnings("deprecation")
	public void create() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(36);
		font.setColor(Color.RED);
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		btn = 0;
		next = false;
	}

	public float center(String s){
		return (s.length()/2 * font.getSpaceWidth());
	}
	
	public void render(SpriteBatch sb) {
		sb.begin();
			switch(btn){
			case 0:
				font.draw(sb, "Move Left on controller analogic", Gdx.graphics.getWidth()/2 - center("Move Left on controller analogic"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Axis " + InputConfig.J_AXIS_X + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Axis " + InputConfig.J_AXIS_X + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			case 1:
				font.draw(sb, "Move Up on controller analogic", Gdx.graphics.getWidth()/2 - center("Move Up on controller analogic"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Axis " + InputConfig.J_AXIS_Y + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Axis " + InputConfig.J_AXIS_Y + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			case 2:
				font.draw(sb, "Press Jump button", Gdx.graphics.getWidth()/2 - center("Press Jump button"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Button " + InputConfig.J_JUMP + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Button " + InputConfig.J_JUMP + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			case 3:
				font.draw(sb, "Press Select button", Gdx.graphics.getWidth()/2 - center("Press Select button"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Button " + InputConfig.J_ENTER + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Button " + InputConfig.J_ENTER + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			case 4:
				font.draw(sb, "Press Action button", Gdx.graphics.getWidth()/2 - center("Press Action button"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Button " + InputConfig.J_ACTION + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Button " + InputConfig.J_ACTION + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			case 5:
				font.draw(sb, "Press Throw button", Gdx.graphics.getWidth()/2 - center("Press Throw button"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Button " + InputConfig.J_THROW + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Button " + InputConfig.J_THROW + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			case 6:
				font.draw(sb, "Press Start button", Gdx.graphics.getWidth()/2 - center("Press Start button"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Button " + InputConfig.J_ESC + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Button " + InputConfig.J_ESC + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			case 7:
				font.draw(sb, "Press Release button", Gdx.graphics.getWidth()/2 - center("Press Release button"), Gdx.graphics.getHeight() - 200);
				if(next){
					font.draw(sb, "Button " + InputConfig.J_RELEASE + " selected, press enter on keyboard",
							Gdx.graphics.getWidth()/2 - center("Button " + InputConfig.J_RELEASE + " selected, press enter on keyboard"),
							Gdx.graphics.getHeight() - 300);
				}
				break;
			}
		sb.end();
		
		if(Gdx.input.isKeyJustPressed(InputConfig.ENTER)){
			if(next){
				next = false;
				btn++;
				if(btn == 8){
					manager.changeState(Manager.MAINMENU);
				}
			}
		}
		if(Gdx.input.isKeyJustPressed(InputConfig.ESC)){
			manager.changeState(Manager.MAINMENU);
		}
	}

	public void update(float delta) {
		
	}

	public void dispose() {
		
	}

	public void resize(int width, int height) {
		
	}

	public boolean accelerometerMoved(Controller arg0, int arg1, Vector3 arg2) {
		return false;
	}

	public boolean axisMoved(Controller c, int axis, float amount) {
		if(Math.abs(amount) > 0.5f){
			switch(btn){
			case 0:
				InputConfig.J_AXIS_X = axis;
				next = true;
				break;
			case 1:
				InputConfig.J_AXIS_Y = axis;
				next = true;
				break;
			}
		}
		return false;
	}

	public boolean buttonDown(Controller c, int code) {
		switch(btn){
		case 2:
			InputConfig.J_JUMP = code;
			next = true;
			break;
		case 3:
			InputConfig.J_ENTER = code;
			next = true;
			break;
		case 4:
			InputConfig.J_ACTION = code;
			next = true;
			break;
		case 5:
			InputConfig.J_THROW = code;
			next = true;
			break;
		case 6:
			InputConfig.J_ESC = code;
			next = true;
			break;
		case 7:
			InputConfig.J_RELEASE = code;
			next = true;
			break;
		}
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
