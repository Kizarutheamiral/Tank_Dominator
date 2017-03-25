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
        super(40,map);
        BodyDef bd = new BodyDef();
        bd.position.set(x, y);
        bd.type = BodyDef.BodyType.DynamicBody;
        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        heigth =10;
        width = 10;
        shape.setAsBox(width,heigth);

        fd.density = 0.2f;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;
        fd.shape = shape;
        body = world.createBody(bd);
        body.createFixture(fd);
        body.getPosition().set(new Vector2(x,y));
        System.out.println("fzeiufezq");
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
    public boolean contain(float x, float y) {
        Vector2 pos = body.getPosition();
        //System.out.println(pos.x +" "+x);
        return !(x>pos.x + width*2|| y>pos.y +heigth*2|| x<pos.x - width|| y<pos.y -width);

    }
}
