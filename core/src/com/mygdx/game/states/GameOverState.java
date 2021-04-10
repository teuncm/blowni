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
 * This class is the game over state. This state is set when the player dies.
 */

public class GameOverState extends State {
    private Texture background;
    private Image restartButton;
    private Image menuButton;

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Label gameOver;
    private Label score;
    private Label highScore;

    private int currentHighScore;

    public GameOverState(GameStateManager gsm, SpriteBatch sb) {
        super(gsm, sb);

        spriteBatch = sb;

        Blowni.setPlayState(false);

        background = new Texture("background.png");

        restartButton = new Image(new Texture("restart.png"));
        menuButton = new Image(new Texture("menu.png"));

        cam.setToOrtho(false, Blowni.getWIDTH(), Blowni.getHEIGHT());

        updateHighScore();

        gameOver = new Label("Game Over", new Label.LabelStyle(Blowni.getFont(), Color.FIREBRICK));
        score = new Label(String.format("Score: %d", PlayState.scoreInt), new Label.LabelStyle(Blowni.getFont(), Color.BLACK));
        highScore = new Label(String.format("High Score: %d", currentHighScore), new Label.LabelStyle(Blowni.getFont(), Color.BLACK));

        FitViewport viewport = new FitViewport(Blowni.getWIDTH(), Blowni.getHEIGHT(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        generateTable();
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX() * Blowni.getScaleX();
            float touchY = 1920 - Gdx.input.getY() * Blowni.getScaleY();

            /* Detect restart button push. */
            if (detectButtonTouch(restartButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.set(new PlayState(gsm, spriteBatch));
            }

            /* Detect quit button push. */
            else if (detectButtonTouch(menuButton, touchX, touchY)) {
                if(Blowni.isBackGroundMusic()) {
                    Blowni.getPlayStateMusic().pause();
                    Blowni.getBackgroundMusic().play();
                }
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.pop();
            }
        }
    }

    private void generateTable() {
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(gameOver).padTop(200);
        table.row();

        if (currentHighScore == PlayState.scoreInt) {
            table.add(new Label("New High Score!", new Label.LabelStyle(Blowni.getFont(), Color.GOLD))).padTop(30);
            table.row();
        }

        table.add(score).padTop(100);
        table.row();

        table.add(highScore).padTop(30);
        table.row();

        table.add(restartButton).padTop(250);
        table.row();

        table.add(menuButton).padTop(50);

        stage.addActor(table);
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render() {
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, -Blowni.bgIndex, Blowni.getWIDTH(), 9600);
        spriteBatch.end();
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
    }

    private void updateHighScore() {
        /* - Get currentHighScore that was saved in the preference file. */
        currentHighScore = Blowni.getPrefs().getInteger("currentHighScore", 0);

        /* - Change the current High Score if the current player score was better than the current
         *   High Score.
         */
        if (currentHighScore < PlayState.scoreInt) {
            Blowni.getPrefs().putInteger("currentHighScore", PlayState.scoreInt);
            Blowni.getPrefs().flush();
        }

        /* - Get the new (or not) Highscore from prefs again  */
        currentHighScore = Blowni.getPrefs().getInteger("currentHighScore", 0);
    }
}
