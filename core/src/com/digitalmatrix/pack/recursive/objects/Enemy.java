package com.digitalmatrix.pack.recursive.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy {
	
	public Body enemy;
	public int ID;
	int direction = 1;
	float directionTimer = 0;
	Recursive rec;
	private boolean alive = true;
	
	public Enemy(Body enemy, int ID, Recursive rec){
		this.enemy = enemy;
		this.ID = ID;
		this.rec = rec;
		enemy.setLinearDamping(10);
		
		direction = (Math.random() - .5f < 0) ? -1 : 1;
	}
	
	public void render(SpriteBatch sb){
		if(direction == 1){
		sb.draw(rec.getTexture(Assets.ENEMY), 
				enemy.getWorldCenter().x - 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
				enemy.getWorldCenter().y - 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
				16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
		}
		else{
			sb.draw(rec.getTexture(Assets.ENEMY), 
					enemy.getWorldCenter().x + 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
					enemy.getWorldCenter().y - 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
					-16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
		}
	}
	
	public void kill(){
		if(!Recursive.forRemoval.contains(enemy, false)){
			Recursive.forRemoval.add(enemy);
			setAlive(false);
		}
	}
	
	public void update(float delta){
			enemy.applyForce(Vector2.X.cpy().scl(direction).scl(100), enemy.getWorldCenter(), true);
			
			if(enemy.getLinearVelocity().len2() < 0.01f){
				directionTimer += delta;
				if(directionTimer > 0.1){
					directionTimer = 0;
					direction *= -1;
				}
				
			}
			
//			//enemy.setLinearVelocity(path.get(nextPoint).cpy().sub(enemy.getWorldCenter()).nor());
//			if(path.get(nextPoint).dst(enemy.getWorldCenter()) < 0.1f){
//				if(backward) nextPoint --;
//				else nextPoint ++;
//				enemy.setLinearVelocity(Vector2.Zero);
//				if(nextPoint == path.size()){
//					nextPoint = path.size()-1;
//					backward = true;
//				}
//				if(nextPoint == -1){
//					nextPoint = 0;
//					backward = false;
//				}
//			}
		
	}
	
	public void dispose(){
		
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
