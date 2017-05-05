package com.digitalmatrix.pack.recursive.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class FirstBoss{
	
	public Body body;
	public ArrayList<Vector2> path;
	
	public float life = 300;
	public int contacts = 0;
	
	public FirstBoss(Body boss){
		this.body = boss;
		boss.setUserData("FirstBoss");
	}
	float timer = 0;
	public void render(SpriteBatch sb){
		
	}
	
	public void reset(){
		life = 300;
		timer = 0;
	}
	
	public void update(float delta){
		
		stateTimer += delta;
		
		if(life > 150){
			firstFase();
		}
		else{		
			secondFase();
		}
				
		if(mode == 0){
			leftAndRightMovement(delta);
			jumped = false;
		}
		else if(mode == 1){
			wiggle(delta);
			jumped = false;
		}
		else if(mode == 2){
			jump(delta);
		}
		else if(mode == 3){
			throwLateralRocks(delta);
			jumped = false;
		}
		

		
	}
	float stateTimer = 0;
	int mode = 0;
	public void firstFase(){
		if(stateTimer < 10){
			mode = 0;
		}
		else if(stateTimer < 13){
			mode = 1;
		}
		else if(stateTimer < 18){
			mode = 2;
		}
		else{
			stateTimer = 0;
		}
	}
	
	public void secondFase(){
		if(stateTimer < 10){
			mode = 0;
		}
		else if(stateTimer < 13){
			mode = 3;
		}
		else if(stateTimer < 23){
			mode = 0;
		}
		else if(stateTimer < 26){
			mode = 1;
		}
		else if(stateTimer < 31){
			mode = 2;
		}
		else{
			stateTimer = 0;
		}
	}
	
	public void thirdFase(){
		
	}
	
	float angle = 0;
	public void throwLateralRocks(float delta){
		timer += delta;
		if(timer > 0.2){
			timer = 0;
			angle += 0.5f;
		
			throwLat(1);
			throwLat(-1);
			
		}
	}
	CircleShape cs;
	Vector2 temp = Vector2.Zero.cpy(), temp2 = temp.cpy(), temp3 = temp.cpy();
	public void throwLat(int side){
		
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		
		Body r = body.getWorld().createBody(def);
		r.setUserData("BossGravel");
		if(cs == null ){
			cs = new CircleShape();
			cs.setRadius(5/Recursive.SCALE_DIV);
		}
		
		Fixture f = r.createFixture(cs, 1);
		f.setSensor(true);
		
		r.setTransform(body.getWorldCenter(), 0);
		temp.set((float)(Math.cos(Math.sin(angle)*0.8+0.5f))*side, (float)(Math.sin(Math.sin(angle)*0.8+0.5f)));
		r.applyLinearImpulse(temp, r.getWorldCenter(), true);
	
	}
	
	boolean jumped = false;
	boolean appl = false;
	public void jump(float delta){
		if(!appl){
			appl = true;
			temp.set(0, 2000);
			body.applyLinearImpulse(temp, body.getWorldCenter(), true);
		}
		if(!jumped){
			if(contacts == 0){
				jumped = true;
			}
		}
		if(jumped){
			
			if(contacts > 0){
				generateRocks();
				jumped = false;
			}
		}
	}
	PolygonShape ps;
	public void generateRocks(){
		for(int i = 0; i < 10; i ++){
			
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		
		Body b = body.getWorld().createBody(def);
		b.setFixedRotation(true);
		b.setLinearDamping(2);
		b.setUserData("BossSpike");
		
		if(ps == null){ps = new PolygonShape();
		temp.set(2*0.2f, -10*0.2f);
		temp2.set(4*0.2f, 0);
		ps.set(new Vector2[]{Vector2.Zero, temp2, temp});
		}
		
		Fixture f = b.createFixture(ps, 1);
		f.setSensor(true);
		
		b.setTransform((float)(Math.random()*30) - 15 + 27, 37 - (float)(Math.random()*5), 0);

		}
	}
	
	public void wiggle(float delta){
		timer += delta;
		if(timer > 0.1){
			timer = 0;
			temp.set(0, 200);
			body.applyLinearImpulse(temp, body.getWorldCenter(), true);
		}
		body.setLinearVelocity(0,  body.getLinearVelocity().y);
	}
	

	boolean left = false;;
	public void leftAndRightMovement(float delta){
		timer += delta;
		
		if(timer > 5){
			timer = 0;
			left = !left;
			body.setLinearVelocity(0, body.getLinearVelocity().y);
		}
		
		if(left){
			temp.set(-500, 0);
			body.applyForceToCenter(temp, true);
		}
		else{
			temp.set(500, 0);
			body.applyForceToCenter(temp, true);
		}
		
	}
	
	public void dispose(){
		
	}
}
