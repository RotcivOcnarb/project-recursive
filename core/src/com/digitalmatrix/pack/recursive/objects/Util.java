package com.digitalmatrix.pack.recursive.objects;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class Util {
	static Color tempC = Color.WHITE;
	public static Texture copyTexture(Texture t){
		
		FrameBuffer fb = new FrameBuffer(Format.RGBA8888, t.getWidth(), t.getHeight(), false);
		SpriteBatch sb = new SpriteBatch();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		tempC.set(1, 1, 1, 1);
		sb.setColor(tempC);
		FrameBufferManager.begin(fb);
		sb.begin();
			sb.draw(t, 0, 0);
		sb.end();
		FrameBufferManager.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		sb.dispose();
		
		return fb.getColorBufferTexture(); 
	}

}
