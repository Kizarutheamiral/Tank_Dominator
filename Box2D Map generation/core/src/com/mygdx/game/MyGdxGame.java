package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class MyGdxGame extends ApplicationAdapter {
    private static final float VIEWPORT_WIDTH = 250;
    private static final float VIEWPORT_HEIGHT = 250;

    SpriteBatch batch;
    World world;
    Box2DDebugRenderer render;
    BodyEditorLoader loader;
    private OrthographicCamera camera;
    private int PPM = 1;
    private OrthographicCamera b2cam;
    private Vector2 bottleModelOrigin;
    Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();

        world = new World(new Vector2(0, 0), true);

        render = new Box2DDebugRenderer();

        camera = new OrthographicCamera(0, 0);
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        b2cam = new OrthographicCamera();

        b2cam.setToOrtho(false, w / PPM, h /PPM);

        b2cam.translate(-w / 2*PPM, -h /2*PPM);

        b2cam.update();

        generate_Hero();

        createGround();

        generate_Obj_from_Json();

    }

    private void generate_Hero() {
    }

    public void inputUpdate(float delta) {

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            camera.translate(new Vector2(0, 3));
            b2cam.translate(new Vector2(0, 3 / PPM));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(new Vector2(0, -3));
            b2cam.translate(new Vector2(0, -3 / PPM));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.translate(new Vector2(-3, 0));
            b2cam.translate(new Vector2(-3 / PPM, 0));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(new Vector2(3, 0));
            b2cam.translate(new Vector2(3 / PPM, 0));

        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
            b2cam.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= 0.02;
            b2cam.zoom -= 0.02;
        }
        b2cam.update();

    }

    @Override
    public void render() {
        inputUpdate(1);
        world.step(1/60f, 6,2);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.end();
        render.render(world, b2cam.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }


    public void generate_Obj_from_Json() {



        loader = new BodyEditorLoader(Gdx.files.internal("data/static_obj.json"));

        // 1. Create a BodyDef, as usual.
        BodyDef bd = new BodyDef();
        bd.position.set(0, 0);
        bd.type = BodyDef.BodyType.DynamicBody;

        // 2. Create a FixtureDef, as usual.
        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;

        // 3. Create a Body, as usual.
        Body bottleModel = world.createBody(bd);

        // 4. Create the body fixture automatically by using the loader.
        loader.attachFixture(bottleModel, "static", fd, 100);

        bottleModelOrigin = loader.getOrigin("static", 100).cpy();



    }

    private void createGround() {
        BodyDef bd = new BodyDef();
        bd.position.set(0, 0);
        bd.type = BodyDef.BodyType.StaticBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50, 1);

        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.5f;
        fd.restitution = 0.5f;
        fd.shape = shape;

        world.createBody(bd).createFixture(fd);
    }
}