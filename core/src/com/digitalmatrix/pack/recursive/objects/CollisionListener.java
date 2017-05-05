package com.digitalmatrix.pack.recursive.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.digitalmatrix.pack.recursive.recstates.RecursiveGameState;

public class CollisionListener implements ContactListener{
	
	RecursiveGameState game;
	
	public CollisionListener(World world, RecursiveGameState game){
		world.setContactListener(this);
		this.game = game;
	}

	public void beginContact(Contact contact) {
		if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().toString().startsWith("SIGN")){
				game.getPlayer().signMessage = contact.getFixtureB().getUserData().toString().split("=")[1];
			}
		}
		if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().startsWith("SIGN")){
				game.getPlayer().signMessage = contact.getFixtureA().getUserData().toString().split("=")[1];
			}
		}

		
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("Acid")){
			if(contact.getFixtureB().getUserData() == null || !contact.getFixtureB().getUserData().equals("Acid")){
				if(contact.getFixtureB().getUserData() == null || !contact.getFixtureB().getUserData().equals("Spitter")){
					if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false))
					Recursive.forRemoval.add(contact.getFixtureA().getBody());
					
					if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
						Player.life -= 10;
					}
					if(contact.getFixtureB().getBody().getUserData() instanceof Enemy){
						Enemy en = (Enemy)contact.getFixtureB().getBody().getUserData();
						en.kill();
					}
				}
			}
		}
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("Acid")){
			if(contact.getFixtureA().getUserData() == null || !contact.getFixtureA().getUserData().equals("Acid")){
				if(contact.getFixtureA().getUserData() == null || !contact.getFixtureA().getUserData().equals("Spitter")){
					if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false))
					Recursive.forRemoval.add(contact.getFixtureB().getBody());
					
					if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
						Player.life -= 10;
					}
					if(contact.getFixtureA().getBody().getUserData() instanceof Enemy){
						Enemy en = (Enemy)contact.getFixtureA().getBody().getUserData();
						en.kill();
					}
				}
			}
		}
		
		if(game.boss != null){
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("BOSS_FEET")){
			if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("BLOCK")){
				game.boss.contacts ++;
			}
		}
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("BOSS_FEET")){
			if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("BLOCK")){
				game.boss.contacts ++;
			}
		}
		}
		
		//boss projectles hit player
				if(contact.getFixtureA().getBody().getUserData() != null && (contact.getFixtureA().getBody().getUserData().equals("BossSpike") || contact.getFixtureA().getBody().getUserData().equals("BossGravel"))){
					if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
						if(game.getPlayer().hitTimer < 0){
							Player.life -= 15;
							game.getPlayer().hitTimer = 2;
							if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false)){
								Recursive.forRemoval.add(contact.getFixtureA().getBody());
							}
						}
					}
				}
				
				if(contact.getFixtureB().getBody().getUserData() != null && (contact.getFixtureB().getBody().getUserData().equals("BossSpike") || contact.getFixtureB().getBody().getUserData().equals("BossGravel"))){
					if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
						if(game.getPlayer().hitTimer < 0){
							Player.life -= 15;
							game.getPlayer().hitTimer = 2;
							if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false)){
								Recursive.forRemoval.add(contact.getFixtureB().getBody());
							}
						}
					}
				}
		
		//boss top rocks falling
		if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("BossSpike")){
			if(contact.getFixtureB().getBody().getUserData() != null && (contact.getFixtureB().getBody().getUserData().equals("Platform") || contact.getFixtureB().getBody().getUserData().equals("BLOCK"))){
				if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false))
				Recursive.forRemoval.add(contact.getFixtureA().getBody());
			}
		}
		if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("BossSpike")){
			if(contact.getFixtureA().getBody().getUserData() != null && (contact.getFixtureA().getBody().getUserData().equals("Platform") || contact.getFixtureA().getBody().getUserData().equals("BLOCK"))){
				if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false))
				Recursive.forRemoval.add(contact.getFixtureB().getBody());
			}
		}
		//boss projectile laterals
		if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("BossGravel")){
			if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("BLOCK")){
				if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false))
				Recursive.forRemoval.add(contact.getFixtureA().getBody());
				
			}
		}
		if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("BossGravel")){
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("BLOCK")){
				if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false))
				Recursive.forRemoval.add(contact.getFixtureB().getBody());
			}
		}
		
		//player projectile hits enemy
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData() instanceof Enemy){
			if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().toString().startsWith("PROJ")){
				if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false)){
					Recursive.forRemoval.add(contact.getFixtureA().getBody());
				}
			}
		}
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData() instanceof Enemy){
			if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().startsWith("PROJ")){
				if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false)){
					Recursive.forRemoval.add(contact.getFixtureB().getBody());
				}
			}
		}
		//bomb hits rock wall
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("PROJBOMB")){
			if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("Rock")){
				if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false)){
					Recursive.forRemoval.add(contact.getFixtureB().getBody());
				}
				game.pe.setPosition(contact.getFixtureB().getBody().getPosition().x + 1, contact.getFixtureB().getBody().getPosition().y + 2);
				game.pe.reset();
			}
		}
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("PROJBOMB")){
			if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("Rock")){
				if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false)){
					Recursive.forRemoval.add(contact.getFixtureA().getBody());
				}
				game.pe.setPosition(contact.getFixtureA().getBody().getPosition().x + 1, contact.getFixtureA().getBody().getPosition().y + 2);
				game.pe.reset();
			}
		}
		//player hits door
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().split("=")[0].equals("Door")){
			if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
				int id = Integer.parseInt(contact.getFixtureA().getUserData().toString().split("=")[1]);
				if(Player.keys.contains(id)){
					if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false))
					Recursive.forRemoval.add(contact.getFixtureA().getBody());
				}	
			}
		}
		
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().toString().split("=")[0].equals("Door")){
			if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
				int id = Integer.parseInt(contact.getFixtureB().getUserData().toString().split("=")[1]);
				if(Player.keys.contains(id)){
					if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false))
					Recursive.forRemoval.add(contact.getFixtureB().getBody());
				}	
			}
		}
		
		//player intersects item
		if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody()) &&
				!contact.getFixtureA().isSensor() &&
				contact.getFixtureB().getUserData()!= null &&
				contact.getFixtureB().getUserData().toString().startsWith("ITM")){
			
			if(!game.getPlayer().colliding.contains(contact.getFixtureB().getBody()))
				game.getPlayer().colliding.add(contact.getFixtureB());
			
		}
		if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody()) &&
				!contact.getFixtureB().isSensor() &&
				contact.getFixtureA().getUserData()!= null &&
				contact.getFixtureA().getUserData().toString().startsWith("ITM")){
			
			if(!game.getPlayer().colliding.contains(contact.getFixtureA().getBody()))
				game.getPlayer().colliding.add(contact.getFixtureA());
			
		}
		
		//player hits ground
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("PLAYER_FEET") && contact.getFixtureA().getUserData() != null &&
				(contact.getFixtureA().getUserData().toString().startsWith("BLOCK")||
						contact.getFixtureA().getUserData().equals("Platform") ||
						contact.getFixtureA().getUserData().toString().startsWith("Box") || 
						contact.getFixtureA().getUserData().toString().startsWith("Door") ||
						contact.getFixtureA().getUserData().toString().startsWith("Rock"))){
			if(contact.getFixtureB().isSensor()){
				game.getPlayer().canJump = true;
				game.getPlayer().numContacts ++;
				game.getPlayer().doubleJump = true;
				System.out.println("Player contact " + game.getPlayer().numContacts);
			}
		}
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("PLAYER_FEET") && contact.getFixtureB().getUserData() != null &&
				(contact.getFixtureB().getUserData().toString().startsWith("BLOCK")||
						contact.getFixtureB().getUserData().equals("Platform") ||
						contact.getFixtureB().getUserData().toString().startsWith("Box") ||
						contact.getFixtureB().getUserData().toString().startsWith("Door") ||
						contact.getFixtureB().getUserData().toString().startsWith("Rock"))){
			if(contact.getFixtureA().isSensor()){

				game.getPlayer().canJump = true;
				game.getPlayer().numContacts ++;
				game.getPlayer().doubleJump = true;
				System.out.println("Player contact " + game.getPlayer().numContacts);
			}
		}	
		
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().startsWith("Box") && contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
			Player.boxOver = contact.getFixtureA().getBody();
		}
		
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().toString().startsWith("Box") && contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
			Player.boxOver = contact.getFixtureB().getBody();
		}
		
		
		
	}

	public void endContact(Contact contact) {
		if(game.boss != null){
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("BOSS_FEET")){
			if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("BLOCK")){
				game.boss.contacts --;
			}
		}
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("BOSS_FEET")){
			if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("BLOCK")){
				game.boss.contacts --;
			}
		}
		}
		if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().toString().startsWith("SIGN")){
				game.getPlayer().signMessage = "";
			}
		}
		if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().startsWith("SIGN")){
				game.getPlayer().signMessage = "";
			}
		}

		
		//player steps off item
		if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody()) &&
				!contact.getFixtureA().isSensor() &&
				contact.getFixtureB().getUserData()!= null &&
				contact.getFixtureB().getUserData().toString().startsWith("ITM")){
			
			if(game.getPlayer().colliding.contains(contact.getFixtureB())){
				game.getPlayer().colliding.remove(contact.getFixtureB());
			}
			
		}
		if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody()) &&
				!contact.getFixtureB().isSensor() &&
				contact.getFixtureA().getUserData()!= null &&
				contact.getFixtureA().getUserData().toString().startsWith("ITM")){
			
			if(game.getPlayer().colliding.contains(contact.getFixtureA())){
				game.getPlayer().colliding.remove(contact.getFixtureA());
			}
			
		}
		
		//player jumps
		if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody()) && contact.getFixtureA().getUserData() != null &&
				(contact.getFixtureA().getUserData().toString().startsWith("BLOCK")||
						contact.getFixtureA().getUserData().equals("Platform") || 
						contact.getFixtureA().getUserData().toString().startsWith("Box") ||
						contact.getFixtureA().getUserData().toString().startsWith("Door") ||
						contact.getFixtureA().getUserData().toString().startsWith("Rock"))){
			if(contact.getFixtureB().isSensor()){
				game.getPlayer().canJump = false;
				game.getPlayer().numContacts --;
			}
		}
		if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody()) && contact.getFixtureB().getUserData() != null &&
				(contact.getFixtureB().getUserData().toString().startsWith("BLOCK")||
						contact.getFixtureB().getUserData().equals("Platform") ||
						contact.getFixtureB().getUserData().toString().startsWith("Box") ||
						contact.getFixtureB().getUserData().toString().startsWith("Door") ||
						contact.getFixtureB().getUserData().toString().startsWith("Rock"))){
			if(contact.getFixtureA().isSensor()){
				game.getPlayer().canJump = false;
				game.getPlayer().numContacts --;
			};
		}
		
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().startsWith("Box") && contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
			Player.boxOver = null;
		}
		
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().toString().startsWith("Box") && contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
			Player.boxOver = null;
		}
		
	}

	public void preSolve(Contact contact, Manifold oldManifold) {
		if(contact.getFixtureA().getBody().getUserData() instanceof Enemy && contact.getFixtureB().getBody().getUserData() instanceof Enemy){
			contact.setEnabled(false);
		}
		
		//game.getPlayer().getBody() hits boss
		if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("FirstBoss")){
				if(game.getPlayer().hitTimer < 0){
					Player.life -= 15;
					game.getPlayer().hitTimer = 2;
				}
			}
		}
		if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("FirstBoss")){
				if(game.getPlayer().hitTimer < 0){
					Player.life -= 15;
					game.getPlayer().hitTimer = 2;
				}
			}
		}
		
		
		
		//enemy hits game.getPlayer().getBody()
				if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData() instanceof Enemy){
					if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
						
						if(game.getPlayer().hitTimer < 0){
							Player.life -= 30;
							game.getPlayer().hitTimer = 2;
						}
					}
				}
				
				if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData() instanceof Enemy){
					if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
						
						if(game.getPlayer().hitTimer < 0){
							Player.life -= 30;
							game.getPlayer().hitTimer = 2;
						}
					}
				}
		
		//first boss pass through platforms
		if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("FirstBoss")){
			if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("Platform")){
				contact.setEnabled(false);
			}
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("Platform")){
				contact.setEnabled(false);
			}
		}
		if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("FirstBoss")){
			if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("Platform")){
				contact.setEnabled(false);
			}
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("Platform")){
				contact.setEnabled(false);
			}
		}
		
		//game.getPlayer().getBody() passes through one-way doors
		if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().toString().startsWith("OWD")){
				String side = contact.getFixtureB().getBody().getUserData().toString().split("=")[1];
				if(side.equals("Left")){
					if(game.getPlayer().getBody().getLinearVelocity().x < 0){
						contact.setEnabled(false);
					}
				}
				if(side.equals("Right")){
					if(game.getPlayer().getBody().getLinearVelocity().x > 0){
						contact.setEnabled(false);
					}
				}
				
			}
		}
		if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().toString().startsWith("OWD")){
				String side = contact.getFixtureA().getBody().getUserData().toString().split("=")[1];
				if(side.equals("Left")){
					if(game.getPlayer().getBody().getLinearVelocity().x < 0){
						contact.setEnabled(false);
					}
				}
				if(side.equals("Right")){
					if(game.getPlayer().getBody().getLinearVelocity().x > 0){
						contact.setEnabled(false);
					}
				}
				
			}
		}
		
		//game.getPlayer().getBody() projectile hits boss
		if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("PROJROCK")){
			if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("FirstBoss")){
				game.boss.life -= 0.1f;
			}
		}
		if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("PROJROCK")){
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("FirstBoss")){
				game.boss.life -= 0.1f;
			}
		}
		
		//game.getPlayer().getBody() bomb hits boss
		if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("PROJBOMB")){
			if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("FirstBoss")){
				game.boss.life -= 100f;
			}
		}
		if(contact.getFixtureB().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData().equals("PROJBOMB")){
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureA().getBody().getUserData().equals("FirstBoss")){
				game.boss.life -= 100f;
			}
		}
		
		//projectile hits anything
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().toString().startsWith("PROJ")){
			if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
			contact.setEnabled(false);
			}
			else{
				if(!Recursive.forRemoval.contains(contact.getFixtureA().getBody(), false)){
					Recursive.forRemoval.add(contact.getFixtureA().getBody());
				}
			}
		}
		
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().toString().startsWith("PROJ")){
			if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
			contact.setEnabled(false);
			}
			else{
				if(!Recursive.forRemoval.contains(contact.getFixtureB().getBody(), false)){
					Recursive.forRemoval.add(contact.getFixtureB().getBody());
				}
			}
		}
		//game.getPlayer().getBody() jump up on platform
		if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("Platform")){
			if(contact.getFixtureB().getBody().equals(game.getPlayer().getBody())){
				if(!contact.getWorldManifold().getNormal().equals(Vector2.Y)){
					contact.setEnabled(false);
				}
			}
		}
		if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("Platform")){
			if(contact.getFixtureA().getBody().equals(game.getPlayer().getBody())){
				if(!contact.getWorldManifold().getNormal().equals(Vector2.Y)){
					contact.setEnabled(false);
				}
			}
		}
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
