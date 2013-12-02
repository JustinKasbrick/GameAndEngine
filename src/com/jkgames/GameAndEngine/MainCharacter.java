package com.jkgames.GameAndEngine;

import com.badlogic.gdx.math.Vector2;

public class MainCharacter {

    private Vector2 jumpVelocity;

    public MainCharacter()
    {
        jumpVelocity = new Vector2(0, 9);
    }
    public Vector2 getJumpVelocity()
    {
        return jumpVelocity;
    }
}
