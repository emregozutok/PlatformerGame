package com.mre.game.platformer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.mre.game.platformer.game.Assets;
import com.mre.game.platformer.screens.MenuScreen;

public class PlatformerGame extends Game {

    @Override
    public void create() {
        // Set Libgdx log level 
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // Load assets
        Assets.instance.init(new AssetManager());
        // Start game at menu screen
        setScreen(new MenuScreen());
    }

}
