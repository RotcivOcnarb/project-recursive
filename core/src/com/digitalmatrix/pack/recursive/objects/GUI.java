package com.digitalmatrix.pack.recursive.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.digitalmatrix.pack.recursive.states.Manager;

public class GUI {
	
	static Matrix4 normalProjection;
	static BitmapFont font;
	static FirstBoss boss;
	//static Texture withBoss;
	//static Texture noBoss;
	static Manager manager;
	static Recursive first;
	
	@SuppressWarnings("deprecation")
	public static void create(Manager manager){
		GUI.manager = manager;
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(30);
		font.setColor(Color.RED);

	}
	
	static float drawLife = 100;
	static float bombOpacity = 0;
	static float bombOpacityAux = 0;
	public static void setBoss(FirstBoss boss){
		GUI.boss = boss;
	}
	
	public static void setFirst(Recursive rec){
		first = rec;
	}
	
	static Color tempC = Color.WHITE;
		public static void drawUI(SpriteBatch sb){
		
		//camera fixa
			
				drawLife += (Player.life - drawLife)/10.0;
			
				sb.begin();
				{
				Matrix4 last = sb.getProjectionMatrix();
				sb.setProjectionMatrix(normalProjection);

				sb.draw(manager.assets.getTexture(Assets.PLAYER_LIFE),
						448f/1920f * (float)Gdx.graphics.getWidth() ,  ((1080 - 16)/1080f *  (float)Gdx.graphics.getHeight()) - (64/1080f * (float)Gdx.graphics.getHeight()),
						(float)(drawLife/100.0) * (460/1920f * (float)Gdx.graphics.getWidth()), 64/1080f * (float)Gdx.graphics.getHeight());
				
				if(Player.holding != null && Player.holding.equals("BOMB")){
					bombOpacity  = 1;
				}
				else{
					bombOpacity = 0;
				}
				
				bombOpacityAux += (bombOpacity - bombOpacityAux)/10.0f;
				
				tempC.set(1, 1, 1, bombOpacityAux);
				sb.setColor(tempC);
				sb.draw(manager.assets.getTexture(Assets.BOMB),
						1347/1920f * Gdx.graphics.getWidth(),
						(1080-13)/1080f * Gdx.graphics.getHeight() - 85/1080f * Gdx.graphics.getHeight(),
						103/1920f * Gdx.graphics.getWidth(),
						85/1080f * Gdx.graphics.getHeight());
				tempC.set(1, 1, 1, 1);
				sb.setColor(tempC);
				if(boss == null){
					sb.draw(manager.assets.getTexture(Assets.F_FRAME_NO_BOSS), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				}
				else{
//					sb.draw(manager.assets.getTexture(Assets.BOSS_LIFE),
//							370/1920f * Gdx.graphics.getWidth(),
//							(1080 - 975)/1080f * Gdx.graphics.getHeight() - 84/1080f * Gdx.graphics.getHeight(),
//							(float)((boss.life/300.0) * (1023/1920f * Gdx.graphics.getWidth())),
//							84/1080f * Gdx.graphics.getHeight());
					sb.draw(manager.assets.getTexture(Assets.F_FRAME_BOSS), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				}
				
				//draw keys
				int i = 0;
				for(int key : Player.keys){
					font.draw(sb, "" + key, 10, Gdx.graphics.getHeight() - ((250/1080.0f) * Gdx.graphics.getHeight() + i *45));
					i++;
				}
				
				
//				if(first != null){
//			
//				tempC.set(1, 1, 1, 0.5f);
//				sb.setColor(tempC);
//				i = 0;
//				Recursive r = first;
//				while(r != null){
//					if(r.lastScreen != null)
//					sb.draw(r.lastScreen, i*Gdx.graphics.getWidth()/4f, 0, Gdx.graphics.getWidth()/4f, Gdx.graphics.getHeight()/4f);
//					r = r.child;
//					i++;
//				}
//				tempC.set(1, 1, 1, 1);
//				sb.setColor(tempC);
//				
//				}
				sb.setProjectionMatrix(last);
				
				}
				sb.end();
//				int cont = 0;
//				for(int k : Player.keys){
//					sb.draw(key, 
//							10,
//							Gdx.graphics.getHeight() - (150 + cont*40),
//							32, 32);
//					font.draw(sb, "" + k, 40, Gdx.graphics.getHeight() - (130 + cont*40));
//					cont++;
//					
//				}
				
				//460 x 64

				

//
//				font.draw(sb, "" + Player.life, Gdx.graphics.getWidth()/2 - playerLife.getWidth()/2 + 5,(float) (Gdx.graphics.getHeight() - 25));
//
//				if(boss != null){
//					sb.draw(bossLife, 50, 10, (float)((boss.life/300.0) * (Gdx.graphics.getWidth() - 100)), 30);
//				}

				
		
	}

}
