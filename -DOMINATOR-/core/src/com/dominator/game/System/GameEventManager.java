package com.dominator.game.System;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.dominator.game.Entities.*;
import com.dominator.game.Module.Pathfinder;

/**
 * Created by Choujaa Wassil on 22/03/2017.
 *
 */
public class GameEventManager {

    private static GameEventManager eventManger;

    public Array<Tank> tanks = new Array<Tank>();

    public Array<Tank> selected = new Array<Tank>();

    private boolean selection = false;

    public Engine engine;

    public Map map;

    public Pathfinder finder;
    private boolean debug = true;

    public void setup(){

        engine = new Engine();

        map = new Map().loadMap(engine.getWorld());
        finder = new Pathfinder(map.quadTree);
        tanks.add(new Abrams(10,10,engine.getWorld(),map));

        tanks.add(new M12(70,10,engine.getWorld(),map));
        tanks.add(new Abrams(40,30,engine.getWorld(),map));
        tanks.add(new Abrams(120,100,engine.getWorld(),map));
        tanks.add(new M12(300,100,engine.getWorld(),map));
        tanks.add(new Abrams(30,70,engine.getWorld(),map));

    }

    public static GameEventManager instance(){
        if(eventManger == null){
            eventManger = new GameEventManager();
        }
        return eventManger;
    }

    // true
    public void touchEvent(float x, float y, boolean selectButton){


        boolean found = false;

        for(Tank tank:tanks){
            if(tank.contain(x,y)){
                tank.setSelected(true);
                for(Tank t : tanks){
                    if(tank != t){
                        t.setSelected(false);
                    }
                }
                selected.clear();
                selected.add(tank);
                found=true;
                break;
            }
        }

        if(!found && selectButton && selected.size!=0){

            Tank tank = selected.first();

            for (Tank others : selected){
                if(others!=tank){
                    others.setLeader(tank);
                }
            }


            tank.setPath(finder.AstarPathFrom(tank.getX(),tank.getY(),x,y));
            // find the medium near tank to the target
            for (Tank others : selected){
                if(others!=tank){
                    others.setLeader(tank);
                }
            }
            // second for: apply setLeader to other tanks
        }
    }

    public void rectSelectionEvent(float x, float y, float width, float height){

        selected.clear();

        long time =  System.nanoTime();

        for(Tank tank:tanks){
            if(tank.intersect(x,y,width,height)){
                tank.setSelected(true);
                selected.add(tank);
            }
            else {
                tank.setSelected(false);
                selected.removeValue(tank,false);
            }
        }
        long nowtime = System.nanoTime();

        //System.out.println((nowtime-time)/10E9);
    }

    public void update(float delta, OrthographicCamera camera, Box2DDebugRenderer render){
        long time =  System.nanoTime();

        engine.tick(delta, camera, render);

        for ( Tank tank : tanks) {
            tank.update();
        }
        long nowtime = System.nanoTime();
    }


}
