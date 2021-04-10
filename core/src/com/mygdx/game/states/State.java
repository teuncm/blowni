package com.mygdx.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * This class is the state abstraction. Every state is able to update the state of the entities,
 * render the entities and dispose of itself.
 */

public abstract class State {
    protected OrthographicCamera cam;
    protected GameStateManager gsm;
    protected static int scoreInt;

    /* Setup the new state. Every state contains a reference to the state manager and its own
    perspective projection camera.
     */
    protected State(GameStateManager gsm, SpriteBatch sb) {
        this.gsm = gsm;
        cam = new OrthographicCamera();
    }

    /* Checks if a touch at touchX and touchY hits the image. */
    protected boolean detectButtonTouch(Image image, float touchX, float touchY) {
        return touchX >= image.getX()
               && touchX <= image.getX() + image.getImageWidth()
               && touchY >= image.getY()
               && touchY <= image.getY() + image.getImageHeight();
    }

    /* Handle user input, if any. */
    protected abstract void handleInput();
    /* Update the state of the entities in the game. */
    public abstract void update(float dt);
    /* Render all entities to the screen. */
    public abstract void render();
    /* Dispose of the current state and its entities. */
    public abstract void dispose();
}
