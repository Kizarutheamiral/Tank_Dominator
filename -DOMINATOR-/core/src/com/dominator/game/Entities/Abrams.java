package com.dominator.game.Entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.dominator.game.System.GameStateManager;

/**
 * Created by Choujaa Wassil on 21/02/2017.
 *
 *
 *  Hero is 4x4
 *
 */
public class Abrams extends Tank{
    private float width;
    private float heigth;
    private Body tourelle;
    private float tourelleSpeed = 40f;

    public Abrams(float x, float y, World world, Map map) {
        super(400f,30f ,10f, map, 150f, 30f);

        JsonToBody.RigidBodyModel model = GameStateManager.instance().Abrams;


        BodyDef bd = new BodyDef();

        width = model.maxX;
        heigth = model.maxX;

        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.DynamicBody;


        // 4. init
        body = world.createBody(bd);

        for (FixtureDef fd : model.fixtures) {
            fd.density = 0.3f;
            fd.friction = 0.5f;
            fd.restitution = 0.3f;

            body.createFixture(fd);
        }

        BodyDef td = new BodyDef();

        td.position.set(x, y);
        td.type = BodyDef.BodyType.DynamicBody;

        tourelle = world.createBody(td);

        for (FixtureDef fd : GameStateManager.instance().AbramsTourelle.fixtures) {
            fd.density = 0.5f;
            fd.friction = 0.5f;
            fd.restitution = 0.3f;
            tourelle.createFixture(fd);
        }

        for(int i=0; i<tourelle.getFixtureList().size;i++){
            this.tourelle.getFixtureList().get(i).setSensor(true);
        }

        RevoluteJointDef revolute = new RevoluteJointDef();

        revolute.bodyA=body;
        revolute.bodyB=tourelle;
        revolute.collideConnected=false;
        revolute.localAnchorA.set(0,0);
        revolute.localAnchorB.set(0,0);

        world.createJoint(revolute);

        tourelle.setAngularDamping(20f);

        // body.getPosition().set(new Vector2(x,y));
        setupFriction();

    }


    @Override
    public void update() {
        movement.update().rotate(tourelle,movement.getPathTarget(),tourelleSpeed);
        attack();
    }

    @Override
    public void attack() {
        large_Calibre_Attack();
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return heigth;
    }

    public void setHeigth(float heigth) {
        this.heigth = heigth;
    }

}
