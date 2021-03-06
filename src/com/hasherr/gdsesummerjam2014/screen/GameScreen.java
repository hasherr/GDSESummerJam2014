package com.hasherr.gdsesummerjam2014.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.hasherr.gdsesummerjam2014.core.InputManager;
import com.hasherr.gdsesummerjam2014.entity.Entity;
import com.hasherr.gdsesummerjam2014.entity.Player;
import com.hasherr.gdsesummerjam2014.entity.path.PathType;
import com.hasherr.gdsesummerjam2014.level.Level;

/**
 * Created with IntelliJ IDEA.
 * User: Evan
 * Date: 7/4/14
 */
public class GameScreen extends Screen
{
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Level level;
    private Player player;
    private InputManager inputManager;
    public PathType levelType;
    private boolean isReadyForSwitch;
    private BitmapFont scoreFont;
    private boolean turtleModeActivated;

    public GameScreen(SpriteBatch batch, OrthographicCamera camera, PathType levelType)
    {
        this.batch = batch;
        this.camera = camera;
        inputManager = new InputManager();
        isReadyForSwitch = false;
        this.levelType = levelType;
        turtleModeActivated = false;

        determineMap();
        player = new Player("Sprites/Player/face_down.png", new Vector2(5, 0));

        generateFont("Fonts/Spoutnik.ttf");
    }

    private void determineMap()
    {
        if (levelType == PathType.WATER)
            level = new Level("Maps/river_map.tmx", batch, levelType);
        else
            level = new Level("Maps/road_map.tmx", batch, levelType);
    }

    private void generateFont(String fontPath)
    {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        scoreFont = generator.generateFont(100);
    }

    @Override
    public void render()
    {
        level.drawLevel(camera);
        player.render(batch);
        renderBitMapTexts();
        batch.end();
    }

    private void renderBitMapTexts()
    {
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(camera.combined);
        scoreFont.draw(batch, Integer.toString(player.getScore()), 50, 6*64);
        if (turtleModeActivated)
        {
            scoreFont.draw(batch, "TURTLE MODE ", 100, 300f);
            scoreFont.draw(batch, "  ACTIVATED", 100, 190f);
        }
        camera.setToOrtho(false, Gdx.graphics.getWidth() / (Gdx.graphics.getHeight() / 9f),
                Gdx.graphics.getHeight() / (Gdx.graphics.getHeight() / 9f));
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void update()
    {
        handleLevelHazards();
        level.updatePaths();
        player.update();
        inputManager.handleInput(player);
        handlePlayerLevelBounds();
        handleLevelHazards();
        handleLevelTypeSwitching();
        handlePowerup();
    }

    private void handlePowerup()
    {
        if (level.getPowerup().boundingBox.overlaps(player.boundingBox))
        {
            player.setTurtle(true);
            turtleModeActivated = true;
            level.powerupActivated();
            for (Entity e: level.getEntities())
            {
                e.timeConstant = 0.2f;
            }
        }
    }

    private void handleLevelHazards()
    {
        if (levelType == PathType.ROAD)
            handleRoadHazards();
        else
            handleWaterHazards();
    }

    private void handleWaterHazards()
    {
        boolean isOnPlatform = false;
        boolean safe = false;

        for (Entity e: level.getEntities())
        {
            if (player.position.x < e.position.x + 1f && player.position.x + 1f > e.position.x && player.position.y
                    < e.position.y + 1f && player.position.y + 1f > e.position.y)

            {
                isOnPlatform = true;
                player.velocity.x = e.velocity.x * e.timeConstant;
            }
        }

        int[] hazardY = { 1, 2, 3, 5, 6, 7 };
        for (int y: hazardY)
        {
            if ((player.position.x < 0f || player.position.x > 15f) && player.position.y == y)
                isDisposable = true;
        }

        if (isOnPlatform)
            safe = true;
        if (!isOnPlatform)
        {
            int[] safeY = { 0,  4, 8 };
            for (int x = 0; x < 18; x++)
            {
                for (int y: safeY)
                {
                    if ((int)player.position.x == x && (int)player.position.y == y)
                    {
                        safe = true;
                    }
                }
            }
            if (!safe)
            {
                isDisposable = true;
            }
        }
    }

    private void handleRoadHazards()
    {
        for (Entity e: level.getEntities())
        {
            if (player.boundingBox.overlaps(e.boundingBox))
            {
                isDisposable = true;
            }
        }
    }

    private void handlePlayerLevelBounds()
    {
        if (player.position.y < 0f)
            player.position.y = 0f;
        if (player.position.y == 0f && player.position.x < 0f)
        {
            player.position.set(0f, 0f);
        }
        if (player.position.y == 0f && player.position.x > 15f)
        {
            player.position.set(15f, 0f);
        }
        if (player.position.y == 4f && player.position.x < 0f)
        {
            player.position.set(0f, 4f);
        }
        if (player.position.y == 4f && player.position.x > 15f)
        {
            player.position.set(15f, 4f);
        }
        if (player.position.y == 8f && player.position.x < 0f)
        {
            player.position.set(0f, 8f);
        }
        if (player.position.y == 8f && player.position.x > 15f)
        {
            player.position.set(15f, 8f);
        }

    }

    private void handleLevelTypeSwitching()
    {
        if (player.position.y > 8f)
            isReadyForSwitch = true;
    }

    public int getScore()
    {
        return player.getScore();
    }

    public void setScore(int score)
    {
        player.setScore(score);
    }

    public boolean isReadyForSwitch()
    {
        return isReadyForSwitch;
    }
}
