package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Blowni;

/**
 *  This class is the help state of the game.
 */

public class HelpState extends State{
    private int currentPage;

    private Texture background;

    private Image logo;
    private Image help;
    private Image backButton;

    private Stage stage;
    private Table table;

    private SpriteBatch spriteBatch;

    protected HelpState(GameStateManager gsm, SpriteBatch sb) {
        super(gsm, sb);

        spriteBatch = sb;

        currentPage = 1;

        background = new Texture("background.png");
        logo = new Image(new Texture("blowni_logo.png"));
        help = new Image(new Texture("helpPage1.png"));
        backButton = new Image(new Texture("back.png"));


        cam.setToOrtho(false, Blowni.getWIDTH(), Blowni.getHEIGHT());

        FitViewport viewport = new FitViewport(Blowni.getWIDTH(), Blowni.getHEIGHT(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        generateTable();
    }

    /* This function generates the table which is the look of the helpstate. */
    private void generateTable() {
        table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(logo).padTop(100).expandX();
        table.row();

        table.add(help).padTop(100).expandX();
        table.row();

        table.add(backButton).padTop(50).expandX();

        stage.addActor(table);
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX() * Blowni.getScaleX();
            float touchY = 1920 - Gdx.input.getY() * Blowni.getScaleY();

            /* Checks if backbutton is clicked. */
            if (detectButtonTouch(backButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.pop();
            }

            /* Checks if help button is clicked and show next page by replacing the help image. */
            if (detectButtonTouch(help, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                currentPage = (currentPage == 1) ? 2 : 1;

                help = new Image(new Texture("helpPage" + Integer.toString(currentPage) + ".png"));

                table.remove();
                generateTable();
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        Blowni.bgIndex += 4;
        Blowni.bgIndex %= 7680;
    }

    @Override
    public void render() {
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, -Blowni.bgIndex, Blowni.getWIDTH(), 9600);
        spriteBatch.end();

        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();
    }

    @Override
    public void dispose() {

    }
}
