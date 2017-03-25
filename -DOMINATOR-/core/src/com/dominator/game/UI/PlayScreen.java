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
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.Quadtree.Func;
import com.dominator.game.Quadtree.Node;
import com.dominator.game.Quadtree.NodeType;
import com.dominator.game.Quadtree.QuadTree;
import com.dominator.game.System.Engine;
import com.dominator.game.System.GameEventManager;
import com.dominator.game.System.GameState;
import com.dominator.game.System.GameStateManager;

/**
 * Created by Choujaa Wassil on 24/03/2017.
 *
 */
public class PlayScreen implements Screen, GestureDetector.GestureListener, InputProcessor{

    Engine engine;
    private Stage stage;
    SpriteBatch batch;
    Texture img;
    private World world;
    Box2DDebugRenderer render;
    public OrthographicCamera camera;
    public Abrams hero;
    Map map;
    public Pathfinder finder;
    private ShapeRenderer shapeRenderer;
    public Rectangle rectangle;
    private boolean debug = true;
    private Texture texture;


    private int X_UNIT = 200;
    private int Y_UNIT = 200;
    private Vector3 previousCamPosition;
    private Vector3 previous;

    @Override
    public void show() {
        batch = new SpriteBatch();
        engine = new Engine();
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        render = new Box2DDebugRenderer();
        map = new Map().loadMap(engine.getWorld());
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

        generate_Hero();
    }

    @Override
    public void render(float delta) {
        camUpdate();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.tick(delta, camera, render);
        batch.begin();
        //: batch.draw(texture, 10, 10);
        batch.end();

        if(debug) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1, 1, 1, 1);


            map.quadTree.navigate(map.quadTree.getRootNode(), new Func() {
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

        shapeRenderer.end();

    }

    private void generate_Hero() {
        hero = new Abrams(20,20,engine.getWorld(), map);
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
        System.out.println(touchPos + " " + camera.position);
        camera.position.set(touchPos.add(camera.position));
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        System.out.println(distance);
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
