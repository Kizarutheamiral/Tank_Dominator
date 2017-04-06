package com.dominator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dominator.game.System.GameStateManager;
import com.dominator.game.UI.Menu;

public class Dominator extends Game {

	@Override
	public void create() {
		// Loading
		initInstances();
		// Menu
		GameStateManager.instance().JumpToMenu();

	}

	private void initInstances() {
        GameStateManager.instance().setup(this);
	}
}
