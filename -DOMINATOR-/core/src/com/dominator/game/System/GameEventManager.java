package com.dominator.game.System;

import com.badlogic.gdx.utils.Array;
import com.dominator.game.Entities.Tank;
import com.dominator.game.Module.Pathfinder;

/**
 * Created by Choujaa Wassil on 22/03/2017.
 */
public class GameEventManager {

    private static GameEventManager eventManger;

    private Array<Tank> tanks = new Array<Tank>();
    
    public static GameEventManager instance(){
        if(eventManger == null){
            eventManger = new GameEventManager();
        }
        return eventManger;
    }

    public void touchEvent(float x, float y){

    }

    public void update(){

    }
}
