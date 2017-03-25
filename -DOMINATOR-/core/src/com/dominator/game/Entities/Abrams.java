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
    public Abrams(float x, float y, World world, Map map) {
        super(40,map);
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.DynamicBody;
        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4,4);
        fd.density = 0.2f;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;
        fd.shape = shape;
        body = world.createBody(bd);
        body.createFixture(fd);
        body.getPosition().set(new Vector2(x,y));
    }


    @Override
    public void update() {
        move(true);
        attack();
    }

    @Override
    public void attack() {
        large_Calibre_Attack();
    }
}
