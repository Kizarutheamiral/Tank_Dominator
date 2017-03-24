package com.dominator.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.dominator.game.CONSTANT;
import com.dominator.game.System.GameStateManager;

/*
*
*  A custom horizontal scroll pane
*
 */

public class MenuScrollPane extends ScrollPane {

    private boolean wasPanDragFling = false;

    private Table content;

    private Skin skin;

    private Stage stage;


    public MenuScrollPane(Skin skin, Stage stage) {
        super(null, skin);
        this.skin = skin;
        this.stage = stage;
        content = new Table();
        content.defaults().space(50);
        content.setColor(new Color(1,0,0,1));

        setWidget(content);
        setup();

    }


    private void setup () {

        setFlingTime(0.2f);
        setPageSpacing(25f);

        float width = stage.getViewport().getWorldWidth();

        ////////////// ADD HOME PICS


        Table table = new Table();
        table.setWidth(width);



        TextButton PLAY = new TextButton("Play",skin);
        PLAY.getLabel().setFontScale(10f, 10f);

        PLAY.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStateManager.instance().launchGame();
            }
        });

        table.add(PLAY).width(500).expandY().height(500).expandX();

        content.add(table).width(width).expandY().fill();
        ////////////// ADD LEVELS

        int c = 1;
        for (int l = 0; l < CONSTANT.Levels; l++) {
            Table levels = new Table().pad(50);
            levels.defaults().pad(20, 40, 20, 40);
            for (int y = 0; y < CONSTANT.MENU_ROWS; y++) {
                levels.row();
                for (int x = 0; x < CONSTANT.MENU_COLUMS; x++) {
                    levels.add(getLevelButton(c++)).expand().fill();
                }
            }
            addPage(levels);
        }
    }

    public void addPages (Actor... pages) {
        for (Actor page : pages) {
            content.add(page).expandY().fillY();
        }
    }

    public void addPage (Actor page) {
        content.add(page).expandY().fillY();

    }

    @Override
    public void act (float delta) {
        super.act(delta);
        if (wasPanDragFling && !isPanning() && !isDragging() && !isFlinging()) {
            wasPanDragFling = false;
            scrollToPage();
        } else {
            if (isPanning() || isDragging() || isFlinging()) {
                wasPanDragFling = true;
            }
        }
    }

    @Override
    public void setWidth (float width) {
        super.setWidth(width);
        if (content != null) {
            for (Cell cell : content.getCells()) {
                cell.width(width);
            }
            content.invalidate();
        }
    }

    public void setPageSpacing (float pageSpacing) {
        if (content != null) {
            content.defaults().space(pageSpacing);
            for (Cell cell : content.getCells()) {
                cell.space(pageSpacing);
            }
            content.invalidate();
        }
    }

    private void scrollToPage() {

        final float width = getWidth();
        final float scrollX = getScrollX();
        final float maxX = getMaxX();

        if (scrollX >= maxX || scrollX <= 0) return;

        Array<Actor> pages = content.getChildren();

        float pageX = 0;
        float pageWidth = 0;

        if (pages.size > 0) {
            for (Actor a : pages) {
                pageX = a.getX();
                pageWidth = a.getWidth();
                if (scrollX < (pageX + pageWidth * 0.5f)) { // scroll right
                    break;
                }
            }
            setScrollX(MathUtils.clamp(pageX - (width - pageWidth) / 2, 0, maxX));
        }
    }



    public Button getLevelButton(int level) {
        Button button = new Button(skin);
        Button.ButtonStyle style = button.getStyle();
        style.up = 	style.down = null;
        Label label = new Label(Integer.toString(level), skin);
        label.setFontScale(2f);
        label.setAlignment(Align.center);

        button.stack(new Image(skin.newDrawable("default-round-large", Color.RED)), label).expand().fill();

        skin.add("star-filled", skin.newDrawable("white", Color.YELLOW), Drawable.class);
        skin.add("star-unfilled", skin.newDrawable("white", Color.GRAY), Drawable.class);

        int stars = MathUtils.random(-1, +3);
        Table starTable = new Table();
        starTable.defaults().pad(5);
        if (stars >= 0) {
            for (int star = 0; star < 3; star++) {
                if (stars > star) {
                    starTable.add(new Image(skin.getDrawable("star-filled"))).width(20).height(20);
                } else {
                    starTable.add(new Image(skin.getDrawable("star-unfilled"))).width(20).height(20);
                }
            }
        }

        button.row();

        button.add(starTable).height(30).width(stage.getViewport().getWorldWidth()/CONSTANT.MENU_COLUMS - 80);

        button.setName("Level" + Integer.toString(level));
        button.addListener(levelClickListener);

        return button;
    }

    /**
     * Handle the click - in real life, we'd go to the level
     */
    public ClickListener levelClickListener = new ClickListener() {
        @Override
        public void clicked (InputEvent event, float x, float y) {
            System.out.println("Click: " + event.getListenerActor().getName());
        }
    };



}