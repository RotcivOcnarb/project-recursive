package com.digitalmatrix.pack.recursive.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class Assets {
	
	public static final String ROCKWALL = "sprites/rock.png";
	public static final String DOOR = "sprites/door.png";
	public static final String KEY = "sprites/key.png";
	public static final String BOMB = "sprites/bomb.png";
	public static final String COMPUTER = "sprites/computer.png";
	public static final String BOX = "sprites/box.png";
	public static final String MOUNTAINS_BG = "sprites/mountains.png";
	public static final String SKY_BG = "sprites/sky.png";
	public static final String FX_VERT = "sprites/effect_portal.png";
	public static final String FX_HOR = "sprites/effect_vertical.png";
	public static final String FRAME_BOSS = "images/frame2.png";
	public static final String FRAME_NO_BOSS = "images/frame1.png";
	public static final String F_FRAME_BOSS = "images/firstFrame2.png";
	public static final String F_FRAME_NO_BOSS = "images/firstFrame1.png";
	public static final String PLAYER_LIFE = "sprites/playerlife.png";
	public static final String BOSS_LIFE = "sprites/life.png";
	public static final String HURT = "sprites/hurt.png";
	public static final String BOSS_TEX  = "sprites/monster.png";
	public static final String PORTAL_SHEET = "sprites/portal.png";
	public static final String SPITTER = "sprites/spitter.png";
	public static final String ACID = "sprites/acid.png";
	public static final String SWITCH_ON = "sprites/switch on.png";
	public static final String SWITCH_OFF = "sprites/switch off.png";
	public static final String ENEMY = "sprites/enemy.png";
	public static final String SPIKE = "sprites/spike.png";
	public static final String GRAVEL = "sprites/gravel.png";
	public static final String SIGN = "sprites/sign.png";
	public static final String SIGN_FONT = "sprites/sign_font.png";

	
	public AssetManager manager;
	

	
	public Assets(){
		manager = new AssetManager();
		loadAssets();
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}
	
	public void loadAssets(){
		loadTextureWait(ROCKWALL);
		loadTextureWait(DOOR);
		loadTextureWait(KEY);
		loadTextureWait(BOMB);
		loadTextureWait(COMPUTER);
		loadTextureWait(BOX);
		loadTextureWait(MOUNTAINS_BG);
		loadTextureWait(SKY_BG);
		loadTextureWait(FX_VERT);
		loadTextureWait(FX_HOR);
		loadTextureWait(FRAME_BOSS);
		loadTextureWait(FRAME_NO_BOSS);
		loadTextureWait(F_FRAME_BOSS);
		loadTextureWait(F_FRAME_NO_BOSS);
		loadTextureWait(PLAYER_LIFE);
		loadTextureWait(BOSS_LIFE);
		loadTextureWait(HURT);
		loadTextureWait(BOSS_TEX);
		loadTextureWait(PORTAL_SHEET);
		loadTextureWait(SWITCH_ON);
		loadTextureWait(SWITCH_OFF);
		loadTextureWait(ENEMY);
		loadTextureWait(SPIKE);
		loadTextureWait(GRAVEL);
		loadTextureWait(SIGN);
		loadTextureWait(SIGN_FONT);
	}
	
//	public void loadFont(String file, int size){
//		manager.load(file);
//	}
	
	public void loadTextureAtlasWait(String file){
		manager.load(file, TextureAtlas.class);
	}
	
	public void loadTextureWait(String file){
		manager.load(file, Texture.class);
	}
	
	public void loadTexture(String file){
		manager.load(file, Texture.class);
		manager.finishLoadingAsset(file);
	}
	
	public TextureAtlas getTextureAtlas(String file){
		if(!manager.isLoaded(file)){
			loadTexture(file);
		}
		return manager.get(file, TextureAtlas.class);
	}
	
	public Texture getTexture(String file){
		if(!manager.isLoaded(file)){
			loadTexture(file);
		}
		return manager.get(file, Texture.class);
	}

}
