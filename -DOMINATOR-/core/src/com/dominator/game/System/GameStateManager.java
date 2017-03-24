package com.dominator.game.System;

import com.dominator.game.Dominator;
import com.dominator.game.UI.Menu;
import com.dominator.game.UI.PlayScreen;

/**
 * Created by Choujaa Wassil on 24/03/2017.
 */
public class GameStateManager {

    private GameState state = GameState.MENU;

    private static GameStateManager manager;

    private Dominator dominator;

    public static GameStateManager instance(){
        if(manager==null){
            manager = new GameStateManager();
        }
        return manager;
    }

    public void launchGame(){
        state = GameState.PLAY;

        dominator.setScreen(new PlayScreen());

    }

    public void  pause(){
        state = GameState.PAUSE;
    }

    public void resume() {

    }

    public void JumpToMenu(){
        dominator.setScreen(new Menu());
    }

    public GameState getState() {
        return state;
    }

    public void setDominator(Dominator d){
       dominator=d;
    }
}
