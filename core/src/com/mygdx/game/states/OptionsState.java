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
 * This class contains the options menu implementation.
 */

public class OptionsState extends State {
    private Texture background;

    private Image logo;
    private Image resetButton;
    private Image backButton;
    private Image musicButton;
    private Image effectsButton;
    private Image confirmationButton;

    private Table table;

    private Label music;
    private Label soundEffects;
    private Label confirmation;

    private boolean resetClicked;
    private boolean fromPlayState;

    private SpriteBatch spriteBatch;
    private Stage stage;

    protected OptionsState(GameStateManager gsm, SpriteBatch sb, boolean fps) {
        super(gsm, sb);

        fromPlayState = fps;

        spriteBatch = sb;

        Blowni.setPlayState(false);

        resetClicked = false;

        background = new Texture("background.png");
        logo = new Image(new Texture("blowni_logo.png"));
        resetButton = new Image(new Texture("reset.png"));
        backButton = new Image(new Texture("back.png"));
        confirmationButton = new Image(new Texture("yes.png"));

        /* Checks if music is on or off. */
        if(Blowni.isBackGroundMusic()) {
            musicButton = new Image(new Texture("soundOn.png"));
        } else {
            musicButton = new Image(new Texture("soundOff.png"));
        }

        /* Checks if soundeffects are on or off. */
        if(Blowni.isSoundEffects()) {
            effectsButton = new Image(new Texture("soundOn.png"));
        } else {
            effectsButton = new Image(new Texture("soundOff.png"));
        }

        music = new Label("Music", new Label.LabelStyle(Blowni.getFont(), Color.BLACK));
        soundEffects = new Label("Sound effects", new Label.LabelStyle(Blowni.getFont(), Color.BLACK));
        confirmation = new Label("Reset Score?" , new Label.LabelStyle(Blowni.getFont(), Color.BLACK));

        cam.setToOrtho(false, Blowni.getWIDTH(), Blowni.getHEIGHT());

        FitViewport viewport = new FitViewport(Blowni.getWIDTH(), Blowni.getHEIGHT(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        generateTable();
    }

    /* This function generates a table which is the look of the options state. */
    private void generateTable() {
        table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(logo).padTop(100).expandX().colspan(2);
        table.row();

        table.add(music).padTop(100).expandX().right();
        table.add(musicButton).padTop(100).expandX();
        table.row();

        table.add(soundEffects).padTop(50).expandX().right();
        table.add(effectsButton).padTop(50).expandX();
        table.row();

        if(resetClicked) {
            table.add(confirmation).padTop(50).expandX().right();
            table.add(confirmationButton).padTop(50).expandX();
        } else {
            table.add(resetButton).padTop(50).expandX().colspan(2);
        }

        table.row();

        table.add(backButton).padTop(50).expandX().colspan(2);

        stage.addActor(table);
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX() * Blowni.getScaleX();
            float touchY = 1920 - Gdx.input.getY() * Blowni.getScaleY();

            /* Checks if music button is clicked. Then checks if music is on or off and changes it. */
            if (detectButtonTouch(musicButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }

                if(Blowni.isBackGroundMusic()) {
                    Blowni.getPrefs().putBoolean("musicOn", false);
                    Blowni.getPrefs().flush();
                    Blowni.setBackGroundMusic(Blowni.getPrefs().getBoolean("musicOn"));
                    Blowni.pauseMusic();
                    musicButton = new Image(new Texture("soundOff.png"));
                } else {
                    Blowni.getPrefs().putBoolean("musicOn", true);
                    Blowni.getPrefs().flush();
                    Blowni.setBackGroundMusic(Blowni.getPrefs().getBoolean("musicOn"));

                    if(fromPlayState) {
                        Blowni.getPlayStateMusic().play();
                    } else {
                        Blowni.getBackgroundMusic().play();
                    }

                    musicButton = new Image(new Texture("soundOn.png"));
                }

                table.remove();
                generateTable();
            }

            /* Checks if sound effects button is clicked. Then checks if sound effects are on or
             * off and changes it.
             */
            if (detectButtonTouch(effectsButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getPrefs().putBoolean("soundFxOn", false);
                    Blowni.getPrefs().flush();
                    Blowni.setSoundEffects(Blowni.getPrefs().getBoolean("soundFxOn"));

                    effectsButton = new Image(new Texture("soundOff.png"));
                } else {
                    Blowni.getButtonClickSound().play();
                    Blowni.getPrefs().putBoolean("soundFxOn", true);
                    Blowni.getPrefs().flush();
                    Blowni.setSoundEffects(Blowni.getPrefs().getBoolean("soundFxOn"));

                    effectsButton = new Image(new Texture("soundOn.png"));
                }

                table.remove();
                generateTable();
            }

            /* Checks if resetbutton is clicked. */
            if (detectButtonTouch(resetButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }

                resetClicked = true;
                table.remove();
                generateTable();
            }

            /* Checks if the confirmationbutton for reset is clicked. */
            if (detectButtonTouch(confirmationButton, touchX, touchY) && resetClicked) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }

                resetClicked = false;
                Blowni.getPrefs().putInteger("currentHighScore", 0);
                Blowni.getPrefs().flush();

                table.remove();
                generateTable();
            }

            /* Checks if backbutton is clicked. */
            if (detectButtonTouch(backButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }

                gsm.pop();
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
