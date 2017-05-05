package com.digitalmatrix.pack.recursive.recstates;

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
import com.digitalmatrix.pack.recursive.objects.Recursive;
import com.digitalmatrix.pack.recursive.states.Manager;

public class MenuState extends RecState{
	
	BitmapFont font;
	Matrix4 normalProjection;
	
	int selection = 0;
	int numItems = 3;

	public MenuState(Recursive rec) {
		super(rec);
	}

	@SuppressWarnings("deprecation")
	public void create() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(38);
		font.setColor(Color.RED);
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		selection = 0;
	}

	public void update(float delta) {

		
		if(Gdx.input.isKeyJustPressed(InputConfig.ENTER)){
			switch(selection){
			case 0:
				if(rec.hasSave())
				rec.changeState(1);
				break;
			case 1:
				if(rec.hasSave()){
					rec.resetLevel(rec == rec.game.first);
					rec.changeState(1);
				}
				break;
			case 2:
				if(rec.parent != null)
				rec.backward();
				break;
			case 3:
				rec.game.manager.changeState(Manager.SELECTLEVEL);
				break;
				
			}
		}
		
		if(Gdx.input.isKeyJustPressed(InputConfig.UP)){
			selection --;
			if(selection == -1) selection = 0;
		}
		if(Gdx.input.isKeyJustPressed(InputConfig.DOWN)){
			selection ++;
			if(selection == numItems) selection = numItems - 1;
		}
	}

	public void render(SpriteBatch sb) {
		sb.begin();
		sb.setProjectionMatrix(normalProjection);
		font.setColor(selection == 0 ? (rec.hasSave() ? Color.BLUE : Color.GRAY) : Color.RED);
		font.draw(sb, "Resume layer", Gdx.graphics.getWidth()/2 - 100, Gdx.graphics.getHeight()/2 + 100);
		font.setColor(selection == 1 ? (rec.hasSave() ? Color.BLUE : Color.GRAY) : Color.RED);
		font.draw(sb, "Reset Level", Gdx.graphics.getWidth()/2 - 100, Gdx.graphics.getHeight()/2 + 50);
		font.setColor(selection == 2 ? (rec.parent == null ? Color.GRAY : Color.BLUE) : Color.RED);
		font.draw(sb, "Previous layer", Gdx.graphics.getWidth()/2 - 100 , Gdx.graphics.getHeight()/2 );
		if(rec == rec.game.first){
			numItems = 4;
			font.setColor(selection == 3 ? Color.BLUE : Color.RED);
			font.draw(sb, "Select level", Gdx.graphics.getWidth()/2 - 100 , Gdx.graphics.getHeight()/2 - 50);
		}
		sb.end();
	}

	public void dispose() {
		//font.dispose();
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if(buttonCode == InputConfig.J_ENTER){
			switch(selection){
			case 0:
				if(rec.hasSave())
				rec.changeState(1);
				break;
			case 1:
				if(rec.hasSave()){
					rec.resetLevel(rec == rec.game.first);
					rec.changeState(1);
				}
				break;
			case 2:
				if(rec.parent != null)
				rec.backward();
				break;
			case 3:
				rec.game.manager.changeState(Manager.SELECTLEVEL);
				break;
			}
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
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
					if(selection == numItems) selection = numItems - 1;
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
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}

}
