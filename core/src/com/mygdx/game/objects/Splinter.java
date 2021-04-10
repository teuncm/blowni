package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Blowni;

import java.util.Random;

/**
 * This class contains the splinter implementation. Splinters fly across the screen and kill the
 * player.
 */

public class Splinter {
    private static final int VELOCITY_X = 700;
    private static final int GRAVITY_Y = -2;
    private static final int HITBOX_OFFSET = 14;

    private Texture texture;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;

    public Splinter(float y) {
        texture = new Texture("splinter.png");
        Random random = new Random();

        /* Set the spawn position to be outside of the screen. */
        int direction = random.nextInt(2);
        velocity = new Vector2(VELOCITY_X, 0);

        if(direction == 0) {
            position = new Vector2(- texture.getWidth(), y);
        } else {
            position = new Vector2(Blowni.getWIDTH() + texture.getWidth(), y);
            velocity.x = - velocity.x;
        }

        bounds = new Rectangle(position.x, position.y + (texture.getHeight() - HITBOX_OFFSET), texture.getWidth(), HITBOX_OFFSET);
    }

    public void update(float dt) {
        velocity.add(0, GRAVITY_Y);
        velocity.scl(dt);
        position.add(velocity.x, velocity.y);
        velocity.scl(1.0f/dt);

        bounds.setPosition(position.x, position.y + (texture.getHeight() - HITBOX_OFFSET));
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
