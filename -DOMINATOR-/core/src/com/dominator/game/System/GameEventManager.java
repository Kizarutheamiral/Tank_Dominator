package com.dominator.game.System;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.dominator.game.Entities.Abrams;
import com.dominator.game.Entities.Dynamic_Bodies;
import com.dominator.game.Entities.Map;
import com.dominator.game.Entities.Tank;
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.Quadtree.Func;
import com.dominator.game.Quadtree.Node;
import com.dominator.game.Quadtree.NodeType;
import com.dominator.game.Quadtree.QuadTree;

/**
 * Created by Choujaa Wassil on 22/03/2017.
 */
public class GameEventManager {

    private static GameEventManager eventManger;

    public Array<Tank> tanks = new Array<Tank>();

    public Array<Tank> selected = new Array<Tank>();

    private boolean selection = false;

    public Engine engine;

    public Map map;

    private Pathfinder finder;
    private boolean debug = true;

    public void setup(){
        engine = new Engine();
        map = new Map().loadMap(engine.getWorld());
        finder = new Pathfinder(map.quadTree);
        tanks.add(new Abrams(80,80,engine.getWorld(),map));
    }

    public static GameEventManager instance(){
        if(eventManger == null){
            eventManger = new GameEventManager();
        }
        return eventManger;
    }

    public void touchEvent(float x, float y){

        if (selection){
            for (Tank tank: selected) {
                tank.path = finder.AstarPathFrom(tank.getX(),tank.getY(),x,y);
            }
            selection = false;
        } else {
            for ( Tank t : tanks) {
                if (t.contain(x,y)){
                    selection = true;
                    selected.add(t);
                }
            }
        }


        // if not ??
    }

    public void RectSelectionEvent(float x, float y, float width, float height){

    }

    public void update(float delta, OrthographicCamera camera, Box2DDebugRenderer render){
        engine.tick(delta, camera, render);
        for ( Tank tank : tanks) {
            tank.update();
        }

    }
}
