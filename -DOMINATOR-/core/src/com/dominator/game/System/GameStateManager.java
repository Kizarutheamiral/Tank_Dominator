package com.dominator.game.System;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.dominator.game.Dominator;
import com.dominator.game.Entities.JsonToBody;
import com.dominator.game.UI.Menu;
import com.dominator.game.UI.PlayScreen;

import static com.dominator.game.CONSTANT.*;

/**
 * Created by Choujaa Wassil on 24/03/2017.
 */
public class GameStateManager {

    private GameState state = GameState.MENU;

    private static GameStateManager manager;

    private Dominator dominator;

    public  JsonToBody.RigidBodyModel Abrams;

    public JsonToBody.RigidBodyModel AbramsTourelle;


    public JsonToBody.RigidBodyModel Map;

    public static JsonToBody.RigidBodyModel Tank2;


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

    public void setup(Dominator d){
        dominator=d;
        loadBox2D();
    }

    private void loadBox2D() {
        Box2D.init();

        JsonToBody loader =  new JsonToBody();
        loader.loadFromJSon(Gdx.files.internal("map.json"),scale);
        loader.loadFromJSon(Gdx.files.internal("abrams.json"),abramsScale);
        loader.loadFromJSon(Gdx.files.internal("abrams_tourelle.json"), abramsTourelleScale);

        Map = loader.model.rigidBodies.get("map");
        Abrams = loader.model.rigidBodies.get("abrams");
        AbramsTourelle = loader.model.rigidBodies.get("abrams_tourelle");

    }
}
