package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Blowni;

/**
 * This class contains the pause state of the game.
 */

public class PauseState extends State {
    private Texture background;
    private Image resumeButton;
    private Image quitButton;
    private Image optionButton;
    private SpriteBatch spriteBatch;

    private Stage stage;
    private Label score;
    private Label paused;

    public PauseState(GameStateManager gsm, SpriteBatch sb) {
        super(gsm, sb);

        Blowni.setPlayState(false);

        background = new Texture("background.png");
        resumeButton = new Image(new Texture("resume.png"));
        optionButton = new Image(new Texture("options.png"));
        quitButton = new Image(new Texture("quit.png"));

        spriteBatch = sb;

        cam.setToOrtho(false, Blowni.getWIDTH(), Blowni.getHEIGHT());

        score = new Label(String.format("Score: %d", PlayState.scoreInt), new Label.LabelStyle(Blowni.getFont(), Color.BLACK));
        paused = new Label("Paused", new Label.LabelStyle(Blowni.getFont(), Color.BLACK));

        FitViewport viewport = new FitViewport(Blowni.getWIDTH(), Blowni.getHEIGHT(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        generateTable();
    }

    private void generateTable() {
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(paused).padTop(200);
        table.row();

        table.add(score).padTop(100);
        table.row();

        table.add(resumeButton).padTop(250);
        table.row();

        table.add(optionButton).padTop(50);
        table.row();

        table.add(quitButton).padTop(50);

        stage.addActor(table);
    }

    /* - This function is used to handle the input of the user on the phone.
     * - This is especially made for screen interactions from the user.
     * - The if and else if with xTouch and yTouch is used for buttons, this way users can click on
     *   those button to change a game state.
     */
    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX() * Blowni.getScaleX();
            float touchY = 1920 - Gdx.input.getY() * Blowni.getScaleY();

            /* Detect resume button push. */
            if (detectButtonTouch(resumeButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                Blowni.setPlayState(true);
                gsm.pop();

            /* Detect options button push. */
            } else if (detectButtonTouch(optionButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.push(new OptionsState(gsm, spriteBatch, true));


            /* Detect quit button push. */
            } else if (detectButtonTouch(quitButton, touchX, touchY)) {
                if(Blowni.isBackGroundMusic()) {
                    Blowni.getPlayStateMusic().pause();
                    Blowni.getBackgroundMusic().play();
                }
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.pop();
                gsm.pop();
            }
        }
    }

    /* - This function invokes handleInput() and this function will be invoked every game tick.
     */
    @Override
    public void update(float dt) {
        handleInput();
    }

    /* - This function is used to draw everything on the screen.
     * - Everything that the user see on their screen was drawn from here.
     */
    @Override
    public void render() {
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, -Blowni.bgIndex, Blowni.getWIDTH(), 9600);
        spriteBatch.end();

        stage.draw();
    }

    /* - This function is used to clear up memory when it is not need anymore.
     * - This is used to prevent memory leaks.
     */
    @Override
    public void dispose() {
        background.dispose();
    }
}
