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
 * This class contains the main menu of the game.
 */

public class MenuState extends State {
    private Texture background;

    private Image logo;
    private Image playButton;
    private Image leftArrow;
    private Image rightArrow;
    private Image currentBalloon;
    private Image optionButton;
    private Image helpButton;

    private SpriteBatch spriteBatch;
    private Table table;
    private Stage stage;

    public MenuState(GameStateManager gsm, SpriteBatch sb) {
        super(gsm, sb);

        spriteBatch = sb;

        Blowni.setPlayState(false);

        background = new Texture("background.png");
        logo = new Image(new Texture("blowni_logo.png"));
        leftArrow = new Image(new Texture("left_arrow.png"));
        rightArrow = new Image(new Texture("right_arrow.png"));
        currentBalloon = new Image(new Texture("balloon" + Blowni.getBalloonColor() + ".png"));
        playButton = new Image(new Texture("play.png"));
        optionButton = new Image(new Texture("options.png"));
        helpButton = new Image(new Texture("help.png"));

        cam.setToOrtho(false, Blowni.getWIDTH(), Blowni.getHEIGHT());

        FitViewport viewport = new FitViewport(Blowni.getWIDTH(), Blowni.getHEIGHT(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        generateTable();
    }

    private void generateTable() {
        table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(logo).padTop(100).expandX().colspan(6);
        table.row();
        table.add(leftArrow).padTop(100).padRight(15).expandX().colspan(2).right();
        table.add(currentBalloon).padTop(100).colspan(2);
        table.add(rightArrow).padTop(100).padLeft(15).expandX().colspan(2).left();
        table.row();
        table.add(playButton).padTop(150).expandX().colspan(6);
        table.row();
        table.add(helpButton).padTop(30).padRight(15).expandX().colspan(3).right();
        table.add(optionButton).padTop(30).padLeft(15).expandX().colspan(3).left();

        stage.addActor(table);
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX() * Blowni.getScaleX();
            float touchY = 1920 - Gdx.input.getY() * Blowni.getScaleY();

            /* Cycle through available balloons. */
            if (detectButtonTouch(leftArrow, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                Blowni.setBalloonColor((Blowni.getBalloonColor() == 5) ? 1 : Blowni.getBalloonColor() + 1);

                currentBalloon = new Image(new Texture("balloon" + Integer.toString(Blowni.getBalloonColor()) + ".png"));

                table.remove();
                generateTable();
            }

            else if (detectButtonTouch(rightArrow, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                Blowni.setBalloonColor((Blowni.getBalloonColor() == 1) ? 5 : Blowni.getBalloonColor() - 1);

                currentBalloon = new Image(new Texture("balloon" + Integer.toString(Blowni.getBalloonColor()) + ".png"));

                table.remove();
                generateTable();
            }

            /* Detect play button push. */
            else if (detectButtonTouch(playButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.push(new PlayState(gsm, spriteBatch));
            }

            /* Detect score button push. */
            else if (detectButtonTouch(helpButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.push(new HelpState(gsm, spriteBatch));
            }

            /* Detect option button push. */
            else if (detectButtonTouch(optionButton, touchX, touchY)) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getButtonClickSound().play();
                }
                gsm.push(new OptionsState(gsm, spriteBatch, false));
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
