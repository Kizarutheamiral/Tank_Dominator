package com.dominator.game.UI;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.dominator.game.Entities.Tank;
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.Quadtree.*;
import com.dominator.game.System.GameEventManager;
import com.dominator.game.System.GameStateManager;
import static com.badlogic.gdx.math.MathUtils.clamp;
import static com.dominator.game.CONSTANT.*;

/**
 * Created by Choujaa Wassil on 24/03/2017.
 *
 */
public class PlayScreen implements Screen, GestureDetector.GestureListener, InputProcessor{

    private SpriteBatch batch;
    private Box2DDebugRenderer render;
    public OrthographicCamera camera;
    public Pathfinder finder;
    private  ShapeRenderer shapeRenderer;
    private ShapeRenderer fillRenderer;
    private boolean debug = true;
    private Texture texture;
    private boolean DoubleCount=false;
    private Vector3 previous = new Vector3();

    /// Graphics objects


    private final Rectangle selectionRectangle = new Rectangle();
    private boolean dragging = false;
    private Vector3 DragContextBegin = new Vector3();
    private Vector3 DragContextCurrent = new Vector3();


    @Override
    public void show() {

        GameEventManager.instance().setup();

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        render = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
        fillRenderer = new ShapeRenderer();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(X_UNIT, Y_UNIT * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        batch.getProjectionMatrix().set(camera.combined);

        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(new GestureDetector(this));
        im.addProcessor(this);
        Gdx.input.setInputProcessor(im);
    }

    @Override
    public void render(float delta) {
        GameEventManager.instance().update(delta, camera, render);

        camUpdate();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        render.render(GameEventManager.instance().engine.getWorld(),camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        fillRenderer.setProjectionMatrix(camera.combined);
        fillRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if(debug) {

            shapeRenderer.setColor(1, 1, 1, 1);


            GameEventManager.instance().map.quadTree.navigate(GameEventManager.instance().map.quadTree.getRootNode(), new Func() {
                @Override
                public void call(QuadTree quadTree, Node node) {
                    if(node.getNodeType() == NodeType.LEAF_EMPTY){
                        shapeRenderer.setColor(0,1, 1, 1);
                    } else if(node.getNodeType() == NodeType.LEAF_OBSTRUCTED){
                        shapeRenderer.setColor(1, 0, 0, 1);
                    }
                    shapeRenderer.rect(node.getCenterX()-node.getW()/2,node.getCenterY()-node.getW()/2,node.getW(),node.getW());
                }
            },0,0,1800,1800);
        }

        for (Tank tank: GameEventManager.instance().tanks) {
            shapeRenderer.setColor(1, 1, 0, 1);

            tank.draw(shapeRenderer,fillRenderer);


            //shapeRenderer.line(tank.getX(),tank.getY(), tank.getX()+ tank.getDirection().x,tank.getY() +tank.getDirection().y);
        }

        shapeRenderer.setColor(1, 1, 0, 1);

        if(dragging){
            shapeRenderer.rect(selectionRectangle.x,selectionRectangle.y,selectionRectangle.width,selectionRectangle.height);
        }

        shapeRenderer.end();
        fillRenderer.end();
        batch.begin();
        //: batch.draw(texture, 10, 10);
        batch.end();


    }


    private void camUpdate() {

        DragContextCurrent.set(Gdx.input.getX(),Gdx.input.getY(),0);


        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.translate(new Vector2(0, 10));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(new Vector2(0, -10));
        }
        if (Gdx.input.isKeyPressed( Input.Keys.Q)) {
            camera.translate(new Vector2(-10, 0));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(new Vector2(10, 0));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            /// dezooming +
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            /// zomming -
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            // Debug mode
            debug = !debug;
        }
        if((((Gdx.input.isTouched(0) && !Gdx.input.isTouched(1) ) || Gdx.input.isButtonPressed(Input.Buttons.LEFT))) && ( DragContextCurrent.x != DragContextBegin.x && DragContextCurrent.y != DragContextBegin.y)  ){

            dragging=true;

            Vector3 begin =  camera.unproject(DragContextBegin.cpy());
            Vector3 current =  camera.unproject(DragContextCurrent.cpy());

            selectionRectangle.set(begin.x,begin.y,current.x-begin.x,current.y-begin.y);

            float rectX = (selectionRectangle.width<0)? selectionRectangle.x+selectionRectangle.width:selectionRectangle.x;
            float rectY = (selectionRectangle.height<0)? selectionRectangle.y+selectionRectangle.height:selectionRectangle.y;

            selectionRectangle.set(rectX,rectY,Math.abs(selectionRectangle.width),Math.abs(selectionRectangle.height));

            GameEventManager.instance().rectSelectionEvent(selectionRectangle.x,selectionRectangle.y,selectionRectangle.width,selectionRectangle.height);


        }
        if(  Gdx.input.isTouched(0) && Gdx.input.isTouched(1)) {

            camera.translate((DragContextCurrent.x-DragContextBegin.x)/8f,-(DragContextCurrent.y-DragContextBegin.y)/8f);

        }
        if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){

            camera.translate((DragContextCurrent.x-DragContextBegin.x)/8f,-(DragContextCurrent.y-DragContextBegin.y)/8f);

            dragging=false;
        }
        if( !( DragContextCurrent.x != DragContextBegin.x && DragContextCurrent.y != DragContextBegin.y) ){
            dragging=false;
        }



        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = X_UNIT;
        camera.viewportHeight = Y_UNIT * height/width;
        camera.update();
    }

    @Override
    public void pause() {
        GameStateManager.instance().pause();
    }

    @Override
    public void resume() {
        GameStateManager.instance().resume();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {


        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        // System.out.println(distance);
        if(!dragging){
            clampCamera(camera.zoom + (initialDistance-distance)/10000f);
        }
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        DragContextBegin.set(x,y,0);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        Vector3 unproject = camera.unproject(DragContextBegin.cpy());

        boolean selection = dragging ;

        GameEventManager.instance().touchEvent(unproject.x,unproject.y,selection);

        selectionRectangle.set(0,0,0,0);

        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {

        if(amount == 1){
            clampCamera(camera.zoom + (float) step/20);
        }
        else if(amount == -1){
            clampCamera(camera.zoom - (float) step/20);
        }
        return true;
    }

    private void clampCamera(float value) {
        camera.zoom = clamp(value, scale/100,scale/10);
    }
}
