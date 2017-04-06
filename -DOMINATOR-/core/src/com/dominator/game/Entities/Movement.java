package com.dominator.game.Entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.dominator.game.Module.Intersection;
import com.dominator.game.System.GameEventManager;

import static com.dominator.game.CONSTANT.UNIT_SPACING;
import static com.dominator.game.CONSTANT.step;

/**
 *
 * Created by Choujaa Wassil on 02/04/2017.
 *
 */
public class Movement {

    private final Tank tank;

    private Tank leader = null;

    public Array<Vector2> path;

    private Vector2 pathTarget;

    private Vector2 destination;

    private Vector2 targetDirection;

    private boolean moving;

    private boolean formation;

    private boolean shouldmove = false;

    public int formationPos=-1;

    public Movement(Tank tank) {
        this.tank = tank;
        this.formation = false;
        this.moving = false;
        this.pathTarget =  new Vector2(0,0);
        this.destination =  new Vector2(0,0);
    }


    public Movement update(){

        if(!moving) return this;

        System.out.println((getDestination())+" "+formationPos);

        if(formation){
            followLeader();
        }
        else {
            followPath();
        }

        return this;
    }


    /**
     * Set the follow leader mode
     *
     * @Param : leader to follow
     */
    public void setupLeaderToFollow(Tank tank, int i) {
        formation = true;
        formationPos = i;
        leader = tank;
        destination = calculOffset(leader.getMovement().getDestination().cpy());
        path = GameEventManager.instance().finder.AstarPathFrom(this.tank.getPosition(),generateFormationPosition());
        moving=true;
        System.out.println(i);
        if(path==null){
            System.out.println("problem 1");
        }
        pathTarget=path.pop();
    }

    public void setupPath(Array<Vector2> points) {
        if (points==null || points.size==0) {
            System.out.println("problem 2");
            return;
        }
        moving = true;
        formation=false;
        leader=null;
        path=points;
        destination=points.first();
        pathTarget=path.pop();

    }

    /**
     *  Apply an rotational Impulse in the desiredDirection on the body with the desired Speed
     *  @Param : Body, desiredDirection , rotationSpeed
     *
     */
    public Movement rotate(Body body, Vector2 desiredDirection, float rotationSpeed){

        if(desiredDirection==null || body == null ){
            return this;
        }

        Vector2 orientation = body.getTransform().getOrientation();

        Vector2 direct = desiredDirection.cpy().sub(tank.getPosition()).nor();

        float orientationAngle = (direct.angle() - orientation.angle() < -180f) ? orientation.angle() - 360f: orientation.angle();

        float delta = direct.angle() - orientationAngle;

        if(delta>=180f){
            delta-=360f;
        }

        body.applyAngularImpulse(delta*rotationSpeed,true);

        return this;
    }

    private void followPath(){

        if (path == null) {
            return;
        }

        Vector2 currentSpeed = tank.body.getLinearVelocity();
        //System.out.println(currentSpeed+ " "+ pathTarget + " "+ body.getPosition());

        Vector2 appliedSpeed = new Vector2(
                tank.body.getTransform().getOrientation().x*tank.getSpeed(),
                tank.body.getTransform().getOrientation().y*tank.getSpeed())
                .sub(currentSpeed);


        if(rotate(tank.getBody(), pathTarget,tank.getRotationSpeed()).canMove(tank.rotationTolerance)){
            applySpeed(appliedSpeed);
        }

        if(isArrivedAt(pathTarget,10f)){
            if(path.size != 0){
                pathTarget = path.pop();
            }
            else if(pathTarget==destination){  // Tank is arrived at final destination
                System.out.println("arrivé");
                moving = false;
            }
        }

    }

    private void followLeader() {

        if(leader==null || formationPos == -1){   // if leader died
            return;
        }

        Vector2 currentSpeed = tank.getBody().getLinearVelocity();


        if(rotate(tank.getBody(), pathTarget,tank.getRotationSpeed()).canMove(tank.rotationTolerance)){

            Vector2 appliedSpeed = new Vector2(
                    tank.getBody().getTransform().getOrientation().x*tank.getSpeed(),
                    tank.getBody().getTransform().getOrientation().y*tank.getSpeed())
                    .sub(currentSpeed);

            applySpeed(appliedSpeed);
        }

        if (isArrivedAt(pathTarget,step)){

            if(path.size != 0){
                pathTarget = path.pop();
            }
            else if(!isArrivedAt(destination,step-2)){
                path = GameEventManager.instance().finder.AstarPathFrom(tank.getPosition(), generateFormationPosition());
            }
            else {
                moving=false;
                setFormation(false);
            }

        }



    }

    private Vector2 generateFormationPosition() {

        Vector2 tmp = new Vector2();

        tmp.set(leader.getMovement().getPathTarget());


        return calculOffset(tmp);
        /// calcul de l'offset par rapport au leader:

    }

    private Vector2 calculOffset(Vector2 tmp) {

        if(formationPos%2==1){  /// impair (odd)
            tmp.add(1*UNIT_SPACING,(1-(formationPos-1))*UNIT_SPACING);
        }
        else {
            tmp.add((-1)*UNIT_SPACING,(1-(formationPos))*UNIT_SPACING);
        }

        return tmp;
    }

    private boolean canMove(float rotationTolerance) {

        Vector2 orientation = tank.body.getTransform().getOrientation();

        Vector2 target = pathTarget.cpy().sub(tank.getPosition());

        float orientationAngle = (target.angle() - orientation.angle() < -180f) ? orientation.angle() - 360f: orientation.angle();

        float delta = target.angle() - orientationAngle;

        if(delta>=180f){
            delta-=360f;
        }

        return delta < rotationTolerance;
    }

    private void applySpeed(Vector2 appliedSpeed) {
        tank.body.applyLinearImpulse(appliedSpeed,tank.body.getWorldCenter(),false);
    }

    private boolean isArrivedAt(Vector2 pathTarget, float range) {
        return Intersection.CircleIntesectsCircle(pathTarget.x,pathTarget.y,range,tank.getX(),tank.getY(),range);
    }

    public Tank getLeader() {
        return leader;
    }

    public Vector2 getPathTarget() {
        return pathTarget;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setFormation(boolean formation) {
        this.formation = formation;
    }

    public boolean isFormation() {
        return formation;
    }

    public Vector2 getDestination() {
        return destination;
    }

    public Array<Vector2> getPath() {
        return path;
    }
}
