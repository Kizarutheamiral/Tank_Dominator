package com.dominator.game.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.System.GameEventManager;


import java.util.ArrayList;

import static com.dominator.game.Entities.Map.scale;

/**
 * Created by Choujaa Wassil on 21/02/2017.
 */
public abstract class Tank implements Dynamic_Bodies{

    Vector2 flowfield[][];
    protected float speed;
    public boolean moving = false;
    public ArrayList<Pathfinder.AstarNodes> path;
    protected Body body;
    private Map map;
    private Vector2 pathTarget;
    public Tank(float speed, Map map) {
        this.map = map;
        this.speed = speed;
    }

    /// Movement
    public void move(boolean option){

        // if target is close to a given range just go straight else, use flowfield or A*

        if(option){
            follow_Flow_field();
        } else {
            follow_Path();
        }

    }

    public void setFlowfield(Vector2 update[][]){
        flowfield = update;
    }

    @Override
    public void follow_Flow_field() {
/*        Vector2 pos =  body.getPosition();
        int position = map.getGraph().findNearestNode((int) pos.x/Map.step,(int) pos.y/Map.step);
        //System.out.println(map.getGraph().getX(position)+" "+map.getGraph().getY(position));
        if(flowfield!=null && position!=-1){
            //body.applyLinearImpulse(new Vector2(1,1),new Vector2(0,0),true);
            Vector2 currentSpeed = body.getLinearVelocity();
            Vector2 direction =  new Vector2(flowfield[map.getGraph().getX(position)][map.getGraph().getY(position)]).nor();
            Vector2 appliedSpeed= new Vector2(direction.y*speed,direction.x*speed).sub(currentSpeed);
            rotate(direction);
           // System.out.println(appliedSpeed.x + " "+appliedSpeed.y + "pos :"+position.x+" "+position.y);

           body.applyLinearImpulse(appliedSpeed,body.getPosition(),true);


            applyFrottement();


        }*/
        // generated by Flow
    }

    private void applyFrottement() {

    }

    public void follow_Path() {
        if (path != null){
            if (pathTarget == null){
                if(path.size() != 0){
                    pathTarget =  new Vector2(path.get(0).getX(), path.get(0).getY());
                } else {
                    path = null;
                    return;
                }
            }


            Vector2 currentSpeed = body.getLinearVelocity();
            //System.out.println(currentSpeed+ " "+ pathTarget + " "+ body.getPosition());
            Vector2 direction =  new Vector2().set(pathTarget.x-body.getPosition().x,pathTarget.y-body.getPosition().y);
            Vector2 appliedSpeed = new Vector2(direction.x*speed,direction.y*speed).sub(currentSpeed);

            body.applyForceToCenter(appliedSpeed,true);

            if (isArrivedAtTarget(pathTarget)){
                pathTarget = null;
                path.remove(0);
            }
        }
        // generated by A*
    }

    private boolean isArrivedAtTarget(Vector2 pathTarget) {

        Vector2 currentPos = body.getPosition();

        float DeltaX = pathTarget.x - Math.max(currentPos.x, Math.min(pathTarget.x, currentPos.x + 40));
        float DeltaY = pathTarget.y - Math.max(currentPos.y, Math.min(pathTarget.y, currentPos.y + 40));

        return (DeltaX * DeltaX + DeltaY * DeltaY) < (20 * 20);
    }

    @Override
    public void show() {}

    /// Attack

    public abstract void update();

    public abstract void attack();
    /// sandbox pattern
    protected void gatling_Cannon_Attack(){

    }

    protected void large_Calibre_Attack(){

    }

    public Vector2[][] getFlowfield() {
        return flowfield;
    }


    public void rotate(Vector2 direction){
        Vector2 current = new Vector2((float) Math.cos(body.getAngle()),(float) (Math.sin(body.getAngle())));
        float angle = current.angle() - direction.angle();
        body.applyTorque(angle,true);
    }

    public float getX() {
        return body.getPosition().x;
    }
    public float getY() {
        return body.getPosition().y;
    }

    public abstract boolean contain(float x, float y);

}
