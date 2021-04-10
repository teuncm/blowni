package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Blowni;

import java.util.Random;

/**
 * This class contains the coin implementation. A coin is collectible by the player.
 */

public class Coin {
    private Texture texture;
    private Vector2 position;
    private Rectangle bounds;

    public Coin(float y) {
        texture = new Texture("coin.png");
        Random random = new Random();

        /* Set the spawn position to be outside of the screen. */
        position = new Vector2(random.nextInt(Blowni.getWIDTH() - texture.getWidth()), y);
        bounds = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
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
