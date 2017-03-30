package com.dominator.game.Entities;

import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

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


    public Abrams(float x, float y, World world, Map map) {
        super(100f,10f , map);
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.DynamicBody;
        // 2. Create a Shape, as usual.
        FixtureDef fd = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        float Box2Dheigth =10;
        float BOX2Dwidth = 10;
        shape.setAsBox(BOX2Dwidth,Box2Dheigth);
        setWidth(BOX2Dwidth*2);
        setHeigth(Box2Dheigth*2);
        // 3. define parameters
        fd.density = 0.25f;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;
        fd.shape = shape;
        // 4. init
        body = world.createBody(bd);
        body.createFixture(fd);
        body.getPosition().set(new Vector2(x,y));

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
        Vector2 orientation = body.getTransform().getOrientation().cpy();
        Vector2 direction = getDirection();
        
        float delta = direction.angle() - orientation.angle();

        if(delta>=180f){
            delta-=360f;
        }
        body.applyAngularImpulse(delta*rotationSpeed,true);

        System.out.println(delta + " orientation "+ orientation.angle()+" direc"+direction.angle());

        return  Math.abs(delta)<20;
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
