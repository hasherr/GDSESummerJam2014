package com.hasherr.gdsesummerjam2014.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Evan on 7/4/2014.
 */
public abstract class MovingEntity extends Entity
{
    @Override
    public void render(SpriteBatch batch)
    {
        batch.draw(sprite, position.x, position.y, );
    }
}
