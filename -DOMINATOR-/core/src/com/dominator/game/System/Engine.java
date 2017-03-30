package com.dominator.game.System;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Choujaa Wassil on 22/03/2017.
 */
public class Engine {

    private World world;

    public Engine() {
        this.world = new World(new Vector2(0,0),false);
    }

    public void tick(float delta, OrthographicCamera camera, Box2DDebugRenderer render){
        render.render(world, camera.combined);
        world.step(1/60f,6,2);

    }


    public World getWorld() {
        return world;
    }
}
