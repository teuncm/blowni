package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Blowni;

import java.util.Random;

/**
 * This class contains the spike implementation. The spikes fall down on the play field.
 */
public class Spike {
    private static final int WIDTH_SPIKE = 160;
    private static final int OFFSET_X = Blowni.getWIDTH() - WIDTH_SPIKE;
    private static final int WIDTH_TOP = 50;
    private static final int HITBOX_OFFSET = 20;
    private static final int GRAVITY_Y = -3;
    private static final int INITIAL_VELOCITY_Y = 50;

    private Texture texture;
    private Rectangle bounds;
    private Rectangle bounds2;
    private Vector2 position;
    private Vector2 velocity;

    public Spike(float y) {
        texture = new Texture("spike.png");
        Random random = new Random();

        position = new Vector2(random.nextInt(OFFSET_X), y);
        velocity = new Vector2(0, INITIAL_VELOCITY_Y);
        bounds = new Rectangle(position.x, position.y + (texture.getHeight() - HITBOX_OFFSET), texture.getWidth(), HITBOX_OFFSET);
        bounds2 = new Rectangle(position.x + (texture.getWidth() - WIDTH_TOP) / 2, position.y, WIDTH_TOP, HITBOX_OFFSET);
    }

    public void update(float dt) {
        velocity.add(0, GRAVITY_Y);
        velocity.scl(dt);
        position.add(velocity.x, velocity.y);
        velocity.scl(1.0f/dt);

        bounds.setPosition(position.x, position.y + (texture.getHeight() - HITBOX_OFFSET));
        bounds2.setPosition(position.x + (texture.getWidth() - WIDTH_TOP) / 2, position.y);
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean collide(Rectangle balloon) {
        return (balloon.overlaps(bounds) || balloon.overlaps(bounds2));
    }
}
