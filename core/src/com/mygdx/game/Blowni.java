package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.GameStateManager;
import com.badlogic.gdx.Preferences;
import com.mygdx.game.states.PauseState;
import com.mygdx.game.states.PlayState;


/**
 * This is the main class of the game. This class is the first
 * to be created after the AndroidLauncher.
 */

public class Blowni extends Game {
	private static final int WIDTH = 1080;
	private static final int HEIGHT = 1920;
    private static final String TITLE = "Blowni";

    private static int balloonColor;
    public static int bgIndex;

	private static float scaleX;
	private static float scaleY;

    private static boolean backGroundMusic;
    private static boolean soundEffects;
    private static boolean playState;

	private static Texture textTexture;
	private static BitmapFont font;

    private static Music backgroundMusic;
	private static Music playStateMusic;
	private static Music coinSound;
	private static Music buttonClickSound;
	private static Music balloonPopSound;

    private static Preferences prefs;

    private SpriteBatch batch;
	private GameStateManager gsm;

	@Override
	public void create () {
        balloonColor = 1;
        playState = false;

        /* - Creates a pref if the pref did not exist before.
		 * - Get preferences if the file already existed.
		 */
        prefs = Gdx.app.getPreferences("My_Prefs");

		backGroundMusic = Blowni.getPrefs().getBoolean("musicOn", true);
		soundEffects = Blowni.getPrefs().getBoolean("soundFxOn", true);

		scaleX = (float) WIDTH / (float) Gdx.graphics.getWidth();
		scaleY = (float) HEIGHT / (float) Gdx.graphics.getHeight();

        /* Use only one sprite batch during runtime for minimal overhead. */
		batch = new SpriteBatch();

        /* Use only one game state manager to pass around states during runtime. */
		gsm = new GameStateManager();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/bg_music/bg_menu/Rainy_Afternoon.mp3"));
        backgroundMusic.setLooping(true);
		playStateMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/bg_music/bg_play/August_Nights.mp3"));
		playStateMusic.setLooping(true);
		coinSound = Gdx.audio.newMusic(Gdx.files.internal("sound/soundeffects/coin.wav"));
		coinSound.setVolume(0.6f);
		buttonClickSound = Gdx.audio.newMusic(Gdx.files.internal("sound/soundeffects/buttonClick.wav"));
		balloonPopSound = Gdx.audio.newMusic(Gdx.files.internal("sound/soundeffects/balloonPop.wav"));


        /* - Check if background music was toggled off.
         */
        if (backGroundMusic) {
            backgroundMusic.play();
        }

		/* - Texture for text */
		textTexture = new Texture(Gdx.files.internal("font/MINECRAFT.png"));

		font = new BitmapFont(Gdx.files.internal("font/MINECRAFT.fnt"), new TextureRegion(textTexture), false);
		font.setColor(Color.BLACK);

        /* Push the initial state. */
		gsm.push(new MenuState(gsm, batch));
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}

    @Override
    public void pause() {
        if (playState) {
            PlayState.audioThread.interrupt();
            gsm.push(new PauseState(gsm, batch));
        }
        super.pause();
    }

	/* Getter for states/ other files to get access to the preferences. */
	public static Preferences getPrefs() {
		return prefs;
	}

	public static void pauseMusic() {
		if(playStateMusic.isPlaying()) {
			playStateMusic.pause();
		}
		else {
			backgroundMusic.pause();
		}
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static float getScaleX() {
        return scaleX;
    }

    public static float getScaleY() {
        return scaleY;
    }

    public static BitmapFont getFont() {
        return font;
    }

    public static int getBalloonColor() {
        return balloonColor;
    }

    public static boolean isBackGroundMusic() {
        return backGroundMusic;
    }

    public static boolean isSoundEffects() {
        return soundEffects;
    }

    public static String getTITLE() {
        return TITLE;
    }

    public static Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public static Music getPlayStateMusic() {
        return playStateMusic;
    }

    public static Music getCoinSound() {
        return coinSound;
    }

    public static Music getButtonClickSound() {
        return buttonClickSound;
    }

    public static Music getBalloonPopSound() {
        return balloonPopSound;
    }

    public static void setPlayState(boolean playState) {
        Blowni.playState = playState;
    }

    public static void setBalloonColor(int balloonColor) {
        Blowni.balloonColor = balloonColor;
    }

    public static void setBackGroundMusic(boolean backGroundMusic) {
        Blowni.backGroundMusic = backGroundMusic;
    }

    public static void setSoundEffects(boolean soundEffects) {
        Blowni.soundEffects = soundEffects;
    }
}
