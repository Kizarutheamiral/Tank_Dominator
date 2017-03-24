package com.dominator.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.dominator.game.Entities.Abrams;
import com.dominator.game.Entities.Map;
import com.dominator.game.Module.Pathfinder;
import com.dominator.game.System.Engine;
import com.dominator.game.System.GameState;
import com.dominator.game.System.GameStateManager;

/**
 * Created by Choujaa Wassil on 24/03/2017.
 */
public class PlayScreen implements Screen{

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
    private boolean debug = false;
    private Texture texture;


    private int X_UNIT = 200;
    private int Y_UNIT = 200;
    @Override
    public void show() {
        batch = new SpriteBatch();
        engine = new Engine();
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        render = new Box2DDebugRenderer();
        map = new Map().loadMap(engine.getWorld());

       // texture = new Texture(Gdx.files.internal("font.png"));

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Constructs a new OrthographicCamera, using the given viewport width and height
        // Height is multiplied by aspect ratio.
        camera = new OrthographicCamera(X_UNIT, Y_UNIT * (h / w));

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        batch.getProjectionMatrix().set(camera.combined);
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
}
