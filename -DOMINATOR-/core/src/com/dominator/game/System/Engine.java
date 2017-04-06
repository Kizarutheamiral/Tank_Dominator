package com.dominator.game.System;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Choujaa Wassil on 22/03/2017.
 */
public class Engine {

    private World world;

    public Engine() {
        this.world = new World(new Vector2(0,0),false);
    }

    public Engine setup(){

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body A = contact.getFixtureA().getBody();
                Body B = contact.getFixtureB().getBody();

                if(A.getLinearVelocity().len() < 50 && B.getLinearVelocity().len() <50){
                    return;
                }

                if(A.getLinearVelocity().len() > B.getLinearVelocity().len()){
                    B.setLinearVelocity(B.getLinearVelocity().scl(1/2f));
                    B.setAngularVelocity(B.getAngularVelocity()*1/2f);
                } else {
                    A.setLinearVelocity(A.getLinearVelocity().scl(1/2f));
                    A.setAngularVelocity(A.getAngularVelocity()*1/2f);
                }

            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });

        return this;
    }

    public void tick(float delta, OrthographicCamera camera, Box2DDebugRenderer render){
        render.render(world, camera.combined);
        world.step(1/60f,6,2);

    }


    public World getWorld() {
        return world;
    }
}
