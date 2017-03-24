package com.dominator.game.UI;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dominator.game.Dominator;

/**
 * Created by Choujaa Wassil on 23/03/2017.
 *
 */
public class Menu implements Screen, InputProcessor {

    private Skin skin;
    private Stage stage;
    private MenuScrollPane panel;
    private Table container;
    private OrthographicCamera cam;
    private float width;
    private float height;
    private MenuScrollPane scroll;

    @Override
    public void show() {

        stage = new Stage(new ExtendViewport(1200, 720));

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        skin.add("top", skin.newDrawable("default-round", Color.RED), Drawable.class);
        skin.add("star-filled", skin.newDrawable("white", Color.YELLOW), Drawable.class);
        skin.add("star-unfilled", skin.newDrawable("white", Color.GRAY), Drawable.class);
        Gdx.input.setInputProcessor(stage);

        container = new Table();
        stage.addActor(container);
        container.setFillParent(true);

        scroll = new MenuScrollPane(skin, stage);

        container.add(scroll).expand().fill();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }




    @Override
    public void resize(int width, int height) {

        this.width = width;
        this.height = height;
        if (width == 480 && height == 320) {
            cam = new OrthographicCamera(700, 466);
            this.width = 700;
            this.height = 466;
        } else if (width == 320 && height == 240) {
            cam = new OrthographicCamera(700, 525);
            this.width = 700;
            this.height = 525;
        } else if (width == 400 && height == 240) {
            cam = new OrthographicCamera(800, 480);
            this.width = 800;
            this.height = 480;
        } else if (width == 432 && height == 240) {
            cam = new OrthographicCamera(700, 389);
            this.width = 700;
            this.height = 389;
        } else if (width == 960 && height == 640) {
            cam = new OrthographicCamera(800, 533);
            this.width = 800;
            this.height = 533;
        }  else if (width == 1366 && height == 768) {
            cam = new OrthographicCamera(1280, 720);
            this.width = 1280;
            this.height = 720;
        } else if (width == 1366 && height == 720) {
            cam = new OrthographicCamera(1280, 675);
            this.width = 1280;
            this.height = 675;
        } else if (width == 1536 && height == 1152) {
            cam = new OrthographicCamera(1366, 1024);
            this.width = 1366;
            this.height = 1024;
        } else if (width == 1920 && height == 1152) {
            cam = new OrthographicCamera(1366, 854);
            this.width = 1366;
            this.height = 854;
        } else if (width == 1920 && height == 1200) {
            cam = new OrthographicCamera(1366, 800);
            this.width = 1280;
            this.height = 800;
        } else if (width > 1280) {
            cam = new OrthographicCamera(1280, 768);
            this.width = 1280;
            this.height = 768;
        } else if (width < 800) {
            cam = new OrthographicCamera(800, 480);
            this.width = 800;
            this.height = 480;
        } else {
            cam = new OrthographicCamera(width, height);
        }

        int aspectRatio =  width /  height;

        stage.getViewport().update(width,height,true);

        cam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage(){
        return stage;
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
        return false;
    }
}
