package com.dominator.game.System;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.dominator.game.Entities.*;
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.Quadtree.Point;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by Choujaa Wassil on 22/03/2017.
 *
 */
public class GameEventManager {

    private static GameEventManager eventManger;

    public Array<Tank> tanks = new Array<Tank>();

    public Array<Tank> selected = new Array<Tank>();

    public Array<Point> Point2Dpool = new Array<Point>();

    public Array<Formation> formations = new Array<Formation>();

    public Point newPoint() {
        return Point2Dpool.size==0 ? new Point() : Point2Dpool.pop();
    }

    public void free(Point v) {
        Point2Dpool.add(v);
    }

    private boolean selection = false;

    public Engine engine;

    public Map map;

    public Pathfinder finder;

    private boolean debug = true;

    public void setup(){

        engine = new Engine().setup();

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
    public void touchEvent(float x, float y, boolean isSelectButton) {

        Iterator<Tank> it;

        boolean found = false;

        it = tanks.iterator();
        Tank tank = null;
        while (it.hasNext() && !found) {
            tank = it.next();
            if (tank.contain(x, y)) {
                found = true;
            }
        }

        if (found) {

            it = tanks.iterator();

            while (it.hasNext()) {
                it.next().setSelected(false);
            }
            selected.clear();
            tank.setSelected(true);
            selected.add(tank);

        }
        else if (!isSelectButton) {
            if (selected.size == 1) {
                Tank slctd = selected.first();
                /// Buf fix: if he is a leader => change formation leader

                for(Formation f : formations){
                    if (f.leader == slctd){
                        f.reset();
                    }
                }

                slctd.getMovement().setupPath(finder.AstarPathFrom(slctd.getPosition(), new Vector2(x,y)));
            }
            else if(selected.size>1){
                formations.add(new Formation(selected,new Vector2(x,y)).setup());
            }
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

        Array.ArrayIterator<Tank> it = new Array.ArrayIterator<Tank>(tanks);

        while (it.hasNext()){
            Tank t = it.next();
            t.update();

        }


        for(Formation f : formations){
            if(f.leader==null) f.setup();
        }

        long nowtime = System.nanoTime();

    }



    private class Formation{

        Array<Tank> formation;

        Vector2 target;

        int size;

        Tank leader;

        public Formation(Array<Tank> f,Vector2 target) {
            this.formation = f;
            this.target = target;
            this.size = f.size;
        }


        public Formation setup() {

            Array.ArrayIterator<Tank> it = new Array.ArrayIterator<Tank>(formation);

            Tank leader = null;

            float MaxHealth=0f;

            while (it.hasNext()){
                Tank t = it.next();
                t.getMovement().setFormation(true);
                if(t.getHealth()>MaxHealth) leader = t;
            }

            this.leader = leader;

            if(leader==null) leader=formation.first();

            leader.getMovement().setupPath(finder.AstarPathFrom(leader.getPosition(), target));

            sortByDist();

            it = new Array.ArrayIterator<Tank>(formation);

            int i = 0;

            while (it.hasNext()){
                Tank t = it.next();

                if(t!=leader){
                    t.getMovement().setupLeaderToFollow(leader,i );
                    i++;
                }
            }

            return this;

        }

        public void reset() {
            setup();
        }

        public void  sortByDist(){

            formation.sort(new Comparator<Tank>() {
                @Override
                public int compare(Tank t1, Tank t2) {
                    if (t1.getPosition().dst(target)<t2.getPosition().dst(target)) {
                        return -1;
                    } else if (t1.getPosition().dst(target)>t2.getPosition().dst(target)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }
}
