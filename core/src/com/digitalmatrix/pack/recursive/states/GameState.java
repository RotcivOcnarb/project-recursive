package com.digitalmatrix.pack.recursive.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.digitalmatrix.pack.recursive.objects.FrameBufferManager;
import com.digitalmatrix.pack.recursive.objects.GUI;
import com.digitalmatrix.pack.recursive.objects.Recursive;

public class GameState extends State{
	
	public Recursive current;
	
	public Recursive first;
	
	BitmapFont font;
	Matrix4 normalProjection;
	public static FrameBuffer firstBuffer;
	SpriteBatch sb;
	static String level = "level1";
	
	public static void setLevel(String level){
		GameState.level = level;
	}
	
	public GameState(Manager manager, SpriteBatch sb) {
		super(manager);
		this.sb = sb;
	}

	@SuppressWarnings("deprecation")
	public void create() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(36);
		font.setColor(Color.RED);
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		
		current = new Recursive(null, this, sb, level, true);
		first = current;
		
		firstBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		GUI.create(manager);
	}

	public void render(SpriteBatch sb) {
	
		FrameBufferManager.begin(firstBuffer);
		{
			//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			first.render(sb);
		}
		FrameBufferManager.end();
		
		sb.begin();
		{
			Matrix4 last = sb.getProjectionMatrix();
			sb.setProjectionMatrix(normalProjection);
			
			//sb.draw(firstBuffer.getColorBufferTexture(), 0, firstBuffer.getHeight(), firstBuffer.getWidth(), -firstBuffer.getHeight());
			sb.draw(firstBuffer.getColorBufferTexture(), 0, firstBuffer.getHeight(), firstBuffer.getWidth(), -firstBuffer.getHeight());
	
			
//			Recursive par = first;
//			int num = 0;
//			while(par != null){
//				if(par.equals(current)) font.setColor(Color.BLUE);
//				else font.setColor(Color.RED);
//				font.draw(sb, "Recursive", 30 + num*50, Gdx.graphics.getHeight() - 50 - num*40);
//				par = par.child;
//				num++;
//			}
			
			
			sb.setProjectionMatrix(last);
		}
		sb.end();
		
		GUI.drawUI(sb);
	}

	public void update(float delta) {
		current.update(delta);
	}

	public void dispose() {
		current.dispose();
		font.dispose();
	}
	
	public int countTotal(){
		if(current == null) return 0;
		return countBackward() + 1 + countForward();
	}
	
	public int countForward(){
		int num = 0;
		Recursive curr = current;
		if(curr != null){
			while(curr.child != null){
				num++;
				curr = curr.child;
			}
		}
		return num;
	}
	
	public int lastLayer(){
		int c = 0;
		Recursive r = first;
		while(r.child != null){
			c ++;
			r = r.child;
		}
		
		return c;
	}
	
	public int countBackward(){
		int num = 0;
		Recursive curr = current;
		if(current == null) return -1;
			while(curr.parent != null){
				num++;
				curr = curr.parent;
			}
		
		return num;
	}
	
	public int layer(Recursive r){
		int l = 0;
		Recursive curr = first;
		do{
			if(curr.equals(r)){
				return l;
			}
			else{
				curr = curr.child;
				l++;
			}
		}while(curr.child != null);
		return l;
	}
	

	
	public void forward(){
		
		if(current.child == null){
			current.child = new Recursive(current, this, sb, level, false);
		}
		current = current.child;
		current.create();
	}
	
	public void backward(){
		if(current.parent != null){
			current = current.parent;
			current.create();
		}
	}
	
	public void resize(int width, int height) {
		current.resize(width, height);
	}

	@Override
	public void connected(Controller controller) {
		current.connected(controller);
		
	}

	@Override
	public void disconnected(Controller controller) {
		current.disconnected(controller);
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		return current.buttonDown(controller, buttonCode);
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		return current.buttonUp(controller, buttonCode);
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		// TODO Auto-generated method stub
		return current.axisMoved(controller, axisCode, value);
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		// TODO Auto-generated method stub
		return current.povMoved(controller, povCode, value);
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return current.xSliderMoved(controller, sliderCode, value);
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return current.ySliderMoved(controller, sliderCode, value);
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return current.accelerometerMoved(controller, accelerometerCode, value);
	}



}
