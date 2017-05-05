package com.digitalmatrix.pack.recursive.recstates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.digitalmatrix.pack.recursive.RecursiveGameSetup;
import com.digitalmatrix.pack.recursive.devanalysis.ErrorElement;
import com.digitalmatrix.pack.recursive.objects.Assets;
import com.digitalmatrix.pack.recursive.objects.CollisionListener;
import com.digitalmatrix.pack.recursive.objects.Enemy;
import com.digitalmatrix.pack.recursive.objects.FirstBoss;
import com.digitalmatrix.pack.recursive.objects.FrameBufferManager;
import com.digitalmatrix.pack.recursive.objects.GUI;
import com.digitalmatrix.pack.recursive.objects.InputConfig;
import com.digitalmatrix.pack.recursive.objects.Player;
import com.digitalmatrix.pack.recursive.objects.Recursive;
import com.digitalmatrix.pack.recursive.objects.Spitter;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

public class RecursiveGameState extends RecState implements InputProcessor{

	

	BitmapFont font;
	BitmapFont worldFont;
	OrthographicCamera guiCam;
	float ratew, rateh;
	Matrix4 normalProjection;
	World world;
	Box2DDebugRenderer b2dr;

	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	
	private Player player;
	Vector2 initPos;
	TiledMap map;
	OrthogonalTiledMapRenderer otm;
	String currentMap;
	double startAngle = 50;
	double angleRange = 35;
	float opacity = 0;
	float canvasPos = 1000;
	SpriteBatch sb;
	Animation portal;
	
	public ParticleEffect pe;
	
	FrameBuffer fboA, fboB;

	float bgFactor = 5;  //0.3f
	
	ArrayList<Sprite> sprites;	
	
	ArrayList<Spitter> spitters;
	public static ArrayList<Boolean> SPstates;
	
	CollisionListener collisions;
	float margin = 100;
	
	ShapeRenderer srenderer;
	Vector2 rockSize = new Vector2(32f, 48f);
	Vector2 doorSize = new Vector2(16f, 48f);
	ShaderProgram shader;
	
	@SuppressWarnings("deprecation")
	public RecursiveGameState(Recursive rec, SpriteBatch sb, String level, boolean isFirst) {
		super(rec);
		spitters = new ArrayList<Spitter>();
		
		pe = new ParticleEffect();
		pe.load(Gdx.files.internal("particles/rock.par"), Gdx.files.internal("particles"));
		pe.scaleEffect(1f/Recursive.SCALE_DIV);
		pe.start();

		this.sb = sb;
		shader = new ShaderProgram(Gdx.files.internal("data/shaders/blur.vert"), Gdx.files.internal("data/shaders/blur.frag"));
		
		shader.begin();
		shader.setUniformf("resolution", Gdx.graphics.getWidth());
		shader.end();
		canReload(level);
		
		if(isFirst){
			firstCreate();
		}

		collisions = new CollisionListener(world, this);
		
		sprites = new ArrayList<Sprite>();
		
		// all aligned.
		Texture walkSheet = rec.getTexture(Assets.PORTAL_SHEET);
				TextureRegion[][] tmp = TextureRegion.split(walkSheet, 
						walkSheet.getWidth() / 4,
						walkSheet.getHeight() / 1);

				// Place the regions into a 1D array in the correct order, starting from the top 
				// left, going across first. The Animation constructor requires a 1D array.
				TextureRegion[] walkFrames = new TextureRegion[4 * 1];
				int index = 0;
				for (int i = 0; i < 1; i++) {
					for (int j = 0; j < 4; j++) {
						walkFrames[index++] = tmp[i][j];
					}
				}

				// Initialize the Animation with the frame interval and array of frames
				portal = new Animation(0.1f, walkFrames);


		guiCam = new OrthographicCamera(Gdx.graphics.getWidth()*50f, Gdx.graphics.getHeight()*50f);
		guiCam.zoom = 1f/Recursive.SCALE_DIV;
		ratew = guiCam.viewportWidth/getCamera().viewportWidth;  //<--- you should calculate these 2 only once.
		rateh = guiCam.viewportHeight/getCamera().viewportHeight;
		
		fboA = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		fboB = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		loadImages();
		
		srenderer = new ShapeRenderer();

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/vcr_osd.ttf"));
		font = generator.generateFont(30);
		font.setColor(Color.RED);
		worldFont = generator.generateFont(2);
		worldFont.setColor(Color.RED);
		RecursiveGameSetup.processor.addProcessor(this);
		normalProjection = new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
	}
	
	
	public void firstCreate() {
		SPstates = new ArrayList<Boolean>();
		for(Spitter s : spitters){
			SPstates.add(s.isOn());
		}
		Player.keys.clear();
		create();
	}
	
	public boolean hasBeenCreated = false;
	public void create() {
		hasBeenCreated = true;
		removedBoxes = false;
		
		//Gdx.input.setInputProcessor(this);
	}
	
	float timer = 0;
	boolean removedBoxes = false;
	public void update(float delta) {
		
		timer += delta*100;
				
		for(int i = 0; i < spitters.size(); i ++){
			if(SPstates.size() == spitters.size())
			spitters.get(i).setOn(SPstates.get(i));
			else
				System.out.println("spitters: " + spitters.size() + " spstates: " + SPstates.size());
		}
		
		allBodies.clear();
		world.getBodies(allBodies);
		
		for(Body b : allBodies){
			if(!removedBoxes){
				if(b.getUserData() != null && b.getUserData().toString().startsWith("Box")){
					int layer = Integer.parseInt(b.getUserData().toString().split("=")[1]);
					if(layer != rec.game.countBackward()){
						if(!Recursive.forRemoval.contains(b, false))
						Recursive.forRemoval.add(b);
					}
				}
			}
			if(b.getUserData() != null && b.getUserData().toString().startsWith("ITM=Key")){
				if(Player.keys.contains(Integer.parseInt(b.getUserData().toString().split("=")[2]))){
					if(!Recursive.forRemoval.contains(b, false))
						Recursive.forRemoval.add(b);
				}
			}
		}
		removedBoxes = true;
				
		getPlayer().input();
		world.step(1f/60f, 6, 2);
		getPlayer().update(delta);
				
		if(Player.life <= 0){
			Player.life = 100;
			rec.changeState(1);
			rec.resetGame(rec == rec.game.first);
		}
		
		if(boss != null){
			if(boss.life <= 0){
				if(!Recursive.forRemoval.contains(boss.body, false))
					Recursive.forRemoval.add(boss.body);

				BodyDef def = new BodyDef();
				def.type = BodyType.StaticBody;
				
				Body key = world.createBody(def);
				
				CircleShape cs = new CircleShape();
				cs.setRadius(0.5f);
				temp.set(0.5f, 0.5f);
				cs.setPosition(temp);
				
				Fixture f = key.createFixture(cs, 1);
				cs.dispose();
				f.setSensor(true);
				
				f.setUserData("ITM=Key=7");
				key.setUserData("ITM=Key=7");
				
				key.setTransform(boss.body.getWorldCenter(), 0);
				
				boss = null;
			}
		}
		
		if(boss != null){
			boss.update(delta);
		}
		for(int i = enemies.size() - 1; i >= 0; i --){
			Enemy e = enemies.get(i);
			if(e.isAlive())
			e.update(delta);
			else{
				enemies.remove(e);
			}
		}
		
		temp.set(getCamera().position.x, getCamera().position.y);
		Vector2 dist = getPlayer().getPosition().cpy().sub(temp).scl(1f/5);
		getCamera().position.add(dist.x, dist.y, 0);
		
		if(Gdx.input.isKeyJustPressed(InputConfig.ESC)){
			rec.changeState(0);
		}
		if(Gdx.input.isKeyJustPressed(InputConfig.RELEASE)){
			releaseItem();
		}
		
		if(Controllers.getControllers().size > 0){
			if(Controllers.getControllers().get(0).getButton(InputConfig.J_THROW)){//if(Gdx.input.isKeyPressed(InputConfig.THROW)){
					throwforce += Gdx.graphics.getDeltaTime()*80;
			}
			if(Controllers.getControllers().get(0).getButton(InputConfig.J_RELEASE)){
					releaseItem();
			}
		}
		if(Gdx.input.isKeyPressed(InputConfig.THROW)){
			throwforce += Gdx.graphics.getDeltaTime()*80;
		}
		
		if(getCamera().position.x < Gdx.graphics.getWidth()/2f /Recursive.SCALE_DIV) getCamera().position.x = Gdx.graphics.getWidth()/2f /Recursive.SCALE_DIV;
		if(getCamera().position.y < Gdx.graphics.getHeight()/2f /Recursive.SCALE_DIV) getCamera().position.y = Gdx.graphics.getHeight()/2f /Recursive.SCALE_DIV;
		
		if(getCamera().position.x > map.getProperties().get("width", Integer.class) - (Gdx.graphics.getWidth()/2f /Recursive.SCALE_DIV)) getCamera().position.x = map.getProperties().get("width", Integer.class) - (Gdx.graphics.getWidth()/2f /Recursive.SCALE_DIV);
		if(getCamera().position.y > map.getProperties().get("height", Integer.class) - (Gdx.graphics.getHeight()/2f /Recursive.SCALE_DIV)) getCamera().position.y = map.getProperties().get("height", Integer.class) - (Gdx.graphics.getHeight()/2f /Recursive.SCALE_DIV);

	}

	Color tempC = Color.WHITE;
	Array<Body> bodies = new Array<Body>();
	public void render(SpriteBatch sb) {
		
		//System.out.println(player.getPosition());
		
		//caso essa camada seja um background de camadas futuras
		if(rec.game.layer(rec.game.current) > rec.game.layer(rec)){
			if(!rec.child.goingBack){
				opacity += Gdx.graphics.getDeltaTime();
				if(opacity > 1) opacity = 1;
				
				canvasPos += (0 - canvasPos)/10.0;
				
			}
			else{
				opacity -= Gdx.graphics.getDeltaTime();
				canvasPos += (1000 - canvasPos)/10.0;
				if(opacity < 0){
					opacity = 0;
					rec.child.goingBack = false;
					rec.child.game.backward();
				}
			}
			
			FrameBufferManager.begin(rec.buffer);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

				rec.child.render(sb);
			FrameBufferManager.end();
			
			sb.begin();
			{
				sb.setProjectionMatrix(normalProjection);
	
				tempC.set((1-opacity)/2f + .5f, (1-opacity)/2 + .5f, (1-opacity)/2f + .5f, 1);
				sb.setColor(tempC);
				//sb.draw(rec.lastScreen, 0, rec.lastScreen.getHeight(), rec.lastScreen.getWidth(), -rec.lastScreen.getHeight());
				sb.draw(rec.lastScreen, 0, 0);
				tempC.set(1, 1, 1, opacity);
				sb.setColor(tempC);
				sb.draw(rec.buffer.getColorBufferTexture(), margin, -margin + rec.buffer.getColorBufferTexture().getHeight() + canvasPos,
						rec.buffer.getColorBufferTexture().getWidth() - margin*2,
						-rec.buffer.getColorBufferTexture().getHeight() + margin*2);
				
				FirstBoss childBoss = ((RecursiveGameState)rec.child.states.get(1)).boss;
				
				if(childBoss == null){
				sb.draw(rec.getTexture(Assets.FRAME_NO_BOSS), margin-10, -margin + rec.buffer.getColorBufferTexture().getHeight() + canvasPos + 10,
						rec.buffer.getColorBufferTexture().getWidth() - margin*2 + 20,
						-rec.buffer.getColorBufferTexture().getHeight() + margin*2 - 20);
				}
				else{
					
				sb.draw(rec.getTexture(Assets.FRAME_BOSS), margin-10, margin + canvasPos - 10,
						rec.buffer.getColorBufferTexture().getWidth() - margin*2 + 20,
						rec.buffer.getColorBufferTexture().getHeight() - margin*2 + 20);
				
				}
				tempC.set(1, 1, 1, 1);
				sb.setColor(tempC);
	
				
				sb.setProjectionMatrix(getCamera().combined);
			}
			sb.end();

			
		}
		//Caso essa for a camada que está sendo usada pelo jogador
		else{
			canvasPos = 1000;
			opacity = 0;
			drawContents(sb);
		GUI.setFirst(rec.game.first);
		}
		GUI.setBoss(boss);

	}

	public void slow(String action, double miliseconds){
		ErrorElement error = new ErrorElement(action, (float)miliseconds, rec.game.countTotal(), rec.game.countBackward(), rec.level);
		error.sendToDev();
	}
	
	float stateTime = 0;
	Vector2 bgParallax = new Vector2();
	public void drawContents(SpriteBatch sb){
		long a = System.nanoTime();
	
		sb.begin();
		{
			Texture mountains = rec.getTexture(Assets.MOUNTAINS_BG);
			Texture sky = rec.getTexture(Assets.SKY_BG);
			
			sb.setProjectionMatrix(normalProjection);
			for(int i = -1; i < 2; i ++){
				sb.draw(mountains,
						(Gdx.graphics.getWidth() - mountains.getWidth()*5)/2 - getCamera().position.x/bgFactor + i*mountains.getWidth()*5,
						(Gdx.graphics.getHeight() - mountains.getHeight()*5-sky.getHeight()*5)/2 - getCamera().position.y/bgFactor,
						mountains.getWidth()*5,
						mountains.getHeight()*5);
				
				for(int j = 0; j < 2; j ++){
					sb.draw(sky,
						(Gdx.graphics.getWidth() - sky.getWidth()*5)/2 - getCamera().position.x/bgFactor + i*sky.getWidth()*5,
						(Gdx.graphics.getHeight() - mountains.getHeight()*5-sky.getHeight()*5)/2 + mountains.getHeight()*5 - getCamera().position.y/bgFactor + j*sky.getHeight()*5,
						sky.getWidth()*5,
						sky.getHeight()*5);
				}
			}


//						sb.setProjectionMatrix(getCamera().combined);
//			int numH = (int) (map.getProperties().get("width", Integer.class) / (mountains.getWidth()/bgFactor));
//			int numV = (int) (map.getProperties().get("height", Integer.class) / (sky.getHeight() / bgFactor));
//			for(int i = 0; i < numH+1; i ++){
//				sb.draw(mountains, i*(mountains.getWidth()/bgFactor), 0, mountains.getWidth()/bgFactor, mountains.getHeight()/bgFactor);
//			}
//			for(int i = 0; i < numH+1; i ++){
//				for(int j = 1; j < numV + 2; j ++){
//					sb.draw(sky, i*(sky.getWidth()/bgFactor), j*(sky.getHeight()/bgFactor), sky.getWidth()/bgFactor, sky.getHeight()/bgFactor);
//				}
//			}
			
			
			otm.setView(getCamera());
			sb.end();
			if((System.nanoTime() - a)/1000000.0 > 30) slow("Background ", (System.nanoTime() - a)/1000000.0); a = System.nanoTime();

	
//			FrameBufferManager.begin(fboA);
//			{
//				Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//				sb.setColor(Color.BLACK);
//				otm.render();
//				sb.setColor(Color.WHITE);
//			}
//			FrameBufferManager.end();
//			
//			sb.begin();
//	
//			FrameBufferManager.begin(fboB);
//		
//			sb.setShader(shader);
//			shader.setUniformf("dir", 1.0f, 0.0f);
//			shader.setUniformf("radius", 5f);
//			sb.setProjectionMatrix(normalProjection);
//			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//			sb.draw(fboA.getColorBufferTexture(),  0.0f, 0.0f);
//			sb.flush();
//			sb.setShader(null);
//			FrameBufferManager.end();
//	
//	
//			
//			sb.setShader(shader);
//			shader.setUniformf("dir", 0.0f, 1.0f);
//			shader.setUniformf("radius", 5f);
//			sb.setProjectionMatrix(normalProjection);
//			
//			sb.draw(fboB.getColorBufferTexture(), 0, -10, fboB.getColorBufferTexture().getWidth() + 20, fboB.getColorBufferTexture().getHeight() + 10);
//			sb.setProjectionMatrix(getCamera().combined);
//			sb.setShader(null);
//			
//			if((System.nanoTime() - a)/1000000.0 > 1) System.out.println("Platform shadow " + (System.nanoTime() - a)/1000000.0); a = System.nanoTime();

		}
	//	sb.end();
		sb.begin();
		//otm.render();
		sb.setProjectionMatrix(getCamera().combined);
		int mapWidth = map.getProperties().get("width", Integer.class);
		int mapHeight = map.getProperties().get("height", Integer.class);
		TiledMapTileLayer tiles = ((TiledMapTileLayer)map.getLayers().get("Tiles"));
		TiledMapTileLayer background = ((TiledMapTileLayer)map.getLayers().get("Background"));
		for(int i = 0; i < mapWidth; i ++){
			for(int j = 0; j < mapHeight; j ++){
				
				//tiles principais
				Cell c = tiles.getCell(i, j);
				if(c != null &&
						getCamera().frustum.pointInFrustum(i*c.getTile().getTextureRegion().getRegionWidth() * Recursive.SCALE_MULT * Recursive.TILE_SCALE, j*c.getTile().getTextureRegion().getRegionHeight() * Recursive.SCALE_MULT* Recursive.TILE_SCALE, 0)){
					TextureRegion tr = c.getTile().getTextureRegion();
					sb.draw(
						tr,
						i*tr.getRegionWidth() * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						j*tr.getRegionHeight() * Recursive.SCALE_MULT* Recursive.TILE_SCALE,
						tr.getRegionWidth() * Recursive.SCALE_MULT* Recursive.TILE_SCALE,
						tr.getRegionHeight() * Recursive.SCALE_MULT* Recursive.TILE_SCALE);
				}
				//tiles plataformas
				c = background.getCell(i, j);
				if(c != null &&
						getCamera().frustum.pointInFrustum(i*c.getTile().getTextureRegion().getRegionWidth() * Recursive.SCALE_MULT * Recursive.TILE_SCALE, j*c.getTile().getTextureRegion().getRegionHeight() * Recursive.SCALE_MULT* Recursive.TILE_SCALE, 0)){
					TextureRegion tr = c.getTile().getTextureRegion();

					sb.draw(
							tr,
						i*tr.getRegionWidth() * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						j*tr.getRegionHeight() * Recursive.SCALE_MULT* Recursive.TILE_SCALE,
						tr.getRegionWidth() * Recursive.SCALE_MULT* Recursive.TILE_SCALE,
						tr.getRegionHeight() * Recursive.SCALE_MULT* Recursive.TILE_SCALE);
				}
				
			}
		}

		sb.end();
		if((System.nanoTime() - a)/1000000.0 > 30) slow("Platform ", (System.nanoTime() - a)/1000000.0); a = System.nanoTime();

		sb.begin();
			if(boss != null){
				boss.render(sb);
			}

			for(Sprite s : sprites){
				s.draw(sb);
			}
			sb.end();
		srenderer.setProjectionMatrix(getCamera().combined);

		
		if((System.nanoTime() - a)/1000000.0 > 30) slow("Boss and sprites ", (System.nanoTime() - a)/1000000.0); a = System.nanoTime();


		sb.begin();
		if(Controllers.getControllers().size > 0){
			if(Controllers.getControllers().get(0).getButton(InputConfig.J_THROW)  && Player.holding != null){
				srenderer.begin(ShapeType.Line);
				srenderer.setColor(1, 1, 0, 1);
				srenderer.line(
						getPlayer().getPosition().x, getPlayer().getPosition().y + 0.5f, 
						(float)(getPlayer().getPosition().x + getPlayer().side()*Math.sin(Math.toRadians((Math.sin(throwforce/10.0)*angleRange + startAngle)))*1), 
						(float)(getPlayer().getPosition().y + 0.5f + Math.cos(Math.toRadians((Math.sin(throwforce/10.0)*angleRange + startAngle)))*1));
				srenderer.end();

			}
			
		}

		if(Gdx.input.isKeyPressed(InputConfig.THROW) && Player.holding != null){
			srenderer.begin(ShapeType.Line);
			srenderer.setColor(1, 1, 0, 1);
			srenderer.line(
					getPlayer().getPosition().x, getPlayer().getPosition().y + 0.5f, 
					(float)(getPlayer().getPosition().x + getPlayer().side()*Math.sin(Math.toRadians((Math.sin(throwforce/10.0)*angleRange + startAngle)))*1), 
					(float)(getPlayer().getPosition().y + 0.5f + Math.cos(Math.toRadians((Math.sin(throwforce/10.0)*angleRange + startAngle)))*1));
			srenderer.end();

		}
		
		if((System.nanoTime() - a)/1000000.0 > 30) slow("Aim line ", (System.nanoTime() - a)/1000000.0); a = System.nanoTime();


		guiCam.position.set(getCamera().position);
		guiCam.update();
		sb.end();
		
		{
			sb.begin();
			drawTextures();
			sb.end();
			sb.begin();
			if(Player.holding != null){
				if(Player.holding.equals("BOMB")){
				sb.draw(rec.getTexture(Assets.BOMB), 
						getPlayer().getPosition().x - 0.4f,
						getPlayer().getPosition().y + 0.4f,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
				}
				else if(Player.holding.equals("BOX")){
					sb.draw(rec.getTexture(Assets.BOX), 
							getPlayer().getPosition().x - 0.4f,
							getPlayer().getPosition().y + 0.4f,
							16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
				}
	
			}
			if(boss != null){
				sb.setProjectionMatrix(normalProjection);
			sb.draw(rec.getTexture(Assets.BOSS_LIFE),
					370/1920f * Gdx.graphics.getWidth(),
					(1080 - 975)/1080f * Gdx.graphics.getHeight() - 84/1080f * Gdx.graphics.getHeight(),
					(float)((boss.life/300.0) * (1023/1920f * Gdx.graphics.getWidth())),
					84/1080f * Gdx.graphics.getHeight());
			}
				
		}
		
		if((System.nanoTime() - a)/1000000.0 > 30) slow("Holds and boss life ", (System.nanoTime() - a)/1000000.0); a = System.nanoTime();

		
		pe.draw(sb, Gdx.graphics.getDeltaTime());
				
		sb.end();
		getPlayer().render(sb);
		
		if((System.nanoTime() - a)/1000000.0 > 30) slow("Player ", (System.nanoTime() - a)/1000000.0); a = System.nanoTime();

		//b2dr.render(world, getCamera().combined);
	}
	public void drawTextures(){

		
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
		
		// Get current frame of animation for the current stateTime
		TextureRegion currentFrame = portal.getKeyFrame(stateTime, true);

		
		world.getBodies(bodies);
		for(Body b : bodies){
			
			if(b.getUserData() != null && b.getUserData().equals("ITM=Computer")){
				sb.draw(rec.getTexture(Assets.COMPUTER), 
						b.getWorldCenter().x,
						b.getWorldCenter().y,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 32 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}

			if(b.getUserData() != null && b.getUserData().toString().startsWith("SIGN")){
				sb.draw(rec.getTexture(Assets.SIGN), 
						b.getWorldCenter().x,
						b.getWorldCenter().y,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
			if(b.getUserData() != null && b.getUserData().toString().startsWith("ITM=Switch")){
				boolean on = Boolean.valueOf(b.getUserData().toString().split("=")[3]);
					if(on){
						sb.draw(rec.getTexture(Assets.SWITCH_ON), 
								b.getWorldCenter().x,
								b.getWorldCenter().y,
								16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
					}
					else{
						sb.draw(rec.getTexture(Assets.SWITCH_OFF), 
								b.getWorldCenter().x,
								b.getWorldCenter().y,
								16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
					}
				
			}
			if(b.getUserData() != null && b.getUserData().toString().startsWith("Box")){
				sb.draw(rec.getTexture(Assets.BOX), 
						b.getWorldCenter().x,
						b.getWorldCenter().y,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
			if(b.getUserData() != null && b.getUserData().toString().startsWith("OWD=Right")){
				sb.draw(rec.getTexture(Assets.FX_HOR),
						b.getWorldCenter().x,
						b.getWorldCenter().y,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 64 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
			if(b.getUserData() != null && b.getUserData().toString().startsWith("OWD=Left")){
				sb.draw(rec.getTexture(Assets.FX_HOR),
						b.getWorldCenter().x + 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						b.getWorldCenter().y,
						-16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 64 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
			if(b.getUserData() != null && b.getUserData().equals("Rock")){
			sb.draw(rec.getTexture(Assets.ROCKWALL), 
					b.getWorldCenter().x,
					b.getWorldCenter().y,
					rockSize.x * Recursive.SCALE_MULT * Recursive.TILE_SCALE, rockSize.y * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
			if(b.getUserData() != null && b.getUserData().toString().startsWith("ITM=Teleporter")){
				sb.draw(currentFrame,
						b.getWorldCenter().x,
						b.getWorldCenter().y,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
			if(b.getUserData() != null && b.getUserData().toString().startsWith("Door")){
					sb.draw(rec.getTexture(Assets.DOOR),
							b.getWorldCenter().x,
							b.getWorldCenter().y,
							doorSize.x * Recursive.SCALE_MULT * Recursive.TILE_SCALE, doorSize.y * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
					sb.setProjectionMatrix(guiCam.combined);
					float x = guiCam.position.x-(getCamera().position.x - (b.getWorldCenter().x + 0.2f))*ratew;
					float y = guiCam.position.y-(getCamera().position.y - (b.getWorldCenter().y + 3))*rateh;
					font.draw(sb, b.getUserData().toString().split("Door=")[1], x, y);
					sb.setProjectionMatrix(getCamera().combined);
				}
			
			if(b.getUserData() != null && b.getUserData().toString().split("=").length > 1 && b.getUserData().toString().split("=")[1].equals("Key")){
				sb.draw(rec.getTexture(Assets.KEY), 
						b.getWorldCenter().x,
						b.getWorldCenter().y,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
				}
			if(b.getUserData() != null && (b.getUserData().equals("ITM=Bomb"))){
				sb.draw(rec.getTexture(Assets.BOMB), 
						b.getWorldCenter().x,
						b.getWorldCenter().y,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
				}
			if(b.getUserData() != null && (b.getUserData().equals("PROJBOMB"))){
				sb.draw(rec.getTexture(Assets.BOMB), 
						b.getWorldCenter().x - 0.4f,
						b.getWorldCenter().y - 0.4f,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
				}
			if(boss != null){
			if(b.equals(boss.body)){
				sb.draw(rec.getTexture(Assets.BOSS_TEX),
						b.getWorldCenter().x - 25*0.2f,
						b.getWorldCenter().y - 25*0.2f,
						25*0.2f * 2,
						25*0.2f * 2);
			}
			}
			if(b.getUserData() != null && b.getUserData().equals("BossGravel")){
				sb.draw(rec.getTexture(Assets.GRAVEL), 
						b.getWorldCenter().x - 4 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						b.getWorldCenter().y - 4 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}
			if(b.getUserData() != null && b.getUserData().equals("BossSpike")){
				sb.draw(rec.getTexture(Assets.SPIKE), 
						b.getWorldCenter().x - 8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						b.getWorldCenter().y - 16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE,
						16 * Recursive.SCALE_MULT * Recursive.TILE_SCALE, 32 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			}

		}

		for(int i = spitters.size() - 1; i >= 0; i --){
			spitters.get(i).render(sb);
		}
		for(int i = enemies.size() - 1; i>= 0; i --){
			enemies.get(i).render(sb);
		}

	}
	
	public int getCurrentLevel(){
		if(currentMap.equals("level1")){
			return 0;
		}
		if(currentMap.equals("level2")){
			return 1;
		}
		if(currentMap.equals("level3")){
			return 2;
		}
		return 0;
	}
	Vector2 temp = Vector2.Zero.cpy();
	Vector3 temp3 = Vector3.Zero.cpy();
	public void canReload(String level){
		currentMap = level;
		if(map != null) map.dispose();
		if(otm != null) otm.dispose();
		map = new TmxMapLoader().load("maps/"+level+".tmx");
		otm = new OrthogonalTiledMapRenderer(map, Recursive.SCALE_MULT * Recursive.TILE_SCALE, sb);//0.02222
		
		MapObjects objects = map.getLayers().get("Player").getObjects();
		MapObject pl = objects.get("Player");
		temp.set((Float)pl.getProperties().get("x"), (Float)pl.getProperties().get("y"));
		initPos = temp;

		temp3.set(initPos.cpy().scl(Recursive.SCALE_MULT), 0);
		getCamera().position.set(temp3);
		initWorld();
		getCamera().position.set(player.getPosition(), 0);

		loadObjectsByParser();
		loadEnemies();
		if(level.equals("level3")){
			loadFirstBoss();
		}
		else{
			boss = null;
		}
	//	loadTiledMapBodies();
	}
	public FirstBoss boss;
	public void loadFirstBoss(){
		
		BodyDef def = new BodyDef();
		def.type = BodyType.DynamicBody;
		def.position.set(27, 9);
		def.fixedRotation = true;
		
		Body boss = world.createBody(def);
		
		CircleShape c = new CircleShape();
		c.setRadius(25*0.2f);
		Fixture f = boss.createFixture(c, 1);
		f.setFriction(0.03f);
		
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(10*0.2f, 5*0.2f, new Vector2(0, -25*0.2f), 0);
		Fixture f2 = boss.createFixture(ps, 0);
		f2.setUserData("BOSS_FEET");
		f2.setSensor(true);
		
		
		this.boss = new FirstBoss(boss);

	}
	
	public void loadImages(){
		sprites.clear();
		if(map.getLayers().get("Images") != null){
		MapObjects objects = map.getLayers().get("Images").getObjects();
		
			for(MapObject object : objects){
				Sprite s = new Sprite(new Texture("images/"+object.getProperties().get("type")+".png"));
				s.setPosition(object.getProperties().get("x", Float.class)*Recursive.SCALE_MULT, object.getProperties().get("y", Float.class)*Recursive.SCALE_MULT);
				s.setSize(object.getProperties().get("width", Float.class)*Recursive.SCALE_MULT, object.getProperties().get("height", Float.class)*Recursive.SCALE_MULT);
				sprites.add(s);
			}
		
		}
	}

	public void loadEnemies(){
		if(map.getLayers().get("Enemies") != null){
			MapObjects objects = map.getLayers().get("Enemies").getObjects();
			//load enemies
			for(MapObject object : objects){
				if(object.getProperties().get("type").equals("movable")){
					
					float x = object.getProperties().get("x", Float.class);
					float y = object.getProperties().get("y", Float.class);
					int ID = object.getProperties().get("id", Integer.class);
					
					BodyDef def = new BodyDef();
					def.type = BodyDef.BodyType.DynamicBody;
					def.position.set(new Vector2(x*Recursive.SCALE_MULT * Recursive.TILE_SCALE, y*Recursive.SCALE_MULT * Recursive.TILE_SCALE));
					
					Body enemy = world.createBody(def);
					
					CircleShape c = new CircleShape();
					c.setRadius(object.getProperties().get("width", Float.class)/2*Recursive.SCALE_MULT * Recursive.TILE_SCALE);
					Fixture f = enemy.createFixture(c, 5);
					c.dispose();
					Enemy en = new Enemy(enemy, ID, rec);
					
					f.setUserData(en);
					enemy.setUserData(en);
					enemies.add(en);
					
				}
				
			}
		}
	}
	
	
	public void resetLevel(boolean first) {
		canReload(currentMap);
		if(first)
		firstCreate();
		//caso ele tenha entrado na fase já com uma bomba -> deixa a bomba
		
		//caso ele tenha entrado de maos vazias -> caso ele tenha uma bomba na hora de resetar ->
		// -> caso a bomba tenha sido adquirida em outra camada -> deixa a bomba
		//if(Player.holding != null && Player.layerHoldOrigin == rec.game.countBackward()){
			Player.holding = null;
		//}

		
//		if(rec.child != null){
//			if(rec.child.levelCreated >= rec.currentLevel()){
//				rec.child = null;
//			}
//		}
	}
	
	public Enemy getEnemyById(int id){
		for(Enemy enemy : enemies){
			if(enemy.ID == id){
				return enemy;
			}
		}
		return null;
	}
	
	public void initWorld(){
		if(b2dr != null) b2dr.dispose();
		b2dr = new Box2DDebugRenderer();
		if(world != null){
			bodies.clear();
			world.getBodies(bodies);
			for(Body b : bodies){
				if(!Recursive.forRemoval.contains(b, false)){
					Recursive.forRemoval.add(b);
				}
			}
		}
		else
		world = new World(new Vector2(0, -35f), false);
		
		//Player
		BodyDef def = new BodyDef();
		
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(initPos.x*Recursive.SCALE_MULT*Recursive.TILE_SCALE, initPos.y*Recursive.SCALE_MULT*Recursive.TILE_SCALE);
		def.fixedRotation = true;		
		
		Body player = world.createBody(def);
		this.setPlayer(new Player(player, rec, this));
		
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void loadTiles(){
//		TiledMapTileLayer collisionLayer = (TiledMapTileLayer) map.getLayers().get("Tiles");
//		//Load blocks
//		  for (int x = 0; x < collisionLayer.getWidth(); x++) {
//		        for (int y = 0; y < collisionLayer.getHeight(); y++) {
//		            Cell cell = collisionLayer.getCell(x, y);
//
//		            if (cell != null && cell.getTile() != null) {
//		               // float tileX = collisionLayer.getProperties().containsKey("x");
//		               // float tileY = cell.getTile().getProperties().get("y", Integer.class);
//
//		                BodyDef bodyDef = new BodyDef();
//		                bodyDef.position.set(x + 0.5f, y + 0.5f);
//
//		                PolygonShape shape = new PolygonShape();
//		                shape.setAsBox(0.5f, 0.5f);
//
//		                Body body = world.createBody(bodyDef);
//		                Fixture f = body.createFixture(shape, 0f);
//		                f.setUserData("BLOCK");
//		                body.setUserData("BLOCK");
//
//		                shape.dispose();
//		            }
//		        }
//		    }
	}
	Array<Integer> lista = new Array<Integer>();
	public Array<Integer> getSpittersByID(int id){
		lista.clear();
		for(int i = 0; i < spitters.size(); i ++){
			if(spitters.get(i).getId() == id){
				lista.add(i);
			}
		}
		return lista;
	}
	
	Array<Body> allBodies = new Array<Body>();
	public void loadObjectsByParser(){
		loadTiles();
		
		Box2DMapObjectParser parser = new Box2DMapObjectParser(Recursive.SCALE_MULT * Recursive.TILE_SCALE);
		parser.load(world, map.getLayers().get("Items"));
		parser.load(world, map.getLayers().get("Objects"));
		parser.load(world, map.getLayers().get("Walls"));
		if(map.getLayers().get("Blocks") != null)
		parser.load(world, map.getLayers().get("Blocks"));
		spitters.clear();
		if(map.getLayers().get("Spitter") != null){
			//parser.load(world, map.getLayers().get("Spitter"));
			MapObjects spitters = map.getLayers().get("Spitter").getObjects();
			for(int i = 0; i < spitters.getCount(); i ++){
				
				MapObject ob = spitters.get(i);
				float x = ob.getProperties().get("x", Float.class);
				float y = ob.getProperties().get("y", Float.class);
				boolean on = Boolean.valueOf(ob.getProperties().get("on", String.class));
				int id = Integer.parseInt(ob.getProperties().get("code", String.class));
				temp.set(x * Recursive.SCALE_MULT * Recursive.TILE_SCALE, y * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
				this.spitters.add(new Spitter(temp.cpy(), rec, world, on, id));
			}
		}
		
	}

	public void throwHolder(){
		if(Player.holding.equals("BOMB")){
			BodyDef def = new BodyDef();
			def.type = BodyDef.BodyType.DynamicBody;
			def.position.set(getPlayer().getPosition().cpy().add(0, 0.5f));
			def.fixedRotation = true;
			
			Body bomb = world.createBody(def);
			CircleShape s = new CircleShape();
			s.setRadius(6/Recursive.SCALE_DIV);
			Fixture f = bomb.createFixture(s, 0);
			s.dispose();
			f.setUserData("PROJBOMB");
			bomb.setUserData("PROJBOMB");
			float mag = 10;
			temp.set(getPlayer().side()*
					(float)Math.sin(Math.toRadians(Math.sin(throwforce/10.0)*angleRange + startAngle))*mag,
					(float)Math.cos(Math.toRadians(Math.sin(throwforce/10.0)*angleRange + startAngle))*mag);
			bomb.setLinearVelocity(temp);
		}
		else if(Player.holding.equals("BOX")){
			System.out.println("Throwing box at layer " + rec.game.countBackward());

			BodyDef def = new BodyDef();
			def.type = BodyDef.BodyType.DynamicBody;
			def.position.set(getPlayer().getPosition().cpy().add(0, 0.5f));
			def.fixedRotation = true;
			
			Body box = world.createBody(def);
			PolygonShape s = new PolygonShape();
			temp.set(8*Recursive.TILE_SCALE*Recursive.SCALE_MULT, 8*Recursive.TILE_SCALE*Recursive.SCALE_MULT);
			s.setAsBox(8*Recursive.TILE_SCALE*Recursive.SCALE_MULT, 8*Recursive.TILE_SCALE*Recursive.SCALE_MULT, temp, 0);
			Fixture f = box.createFixture(s, 0);
			
			f.setUserData("Box=" + rec.game.countBackward());
			box.setUserData("Box=" + rec.game.countBackward());
			s.dispose();
			float mag = 10;
			temp.set(getPlayer().side()*
					(float)Math.sin(Math.toRadians(Math.sin(throwforce/10.0)*angleRange + startAngle))*mag,
					(float)Math.cos(Math.toRadians(Math.sin(throwforce/10.0)*angleRange + startAngle))*mag);
			box.setLinearVelocity(temp);
		}
	}
	
	public void releaseItem(){
		if(Player.holding != null){
		if(Player.holding.equals("BOMB")){
			BodyDef def = new BodyDef();
			def.type = BodyDef.BodyType.StaticBody;
			def.position.set(getPlayer().getPosition().cpy());
			def.fixedRotation = true;
			
			Body bomb = world.createBody(def);
			CircleShape s = new CircleShape();
			s.setRadius(8 * Recursive.SCALE_MULT * Recursive.TILE_SCALE);
			temp.set(8*Recursive.TILE_SCALE*Recursive.SCALE_MULT, 8*Recursive.TILE_SCALE*Recursive.SCALE_MULT);
			s.setPosition(temp);
			Fixture f = bomb.createFixture(s, 0);
			s.dispose();
			f.setSensor(true);
			f.setUserData("ITM=Bomb");
			bomb.setUserData("ITM=Bomb");
			
			Player.holding = null;
			Player.layerHoldOrigin = -1;
		}
		else if(Player.holding.equals("BOX")){
			System.out.println("Throwing box at layer " + rec.game.countBackward());

			BodyDef def = new BodyDef();
			def.type = BodyDef.BodyType.DynamicBody;
			def.position.set(getPlayer().getPosition().cpy().add(0, 0.5f));
			def.fixedRotation = true;
			
			Body box = world.createBody(def);
			PolygonShape s = new PolygonShape();
			temp.set(8*Recursive.TILE_SCALE*Recursive.SCALE_MULT, 8*Recursive.TILE_SCALE*Recursive.SCALE_MULT);
			s.setAsBox(8*Recursive.TILE_SCALE*Recursive.SCALE_MULT, 8*Recursive.TILE_SCALE*Recursive.SCALE_MULT, temp, 0);
			Fixture f = box.createFixture(s, 0);
			
			f.setUserData("Box=" + rec.game.countBackward());
			box.setUserData("Box=" + rec.game.countBackward());
			s.dispose();
			
			Player.holding = null;
			Player.layerHoldOrigin = -1;
		}
		}
		
	}


	public void dispose() {
		getPlayer().dispose();
		map.dispose();
		otm.dispose();
		if(boss != null){
			boss.dispose();
		}
	}
	double throwforce = Math.toRadians(270)*10;
	public boolean keyDown(int keycode) {
		if(keycode == Keys.NUM_1){
			
		}
		return false;
	}

	public boolean keyUp(int keycode) {
		if(keycode == InputConfig.THROW){
			if(Player.holding != null){
				if(rec.game.current == rec){
					throwHolder();
					Player.holding = null;
					Player.layerHoldOrigin = -1;
				}
			}
			//else{
			//	throwRock();
			//}
			throwforce = Math.toRadians(270)*10;
			return false;
		}
		return false;
	}

	public boolean keyTyped(char character) {
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void connected(Controller controller) {
		
	}

	@Override
	public void disconnected(Controller controller) {
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		getPlayer().buttonDown(controller, buttonCode);
		if(buttonCode == InputConfig.J_ESC){
			rec.changeState(0);
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		if(buttonCode == InputConfig.J_THROW){
			if(Player.holding != null){
				if(rec.game.current == rec){
					throwHolder();
					Player.holding = null;
					Player.layerHoldOrigin = -1;
				}
			}
			//else{
			//	throwRock();
			//}
			throwforce = Math.toRadians(270)*10;
		}
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		getPlayer().axisMoved(controller, axisCode, value);
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		return false;
	}




}
