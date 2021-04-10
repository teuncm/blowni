package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Blowni;

import java.util.Random;

/**
 * This class contains the spike ball implementation. A spike ball is yet another obstacle for
 * the player to avoid.
 */

public class SpikeBall {
    private static final int INITIAL_VELOCITY_Y = 600;
    private static final int RANDOM_X = 301;
    private static final int GRAVITY_Y = -7;

    private Texture texture;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private Random random;

    public SpikeBall(float y) {
        texture = new Texture("spikeball.png");
        random = new Random();

        /* Set the spawn position to be outside of the screen. */
        position = new Vector2(random.nextInt(Blowni.getWIDTH() - texture.getWidth()), y);
        velocity = new Vector2(random.nextInt(RANDOM_X) - RANDOM_X / 2, INITIAL_VELOCITY_Y);
        bounds = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
    }

    public void update(float dt) {
        velocity.add(0, GRAVITY_Y);
        velocity.scl(dt);
        position.add(velocity.x, velocity.y);
        velocity.scl(1.0f/dt);

        bounds.setPosition(position.x, position.y);
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean collide(Rectangle balloon) {
        return balloon.overlaps(bounds);
    }
}
