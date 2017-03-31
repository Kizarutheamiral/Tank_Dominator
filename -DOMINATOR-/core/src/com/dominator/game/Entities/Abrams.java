package com.dominator.game.Entities;

import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.dominator.game.System.GameState;
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
    private int tourelleSpeed = 12;
    public Abrams(float x, float y, World world, Map map) {
        super(100f,10f , map);

        JsonToBody.RigidBodyModel model = GameStateManager.instance().Abrams;


        BodyDef bd = new BodyDef();

        width = model.maxX;
        heigth = model.maxX;

        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.DynamicBody;


        // 4. init
        body = world.createBody(bd);

        for (FixtureDef fd : model.fixtures) {
            // 2. Create a Shape, as usual.
            // 3. define parameters
            System.out.println(fd);

            fd.density = 0.1f;
            fd.friction = 0.5f;
            fd.restitution = 0.3f;

            body.createFixture(fd);
        }

        BodyDef td = new BodyDef();

        td.position.set(x, y);
        td.type = BodyDef.BodyType.DynamicBody;

        tourelle = world.createBody(td);

        for (FixtureDef fd : GameStateManager.instance().AbramsTourelle.fixtures) {
            // 2. Create a Shape, as usual.
            // 3. define parameters
            System.out.println(fd);

            fd.density = 0.1f;
            fd.friction = 0.5f;
            fd.restitution = 0.3f;

            tourelle.createFixture(fd);
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
        move(false);
        attack();
    }

    @Override
    public void attack() {
        large_Calibre_Attack();
    }

    @Override
    public boolean rotate() {

        Vector2 orientation = tourelle.getTransform().getOrientation().cpy();
        Vector2 direction = getDirection();

        float orientationAngle = (direction.angle() - orientation.angle() < -180f) ? 360f - orientation.angle() : orientation.angle();

        float delta = direction.angle() - orientationAngle;

        if(delta>=180f){
            delta-=360f;
        }

        tourelle.applyAngularImpulse(delta*tourelleSpeed,true);

        orientation = body.getTransform().getOrientation().cpy();

        orientationAngle = (direction.angle() - orientation.angle() < -180f) ? 360f - orientation.angle() : orientation.angle();

        delta = direction.angle() - orientationAngle;

        if(delta>=180f){
            delta-=360f;
        }

        body.applyAngularImpulse(delta*rotationSpeed,true);

        return  Math.abs(delta)<10;
    }

    @Override
    public boolean contain(float x, float y) {
        Vector2 pos = body.getPosition();
        //System.out.println(pos.x +" "+x);
        return !(x>pos.x + width*2|| y>pos.y +heigth*2|| x<pos.x - width|| y<pos.y -width);
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
