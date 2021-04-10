package com.mygdx.game.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Blowni;

/**
 * This class contains the balloon implementation. The balloon is the player object.
 */

public class Balloon {
    private static final int INITIAL_VELOCITY = 800;
    private static final int INITIAL_POSITION_Y = 400;
    private static final int HITBOX_OFFSET = 190;
    private static final float ACCELERATION_Y = 18;

    private float gravity;
    private float maxVelocity;

    private Vector3 position;
    private Vector3 velocity;
    private Texture texture;
    private Rectangle bounds;

    public Balloon(int x, int y) {
        maxVelocity = 800;
        gravity = -6f;
        position = new Vector3(x, INITIAL_POSITION_Y, 0);
        velocity = new Vector3(0, INITIAL_VELOCITY, 0);

        texture = new Texture("balloon" + Blowni.getBalloonColor() + ".png");
        bounds = new Rectangle(position.x, position.y + (texture.getHeight() - HITBOX_OFFSET), texture.getWidth(), HITBOX_OFFSET);
    }

    public void update(float dt) {
        if(velocity.y > -20) {
            velocity.add(0, gravity, 0);
        }

        velocity.scl(dt);
        position.add(velocity.x, velocity.y, 0);
        velocity.scl(1.0f/dt);

        if (position.x < 0) {
            position.x = 0;
        } else if (position.x > Blowni.getWIDTH() - texture.getWidth()) {
            position.x = Blowni.getWIDTH() - texture.getWidth();
        }

        bounds.setPosition(position.x, position.y + (texture.getHeight() - HITBOX_OFFSET));
    }

    public void setMaxVelocity(float cameraSpeed) {
        maxVelocity = cameraSpeed * 2;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Texture getTexture() {
        return texture;
    }

    public void moveX(float moveX) {
        position.x -= moveX;
    }

    public void moveY() {
        if(velocity.y < maxVelocity) {
            velocity.add(0, ACCELERATION_Y, 0);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }


}
