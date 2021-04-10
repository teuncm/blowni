package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.Blowni;
import com.mygdx.game.audio.AudioBuffer;
import com.mygdx.game.objects.Balloon;
import com.mygdx.game.objects.Coin;
import com.mygdx.game.objects.Spike;
import com.mygdx.game.objects.SpikeBall;
import com.mygdx.game.objects.Splinter;

import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.floor;

/**
 * This class is the primary play state of the game.
 */

public class PlayState extends State{
    private static final int SPAWN_START = 2000;
    private static final float CAMERA_SPEED_CAP = 650f;
    private static final int COIN_DISTANCE = 2000;
    private static final int SPIKE_DISTANCE = 600;
    private static final int SPIKEBALL_DISTANCE = 1300;
    private static final int SPLINTER_DISTANCE = 2200;
    private static final int SPLINTER_OFFSET_Y = 600;

    private ArrayList<Spike> spikes;
    private ArrayList<Coin> coins;
    private ArrayList<SpikeBall> spikeBalls;
    private ArrayList<Splinter> splinters;

    private float cameraSpeed;
    private int nextCoinHeight;
    private int nextSpikeHeight;
    private int nextSpikeBallHeight;
    private int nextSplinterHeight;

    private Texture background;
    private Balloon balloon;
    private float score;
    private Label scoreLabel;

    private SpriteBatch spriteBatch;
    private Table table;
    private Stage stage;
    private int ready;
    private boolean start;
    private Label readyString;
    private Label goString;

    public static Thread audioThread;

    /* Initialize all of the playing field variables. */
    public PlayState(GameStateManager gsm, SpriteBatch sb) {
        super(gsm, sb);

        spriteBatch = sb;

        Blowni.setPlayState(true);

        if(Blowni.isBackGroundMusic()) {
            Blowni.getBackgroundMusic().pause();
            Blowni.getPlayStateMusic().stop();
            Blowni.getPlayStateMusic().play();
        }

        cameraSpeed = 400f;
        nextCoinHeight = SPAWN_START;
        nextSpikeHeight = SPAWN_START;
        nextSpikeBallHeight = SPAWN_START;
        nextSplinterHeight = SPAWN_START;
        score = 0f;
        scoreInt = 0;
        background = new Texture("background.png");
        ready = 0;
        start = false;

        readyString = new Label("Ready?", new Label.LabelStyle(Blowni.getFont(), Color.BLACK));
        goString = new Label("Go!", new Label.LabelStyle(Blowni.getFont(), Color.BLACK));

        balloon = new Balloon(Blowni.getWIDTH() / 2, 0);
        cam.setToOrtho(false, Blowni.getWIDTH(), Blowni.getHEIGHT());

        spikes = new ArrayList<Spike>();
        coins = new ArrayList<Coin>();
        spikeBalls = new ArrayList<SpikeBall>();
        splinters = new ArrayList<Splinter>();

        /* Launch a separate thread for audio processing. */
        audioThread = new Thread(new AudioBuffer());
        audioThread.start();

        Gdx.input.setCatchMenuKey(true);
        Gdx.input.setCatchBackKey(true);

        FitViewport viewport = new FitViewport(Blowni.getWIDTH(), Blowni.getHEIGHT(), new OrthographicCamera());
        stage = new Stage(viewport, spriteBatch);

        scoreLabel = new Label(Integer.toString(scoreInt), new Label.LabelStyle(Blowni.getFont(), Color.BLACK));
        generateTable();
    }

    private void generateTable() {
        table = new Table();
        table.top();
        table.setFillParent(true);

        if(ready == 0) {
            table.add(readyString).expandY();
        }

        if(ready == 1) {
            table.add(goString).expandY();
        }

        if(ready == 2) {
            table.add(scoreLabel).padTop(100);
        }

        stage.addActor(table);
    }

    @Override
    public void handleInput() {
        /* Detect touches to activate the pause menu. */
        if (Gdx.input.justTouched()
                || Gdx.input.isKeyPressed(Input.Keys.MENU)
                || Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            if(Blowni.isSoundEffects()) {
                Blowni.getButtonClickSound().play();
            }
            gsm.push(new PauseState(gsm, spriteBatch));
        }

        /* Use the accelerometer to moveX the player. */
        if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            float moveX = Gdx.input.getAccelerometerX();

            balloon.moveX(moveX * 4);
        }

        /* Detect whether the user is blowing. */
        if (AudioBuffer.isBlowing()) {
            balloon.moveY();
        }
    }

    @Override
    public void update(float dt) {
        if(scoreInt == 2 && !start) {
            ready = 1;
            table.remove();
            generateTable();
        }

        if(scoreInt == 3 && !start) {
            start = true;
            ready = 2;
            score = 0f;
            table.remove();
            generateTable();
        }

        if(ready == 2) {
            handleInput();
            balloon.update(dt);
            spawnEntities();

            cam.position.y += dt * cameraSpeed;

            if(cameraSpeed < CAMERA_SPEED_CAP) {
                cameraSpeed += 5f * dt;
                balloon.setMaxVelocity(cameraSpeed);
            }

            updateSpikes(dt);
            updateCoins(dt);
            updateSpikeBalls(dt);
            updateSplinters(dt);
            checkOutOfScreen(dt);
        }

        score += dt;
        scoreInt = floor(score);
        scoreLabel.setText(Integer.toString(scoreInt));

        /* Scroll the background, creating a parallax effect. */
        Blowni.bgIndex += 4;
        Blowni.bgIndex %= 7680;

        cam.update();
    }

    /* Spawn the entities in the game. */
    public void spawnEntities() {
        /* Spawn a new coin after a certain distance. */
        if (cam.position.y > nextCoinHeight) {
            coins.add(new Coin(cam.position.y + Blowni.getHEIGHT()));

            nextCoinHeight += COIN_DISTANCE;
        }

        /* Spawn new spike after a certain distance. */
        if (cam.position.y > nextSpikeHeight) {
            spikes.add(new Spike(cam.position.y + Blowni.getHEIGHT()));

            nextSpikeHeight += SPIKE_DISTANCE;
        }

        /* Spawn new spike balls after a certain distance. */
        if (cam.position.y > nextSpikeBallHeight && scoreInt > 50) {
            spikeBalls.add(new SpikeBall(cam.position.y + Blowni.getHEIGHT()));

            nextSpikeBallHeight = (int)cam.position.y + SPIKEBALL_DISTANCE;
        }

        /* Spawn new splinters after a certain distance. */
        if (cam.position.y > nextSplinterHeight && scoreInt > 100) {
            splinters.add(new Splinter(cam.position.y + SPLINTER_OFFSET_Y));

            nextSplinterHeight = (int)cam.position.y + SPLINTER_DISTANCE;
        }
    }

    /* Update all spikes. */
    public void updateSpikes(float dt) {
        for(int i = 0; i < spikes.size(); i++) {
            Spike spike = spikes.get(i);
            spike.update(dt);

            /* Remove the spike if it is off the screen. */
            if(cam.position.y - spike.getPosition().y > Blowni.getHEIGHT()) {
                spike.getTexture().dispose();
                spikes.remove(i);

                i--;
                break;
            }

            /* Check for collisions with the player. */
            if(spike.collide(balloon.getBounds())) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getBalloonPopSound().play();
                }
                cam.update();
                gsm.set(new GameOverState(gsm, spriteBatch));
            }
        }
    }

    /* Update all coins. */
    public void updateCoins(float dt) {
        for(int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);

            /* Remove the coin if it is off the screen. */
            if(cam.position.y - coin.getPosition().y > Blowni.getHEIGHT()) {
                coin.getTexture().dispose();
                coins.remove(i);

                i--;
                break;
            }

            /* Check for collisions with the player. */
            if(coin.collide(balloon.getBounds())) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getCoinSound().play();
                }
                score += 5;

                coin.getTexture().dispose();
                coins.remove(i);

                i--;
                break;
            }
        }
    }

    /* Update all spike balls. */
    public void updateSpikeBalls(float dt) {
        for(int i = 0; i < spikeBalls.size(); i++) {
            SpikeBall spikeBall = spikeBalls.get(i);
            spikeBall.update(dt);

            /* Remove the spike ball if it is off the screen. */
            if(cam.position.y - spikeBall.getPosition().y > Blowni.getHEIGHT()) {
                spikeBall.getTexture().dispose();
                spikeBalls.remove(i);

                i--;
                break;
            }

            /* Check for collisions with the player. */
            if(spikeBall.collide(balloon.getBounds())) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getBalloonPopSound().play();
                }
                cam.update();
                gsm.set(new GameOverState(gsm, spriteBatch));
            }
        }
    }

    /* Update all splinters. */
    public void updateSplinters(float dt) {
        for(int i = 0; i < splinters.size(); i++) {
            Splinter splinter = splinters.get(i);
            splinter.update(dt);

            /* Remove the splinter if it is off the screen. */
            if(cam.position.y - splinter.getPosition().y > Blowni.getHEIGHT()) {
                splinter.getTexture().dispose();
                splinters.remove(i);

                i--;
                break;
            }

            /* Check for collisions with the player. */
            if(splinter.collide(balloon.getBounds())) {
                if(Blowni.isSoundEffects()) {
                    Blowni.getBalloonPopSound().play();
                }
                cam.update();
                gsm.set(new GameOverState(gsm, spriteBatch));
            }
        }
    }

    /* Check whether or not the player is out of the screen. */
    public void checkOutOfScreen(float dt) {
        if(balloon.getPosition().y + balloon.getTexture().getHeight() < cam.position.y - cam.viewportHeight / 2 || balloon.getBounds().y > cam.position.y + cam.viewportHeight / 2) {
            if(Blowni.isSoundEffects()) {
                Blowni.getBalloonPopSound().play();
            }
            gsm.set(new GameOverState(gsm, spriteBatch));
        }
    }

    @Override
    public void render() {
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();

        spriteBatch.draw(background, 0, cam.position.y - (cam.viewportHeight / 2) - Blowni.bgIndex, Blowni.getWIDTH(), 9600);
        if(ready == 2) {
            spriteBatch.draw(balloon.getTexture(), balloon.getPosition().x, balloon.getPosition().y);

        /* Draw all game objects. */
            for(Spike spike : spikes) {
                spriteBatch.draw(spike.getTexture(), spike.getPosition().x, spike.getPosition().y);
            }
            for(SpikeBall spikeBall : spikeBalls) {
                spriteBatch.draw(spikeBall.getTexture(), spikeBall.getPosition().x, spikeBall.getPosition().y);
            }
            for(Coin coin : coins) {
                spriteBatch.draw(coin.getTexture(), coin.getPosition().x, coin.getPosition().y);
            }
            for(Splinter splinter : splinters) {
                spriteBatch.draw(splinter.getTexture(), splinter.getPosition().x, splinter.getPosition().y);
            }
        }

        spriteBatch.end();

        spriteBatch.setProjectionMatrix(stage.getCamera().combined);
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        balloon.getTexture().dispose();

        /* Garbage collect all game objects. */
        for(Spike spike : spikes) {
            spike.getTexture().dispose();
        }
        for(SpikeBall spikeBall : spikeBalls) {
            spikeBall.getTexture().dispose();
        }
        for(Coin coin : coins) {
            coin.getTexture().dispose();
        }
        for(Splinter splinter : splinters) {
            splinter.getTexture().dispose();
        }

        audioThread.interrupt();
    }
}
