package com.digitalmatrix.pack.recursive.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Spitter{
	
	Recursive rec;
	Vector2 position;
	Body body;
	private boolean on = true;
	float timer = 0;
	Array<Body> bodies;
	private int id;
	
	Vector2 temp = Vector2.Zero.cpy();
	public Spitter(Vector2 position, Recursive rec, World world, boolean on, int id){
		this.position = position;
		this.setId(id);
		this.setOn(on);
		this.rec = rec;
		BodyDef def = new BodyDef();
		def.position.set(position);
		def.type = BodyType.StaticBody;
		
		bodies = new Array<Body>();
		
		body = world.createBody(def);
		PolygonShape ps = new PolygonShape();
		temp.set(8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
		ps.setAsBox(8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, temp, 0);
		Fixture f = body.createFixture(ps, 1);
		f.setUserData("Spitter");
		body.setUserData("Spitter");
		
		ps.dispose();
	}

	public void render(SpriteBatch sb) {
		bodies.clear();
		body.getWorld().getBodies(bodies);
		for(Body b : bodies){
			if(b.getUserData() != null && b.getUserData().equals("Acid")){
				sb.draw(rec.getTexture(Assets.ACID), 
						b.getWorldCenter().x - 5 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						b.getWorldCenter().y - 5 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						10 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 10 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
		}
		
		sb.draw(rec.getTexture(Assets.SPITTER),
				position.x, position.y, 
				16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
		if(isOn()){
			timer += Gdx.graphics.getDeltaTime();
			if(timer > 0.05f){
				timer = 0;
				for(int i = 0; i < 3; i ++)
				createAcid();
			}
		}
		
		
	}
	
	public void createAcid(){
		World world = body.getWorld();
		BodyDef def = new BodyDef();
		def.position.set(position.cpy().add((float)Math.random() * 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE + 4 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, -(float)Math.random() * Recursive.SCALE_MULT * Recursive.TILE_SCALE));
		def.type = BodyType.DynamicBody;
		
		Body acid = world.createBody(def);
		CircleShape ps = new CircleShape();
		ps.setRadius(3 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
		Fixture f = acid.createFixture(ps, 1);
		f.setSensor(true);
		acid.setUserData("Acid");
		acid.setLinearDamping(4);
		f.setUserData("Acid");
		
		ps.dispose();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}
	

}
