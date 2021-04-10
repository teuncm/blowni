package com.mygdx.game.states;

import java.util.Stack;

/**
 * This class contains the game state manager. The game state manager holds the current states
 * of the game and is the primary mechanism for controlling game events.
 */

public class GameStateManager {
    private Stack<State> states;

    public GameStateManager() {
        states = new Stack<State>();
    }

    /* Push a new state. Useful for pause menus */
    public void push(State state) {
        states.push(state);
    }

    /* Pop and cleanup the current state. */
    public void pop() {
        states.pop().dispose();
    }

    /* Swap between two states. */
    public void set(State state) {
        this.pop();
        this.push(state);
    }

    /* Update the current state using the given delta time as reference. */
    public void update(float dt) {
        states.peek().update(dt);
    }

    /* Render the given sprite batch on the current state. */
    public void render() {
        states.peek().render();
    }
}
