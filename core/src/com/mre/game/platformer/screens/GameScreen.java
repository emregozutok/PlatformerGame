package com.mre.game.platformer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mre.game.platformer.PlatformerGame;
import com.mre.game.platformer.entities.Acorn;
import com.mre.game.platformer.entities.Player;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 240;
    private static final float CELL_SIZE = 16;

    private final PlatformerGame game;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;
    private Player player;
    private Array<Acorn> acorns = new Array<Acorn>();

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
        player = new Player(game.getManager().get("pete.png", Texture.class), game.getManager().get("jump.wav", Sound.class));
        populateAcorns();
        game.getManager().get("peteTheme.mp3", Music.class).setLooping(true);
        game.getManager().get("peteTheme.mp3", Music.class).play();
    }

    @Override
    public void render(float dt) {
        update(dt);
        clearScreen();
        draw();
        drawDebug();
    }

    private void update(float dt) {
        player.update(dt);
        stopPlayerLeavingTheScreen();
        handlePeteCollision();
        handlePeteCollisionWithAcorn();
        updateCameraX();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.TEAL.r, Color.TEAL.g, Color.TEAL.b, Color.TEAL.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw() {
        batch.setProjectionMatrix(cam.projection);
        batch.setTransformMatrix(cam.view);
        mapRenderer.render();
        batch.begin();
        for (Acorn a : acorns) {
            a.draw(batch);
        }
        player.draw(batch);
        batch.end();
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(cam.projection);
        shapeRenderer.setTransformMatrix(cam.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        player.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }

    private void stopPlayerLeavingTheScreen() {
        if (player.getY() < 0) {
            player.setPosition(player.getX(), 0);
            player.landed();
        }
        if (player.getX() < 0) {
            player.setPosition(0, player.getY());
        }
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) map.getLayers().get("Ground");
        float levelWidth =  tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
        if (player.getX() + Player.WIDTH > levelWidth) {
            player.setPosition(levelWidth - player.WIDTH, player.getY());
        }
    }

    private Array<CollisionCell> whichCellsDoesPeteCover() {
        float x = player.getX();
        float y = player.getY();
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();
        float cellX = x / CELL_SIZE;
        float cellY = y / CELL_SIZE;

        int bottomLeftCellX = MathUtils.floor(cellX);
        int bottomLeftCellY = MathUtils.floor(cellY);

        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);

        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLeftCellX, bottomLeftCellY), bottomLeftCellX, bottomLeftCellY));

        if (cellX % 1 != 0 && cellY % 1 != 0) {
            int topRightCellX = bottomLeftCellX + 1;
            int topRightCellY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topRightCellX, topRightCellY), topRightCellX, topRightCellY));
        }

        if (cellX % 1 != 0) {
            int bottomRightCellX = bottomLeftCellX + 1;
            int bottomRightCellY = bottomLeftCellY;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX, bottomRightCellY), bottomRightCellX, bottomRightCellY));
        }

        if (cellY % 1 != 0) {
            int topLeftCellX = bottomLeftCellX;
            int topLeftCellY = bottomLeftCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topLeftCellX, topLeftCellY), topLeftCellX, topLeftCellY));
        }
        return cellsCovered;
    }

    private class CollisionCell {
        private final TiledMapTileLayer.Cell cell;
        private final int cellX;
        private final int cellY;

        public CollisionCell(TiledMapTileLayer.Cell cell, int cellX, int cellY) {
            this.cell = cell;
            this.cellX = cellX;
            this.cellY = cellY;
        }

        public boolean isEmpty() {
            return cell == null;
        }
    }

    private Array<CollisionCell> filterOutNonTiledCells(Array<CollisionCell> cells) {
        for (Iterator<CollisionCell> iter = cells.iterator(); iter.hasNext(); ) {
            CollisionCell collisionCell = iter.next();
            if (collisionCell.isEmpty()) {
                iter.remove();
            }
        }
        return cells;
    }

    private void handlePeteCollision() {
        Array<CollisionCell> playerCells = whichCellsDoesPeteCover();
        playerCells = filterOutNonTiledCells(playerCells);
        for (CollisionCell cell : playerCells) {
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            Rectangle intersection = new Rectangle();
            Intersector.intersectRectangles(player.getCollisionRectangle(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);
            if (intersection.getHeight() < intersection.getWidth()) {
                player.setPosition(player.getX(), intersection.getY() + intersection.getHeight());
                player.landed();
            } else if (intersection.getWidth() < intersection.getHeight()) {
                if (intersection.getX() == player.getX()) {
                    player.setPosition(intersection.getX() + intersection.getWidth(), player.getY());
                }
                if (intersection.getX() > player.getX()) {
                    player.setPosition(intersection.getX() - player.WIDTH, player.getY());
                }
            }
        }
    }

    private void populateAcorns() {
        MapLayer mapLayer = map.getLayers().get("Collectables");
        for (MapObject mapObject : mapLayer.getObjects()) {
            acorns.add(
                    new Acorn(game.getManager().get("acorn.png", Texture.class),
                            mapObject.getProperties().get("x", Float.class),
                            mapObject.getProperties().get("y", Float.class)
                    )
            );
        }
    }

    private void handlePeteCollisionWithAcorn() {
        for (Iterator<Acorn> iter = acorns.iterator(); iter.hasNext(); ) {
            Acorn acorn = iter.next();
            if (player.getCollisionRectangle().overlaps(acorn.getCollisionRectangle())){
                game.getManager().get("acorn.wav", Sound.class).play();
                iter.remove();
            }
        }
    }

    private void updateCameraX() {
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
        if ( (player.getX() > WORLD_WIDTH / 2f) && (player.getX() < (levelWidth - WORLD_WIDTH / 2f)) ) {
            cam.position.set(player.getX(), cam.position.y, cam.position.z);
            cam.update();
            mapRenderer.setView(cam);
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
