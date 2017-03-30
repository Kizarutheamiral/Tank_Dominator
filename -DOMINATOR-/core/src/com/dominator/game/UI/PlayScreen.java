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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.dominator.game.Entities.Abrams;
import com.dominator.game.Entities.Map;
import com.dominator.game.Entities.Tank;
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.Quadtree.*;
import com.dominator.game.System.Engine;
import com.dominator.game.System.GameEventManager;
import com.dominator.game.System.GameState;
import com.dominator.game.System.GameStateManager;

/**
 * Created by Choujaa Wassil on 24/03/2017.
 *
 */
public class PlayScreen implements Screen, GestureDetector.GestureListener, InputProcessor{

    SpriteBatch batch;
    Texture img;
    Box2DDebugRenderer render;
    public OrthographicCamera camera;
    public Pathfinder finder;
    private  ShapeRenderer shapeRenderer;
    private boolean debug = true;
    private Texture texture;
    private int X_UNIT = 200;
    private int Y_UNIT = 200;
    private Vector3 previousCamPosition;
    private Vector3 previous = new Vector3();

    @Override
    public void show() {

        GameEventManager.instance().setup();

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        render = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        // texture = new Texture(Gdx.files.internal("font.png"));

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Constructs a new OrthographicCamera, using the given viewport width and height
        // Height is multiplied by aspect ratio.
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

            for (Tank tank: GameEventManager.instance().tanks) {
                shapeRenderer.setColor(1, 1, 0, 1);

                float width = 20f;

                if(tank.path != null){
                    for (Point node : tank.path){
                        shapeRenderer.rect(node.getX()-width/2,node.getY()-width/2,width,width);

                    }
                }

                shapeRenderer.line(tank.getX(),tank.getY(), tank.getX()+ tank.getDirection().x,tank.getY() +tank.getDirection().y);
            }
        }



        shapeRenderer.end();

        batch.begin();
        //: batch.draw(texture, 10, 10);
        batch.end();


    }


    private void camUpdate() {
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
        previousCamPosition = camera.position;
        previous = camera.unproject(previous.set(x,y,0));
        GameEventManager.instance().touchEvent(previous.x,previous.y);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        camera.zoom += 0.2;

        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Vector3 touchPos = new Vector3(x,y,0);
        camera.unproject(touchPos);
        touchPos.sub(previous).scl(1/100f);
        //System.out.println(touchPos + " " + camera.position);
        camera.position.set(touchPos.add(camera.position));
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
       // System.out.println(distance);
        camera.zoom+=(initialDistance-distance)/1000f;
        return true;
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount == 1){
            camera.zoom += .2f;
        }
        else if(amount == -1){
            camera.zoom -= .2f;
        }
        return true;
    }
}
