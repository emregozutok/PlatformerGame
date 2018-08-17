package com.mre.game.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mre.game.platformer.PlatformerGame;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;

    private final PlatformerGame game;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;

    public GameScreen() {
        this.game = (PlatformerGame) Gdx.app.getApplicationListener();
        this.batch = game.getBatch();
    }

    @Override
    public void show() {
        cam = new OrthographicCamera();
        cam.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, cam);
        shapeRenderer = new ShapeRenderer();
        map = game.getManager().get("level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
        mapRenderer.setView(cam);
    }

    @Override
    public void render(float dt) {
        update(dt);
        clearScreen();
        draw();
        drawDebug();
    }

    private void update(float dt) {
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b, Color.CYAN.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.setProjectionMatrix(cam.projection);
        batch.setTransformMatrix(cam.view);
        mapRenderer.render();
        batch.begin();
        batch.end();
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(cam.projection);
        shapeRenderer.setTransformMatrix(cam.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
