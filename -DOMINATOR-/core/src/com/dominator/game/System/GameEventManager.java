package com.dominator.game.System;

/**
 * Created by Choujaa Wassil on 22/03/2017.
 */
public class GameEventManager {

    private static GameEventManager eventManger;

    public static GameEventManager instance(){
        if(eventManger == null){
            eventManger = new GameEventManager();
        }
        return eventManger;
    }

    public void update(){

    }
}
