package com.dominator.game.Entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.dominator.game.Module.Intersection;
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.Quadtree.Point;
import com.dominator.game.System.GameEventManager;
import com.sun.jnlp.IntegrationServiceImpl;


import java.util.ArrayList;

import static com.dominator.game.CONSTANT.step;


/**
 * Created by Choujaa Wassil on 21/02/2017.
 */
public strictfp abstract class Tank {

    protected Body body;
    private Map map;
    ///////////////////////////////////////////////////////
    private boolean selected = false;
    protected Movement movement;
    private float speed;
    private float rotationSpeed;
    public float rotationTolerance;
    ///////////////////////////////////////////////////////
    private float health;
    private float damage;
    public Tank(float speed, float rotationSpeed,float rotationTol , Map map, float health, float damage) {
        this.map = map;
        this.speed = speed;
        this.rotationTolerance=rotationTol;
        this.rotationSpeed = rotationSpeed;
        this.movement = new Movement(this);
        this.health = health;
        this.damage = damage;
    }

    protected void setupFriction(){
        body.setAngularDamping(10f);
        body.setLinearDamping(10f);
    }
    /// Attack

    public abstract void update();

    public abstract void attack();
    /// sandbox pattern
    protected void gatling_Cannon_Attack(){

    }

    protected void large_Calibre_Attack(){

    }

    public float getX() {
        return body.getPosition().x;
    }
    public float getY() {
        return body.getPosition().y;
    }
    public abstract float getHeight();
    public abstract float getWidth();

    public boolean contain(float x, float y) {

        for (Fixture fix: body.getFixtureList()) {
            if (fix.testPoint(x,y)){
                return true;
            }
        }
        return false;
    }


    public boolean intersect(float x, float y, float width, float height) {

        final Boolean[] found = {false};

        body.getWorld().QueryAABB(new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                found[0] =  body.getFixtureList().contains(fixture,false);
                return !found[0];
            }
        },x, y, x+width, y+height);

        return found[0];
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    public float getSpeed() {
        return speed;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public float getHealth() {
        return health;
    }

    public void draw(ShapeRenderer shapeRenderer, ShapeRenderer fillRenderer){
        if (isSelected()){
            shapeRenderer.setColor(1, 0.5f, 0, 1);
            shapeRenderer.circle(getX(),getY(),step-1);
        }
        if(movement.isFormation()){
            fillRenderer.setColor(1,0,0,1);
            fillRenderer.circle(getX(),getY(),step-3);
        }

        if(movement.getDestination()!=null && movement.isMoving()) {
            shapeRenderer.setColor(1,1,1,1);
            shapeRenderer.circle(movement.getDestination().x,movement.getDestination().y,step+13);
        }

        if(movement.getPath() != null){
            for(Vector2 path : movement.getPath()){
                shapeRenderer.setColor(0.9f,0.9f,0.9f,1);
                shapeRenderer.circle(path.x,path.y,step-1);
            }
        }

        if(movement.getPathTarget() != null){
            shapeRenderer.setColor(0.3f,0.7f,0.6f,1);
            shapeRenderer.circle(movement.getPathTarget().x,movement.getPathTarget().y,step+7);

        }


    }
}