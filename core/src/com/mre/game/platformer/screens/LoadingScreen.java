package com.mre.game.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mre.game.platformer.PlatformerGame;

public class LoadingScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 640;
    private static final float WORLD_HEIGHT = 480;
    private static final float PROGRESS_BAR_WIDTH = 100;
    private static final float PROGRESS_BAR_HEIGHT = 25;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera cam;
    private float progress = 0;
    private final PlatformerGame game;

    public LoadingScreen() {
        this.game = (PlatformerGame) Gdx.app.getApplicationListener();
    }

    @Override
    public void show() {
        cam = new OrthographicCamera();
        cam.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, cam);
        shapeRenderer = new ShapeRenderer();
        /*
        game.getManager().load("acorn.png", Texture.class);
        game.getManager().load("floor.png", Texture.class);
        game.getManager().load("pete.png", Texture.class);
        */
        game.getManager().load("level1.tmx", TiledMap.class);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.getManager().update()) {
            game.setScreen(new GameScreen());
            dispose();
        } else {
            shapeRenderer.setProjectionMatrix(cam.projection);
            shapeRenderer.setTransformMatrix(cam.view);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect((viewport.getWorldWidth() - PROGRESS_BAR_WIDTH) * 0.5f, (viewport.getWorldHeight() - PROGRESS_BAR_HEIGHT) * 0.5f,
                    game.getManager().getProgress() * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
            shapeRenderer.end();
        }
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
