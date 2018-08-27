package com.mre.game.platformer.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.mre.game.platformer.game.Assets;

public abstract class AbstractGameScreen implements Screen {
    protected Game game;

    public AbstractGameScreen () {
        this.game = (Game) Gdx.app.getApplicationListener();
    }

    public abstract void render (float deltaTime);
    public abstract void resize (int width, int height);
    public abstract void show ();
    public abstract void hide ();
    public abstract void pause ();

    public void resume () {
        Assets.instance.init(new AssetManager());
    }

    public void dispose () {
        Assets.instance.dispose();
    }
}
