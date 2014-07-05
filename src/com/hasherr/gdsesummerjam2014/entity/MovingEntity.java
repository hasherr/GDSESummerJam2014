package com.hasherr.gdsesummerjam2014.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Evan on 7/4/2014.
 */
public abstract class MovingEntity extends Entity
{

    public MovingEntity(String spritePath, Vector2 position)
    {
        super(spritePath, position);
    }

    @Override
    public void update()
    {
        super.update();
    }

    @Override
    public void render(SpriteBatch batch)
    {
        batch.draw(sprite, position.x, position.y, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 0, 0, 64, 64, false, false);
    }
}
