package com.digitalmatrix.pack.recursive.objects;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.digitalmatrix.pack.recursive.recstates.RecursiveGameState;

public class Player{

	Body player;
	Recursive rec;
	boolean canJump = true;
	public int numContacts = 0;
	float MAX_SPEED = 6;
	boolean doubleJump = false;
	
	//ParticleEffect pe;
	
	//TODO: conseguir sprite do player
	Texture playerTex;
	
	boolean left;
	boolean right;
		
	public static float life = 100;
	public static Body boxOver = null;
	
	float hitTimer = 0;
	
	public String signMessage = "";
	
	ArrayList<Fixture> colliding;
	private Matrix4 normalProjection;
	public static String holding = null;
	public static int layerHoldOrigin = -1;
	public static ArrayList<Integer> keys = new ArrayList<Integer>();
	RecursiveGameState state;
	OrthographicCamera guiCam;
	BitmapFont font;
	
	float ratew;
	float rateh;
	
	@SuppressWarnings("deprecation")
	public Player(Body player, Recursive rec, RecursiveGameState state){
		this.player = player;
		this.rec = rec;
		this.state = state;
				
//		pe = new ParticleEffect();
//		pe.load(Gdx.files.internal("particles/rock.party"), Gdx.files.internal("particles"));
//		pe.scaleEffect(1f/Recursive.SCALE_DIV);
		
		playerTex = rec.getTexture(Assets.BOMB);
				
		colliding = new ArrayList<Fixture>();
		
		CircleShape circ = new CircleShape();
		circ.setRadius(15/Recursive.SCALE_DIV);
		player.createFixture(circ, 1f);
		PolygonShape feet = new PolygonShape();
		feet.setAsBox(10/Recursive.SCALE_DIV, 10/Recursive.SCALE_DIV, new Vector2(0, -15/Recursive.SCALE_DIV), 0);
		Fixture f = player.createFixture(feet, 0);
		f.setSensor(true);
		f.setUserData("PLAYER_FEET");
		player.setUserData("Player");
		
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());

		guiCam = new OrthographicCamera(Gdx.graphics.getWidth()*50f, Gdx.graphics.getHeight()*50f);
		guiCam.zoom = 1f/Recursive.SCALE_DIV;
		ratew = guiCam.viewportWidth/rec.getCamera().viewportWidth;  //<--- you should calculate these 2 only once.
		rateh = guiCam.viewportHeight/rec.getCamera().viewportHeight;
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(20);
		font.setColor(Color.MAROON);
				
	}
	float signOpacity = 0;
	Color tempC = Color.WHITE;
	
	public String wrapText(String text, float width){
		String[] words = text.split("\\s+");
		String finalWord = "";
		
		float w = 0;
		for(String s : words){
			w += font.getSpaceWidth()*s.length();
			if(w > width){
				finalWord += "\n" + s + " ";
				w = 0;
			}
			else{
				finalWord += s + " ";
			}
		}
		finalWord = finalWord.substring(0, finalWord.length() - 1);
		
		return finalWord;
	}
	
	public void render(SpriteBatch sb){
		sb.begin();
		//pe.draw(sb);
		sb.setProjectionMatrix(rec.getCamera().combined);
		sb.draw(playerTex, player.getPosition().x - playerTex.getWidth() * Recursive.SCALE_MULT/2, player.getPosition().y - playerTex.getHeight() * Recursive.SCALE_MULT/2, playerTex.getWidth() * Recursive.SCALE_MULT, playerTex.getHeight() * Recursive.SCALE_MULT);
		
		Matrix4 last = sb.getProjectionMatrix();
		sb.setProjectionMatrix(normalProjection);
		
		if(hitTimer > 0){
			sb.draw(rec.getTexture(Assets.HURT), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		
		if(signMessage != ""){
			signOpacity += Gdx.graphics.getDeltaTime() * 2;
			if(signOpacity > 1) signOpacity = 1;
		}
		else{
			signOpacity -= Gdx.graphics.getDeltaTime() * 2;
			if(signOpacity < 0) signOpacity = 0;
		}
		tempC.set(1, 1, 1, signOpacity);
		sb.setColor(tempC);
		
		//draw texture sign
		sb.setProjectionMatrix(rec.getCamera().combined);
		sb.draw(rec.getTexture(Assets.SIGN_FONT),
				player.getPosition().x - 32 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
				player.getPosition().y,
				64 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
				64 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
		
		//draw sign message
		sb.setProjectionMatrix(guiCam.combined);
		float x = guiCam.position.x-(rec.getCamera().position.x - (player.getPosition().x))*ratew;
		float y = guiCam.position.y-(rec.getCamera().position.y - (player.getPosition().y + 64 * Recursive.SCALE_MULT * Recursive.TILE_SCALE - 0.9f))*rateh;
		font.draw(sb, wrapText(signMessage, 80), x, y, 1, 1, true);
		sb.setProjectionMatrix(rec.getCamera().combined);
		
		tempC.set(1, 1, 1, 1);
		sb.setColor(tempC);
		
		sb.setProjectionMatrix(last);
		
		sb.end();
	}
	int side = 1;
	public int side(){
		return side;
	}
	Vector2 temp = Vector2.Zero.cpy();
	public void input(){
		
		//if(player.getLinearVelocity().x > 0) player.setLinearVelocity(0, player.getLinearVelocity().y);
		//player.applyForceToCenter(new Vector2(20f, 0), true);
		
//		if(Gdx.input.isKeyJustPressed(InputConfig.ACTION)){
//			if(colliding.size() > 0 && colliding.get(0).getUserData().toString().startsWith("TELEPORTER")){
//				String map = colliding.get(0).getUserData().toString().split("=")[1].toLowerCase();
//				rec.reload(map);
//			}
//		}

		if(Gdx.input.isKeyPressed(InputConfig.LEFT) || left){
			side = -1;
			if(player.getLinearVelocity().x > 0) player.setLinearVelocity(0, player.getLinearVelocity().y);
			temp.set(-20, 0);
			player.applyForceToCenter(temp, true);
		}
		else if(Gdx.input.isKeyPressed(InputConfig.RIGHT) || right){
			side = 1;
			if(player.getLinearVelocity().x < 0) player.setLinearVelocity(0, player.getLinearVelocity().y);
			temp.set(20, 0);
			player.applyForceToCenter(temp, true);
		}
		else if(!(Gdx.input.isKeyPressed(InputConfig.LEFT) || Gdx.input.isKeyPressed(InputConfig.RIGHT)) && !(left || right)){
			temp.set(-player.getLinearVelocity().x, 0);
			player.applyForceToCenter(temp, true);
		}
		
	
		if(Gdx.input.isKeyJustPressed(InputConfig.JUMP)){
			if(numContacts > 0){
				player.setLinearVelocity(player.getLinearVelocity().x, 0);
				temp.set(0, 4);
				player.applyLinearImpulse(temp, player.getWorldCenter(), true);
			}
			else{
				if(doubleJump){
					player.setLinearVelocity(player.getLinearVelocity().x, 0);
					temp.set(0, 4);
					player.applyLinearImpulse(temp, player.getWorldCenter(), true);
					doubleJump = false;
				}
			}
		}
		
		if(Gdx.input.isKeyJustPressed(InputConfig.ACTION)){
			if(colliding.size() > 0 && colliding.get(0).getUserData().toString().split("=")[1].equals("Computer")){
				rec.forward();
			}
			if(colliding.size() > 0 && colliding.get(0).getUserData().toString().split("=")[1].equals("Teleporter")){
				rec.teleport(colliding.get(0).getUserData().toString().split("=")[2]);
			}
			if(colliding.size() > 0 && colliding.get(0).getUserData().toString().split("=")[1].equals("Switch")){
				String id = colliding.get(0).getUserData().toString().split("=")[2];
				boolean on = Boolean.valueOf(colliding.get(0).getUserData().toString().split("=")[3]);
				colliding.get(0).getBody().setUserData("ITM=Switch=" + id + "=" + !on);
				colliding.get(0).setUserData("ITM=Switch=" + id + "=" + !on);
				for(int i : state.getSpittersByID(Integer.parseInt(id))){
					RecursiveGameState.SPstates.set(i, !RecursiveGameState.SPstates.get(i));
				}

			}
			
			if(colliding.size() > 0 && holding == null){
				
				if(colliding.get(0).getUserData() != null && colliding.get(0).getUserData().toString().split("=")[1].equals("Bomb")){
					holding = "BOMB";
					layerHoldOrigin = rec.game.countBackward();
					
					Recursive.forRemoval.add(colliding.get(0).getBody());
					colliding.remove(0);	
				}
				else if(colliding.get(0).getUserData() != null && colliding.get(0).getUserData().toString().split("=")[1].equals("Key")){
					int keyID = Integer.parseInt(colliding.get(0).getUserData().toString().split("=")[2]);
					if(!keys.contains(keyID))
					keys.add(keyID);
					
					Recursive.forRemoval.add(colliding.get(0).getBody());
					colliding.remove(0);	
				}
				
			}
			else if(holding == null){
				if(boxOver != null){
					holding = "BOX";
					layerHoldOrigin = rec.game.countBackward();
					Recursive.forRemoval.add(boxOver);
					boxOver = null;
				}
			}
			
		}

	}
	
	public void update(float delta){
		hitTimer -= delta;
		
		if(player.getLinearVelocity().x > MAX_SPEED) player.setLinearVelocity(MAX_SPEED, player.getLinearVelocity().y);
		if(player.getLinearVelocity().x < -MAX_SPEED) player.setLinearVelocity(-MAX_SPEED, player.getLinearVelocity().y);
		
		guiCam.position.set(rec.getCamera().position);
		guiCam.update();
		
	//	pe.update(delta);
	}
	
	public Vector2 getPosition(){
		return player.getWorldCenter();
	}
	
	public void dispose(){
		
	}
	public void buttonDown(Controller controller, int buttonCode) {
		if(buttonCode == InputConfig.J_JUMP){//Gdx.input.isKeyJustPressed(InputConfig.JUMP)){
			if(numContacts > 0){
				player.setLinearVelocity(player.getLinearVelocity().x, 0);
				temp.set(0, 4);
				player.applyLinearImpulse(temp, player.getWorldCenter(), true);
			}
			else{
				if(doubleJump){
					player.setLinearVelocity(player.getLinearVelocity().x, 0);
					temp.set(0, 4);
					player.applyLinearImpulse(temp, player.getWorldCenter(), true);
					doubleJump = false;
				}
			}
		}
		
		if(buttonCode == InputConfig.J_ACTION){
			if(colliding.size() > 0 && colliding.get(0).getUserData().toString().split("=")[1].equals("Computer")){
				rec.forward();
			}
			if(colliding.size() > 0 && colliding.get(0).getUserData().toString().split("=")[1].equals("Teleporter")){
				rec.teleport(colliding.get(0).getUserData().toString().split("=")[2]);
			}
			
			if(colliding.size() > 0 && colliding.get(0).getUserData().toString().split("=")[1].equals("Switch")){
				String id = colliding.get(0).getUserData().toString().split("=")[2];
				boolean on = Boolean.valueOf(colliding.get(0).getUserData().toString().split("=")[3]);
				colliding.get(0).getBody().setUserData("ITM=Switch=" + id + "=" + !on);
				colliding.get(0).setUserData("ITM=Switch=" + id + "=" + !on);
				for(int i : state.getSpittersByID(Integer.parseInt(id))){
					RecursiveGameState.SPstates.set(i, !RecursiveGameState.SPstates.get(i));
				}
			}
			if(colliding.size() > 0 && holding == null){
				if(colliding.get(0).getUserData() != null && colliding.get(0).getUserData().toString().split("=")[1].equals("Bomb")){
					holding = "BOMB";
					layerHoldOrigin = rec.game.countBackward();
					Recursive.forRemoval.add(colliding.get(0).getBody());
					colliding.remove(0);	
				}
				else if(colliding.get(0).getUserData() != null && colliding.get(0).getUserData().toString().split("=")[1].equals("Key")){
					int keyID = Integer.parseInt(colliding.get(0).getUserData().toString().split("=")[2]);
					if(!keys.contains(keyID))
					keys.add(keyID);
					
					Recursive.forRemoval.add(colliding.get(0).getBody());
					colliding.remove(0);	
				}
			}
			else if(holding == null){
				if(boxOver != null){
					holding = "BOX";
					layerHoldOrigin = rec.game.countBackward();
					Recursive.forRemoval.add(boxOver);
					boxOver = null;
				}
			}
			
		}
	}
	boolean axisXMoved = false;
	boolean axisYMoved = false;
	public void axisMoved(Controller controller, int axisCode, float value) {
		if(axisCode == InputConfig.J_AXIS_X){
			if(Math.abs(value) > 0.5f){
				if(!axisXMoved){
				axisXMoved = true;
				//axisX JUST MOVED
					if(value < 0){
						right = false;
						left = true;
						side = -1;
					}
					else{
						right = true;
						left = false;
						side = 1;
					}
				//end
				}
			}
			else{
				left = false;
				right = false;
				axisXMoved = false;
			}
		}
		if(axisCode == InputConfig.J_AXIS_Y){
			if(Math.abs(value) > 0.5f){
				if(!axisYMoved){
				axisYMoved = true;
				//axis y JUST MOVED
					
				//end
				}
			}
			else{
				axisYMoved = false;
			}
		}
	}

	public Body getBody() {
		return player;
	}

}
