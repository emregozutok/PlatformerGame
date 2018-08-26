package com.mre.game.platformer.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.mre.game.platformer.util.Constants;

public class WorldRenderer implements Disposable {
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private WorldController worldController;

    public WorldRenderer(WorldController worldController) {
        this.worldController = worldController;
        init();
    }

    private void init() {
        batch = new SpriteBatch();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
    }

    public void render() {
        renderTestObjects();
    }

    private void renderTestObjects() {
        worldController.camHelper.applyTo(cam);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        for(Sprite sprite : worldController.testSprites) {
            sprite.draw(batch);
        }
        batch.end();
    }

    public void resize(int width, int height) {
        cam.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        cam.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
