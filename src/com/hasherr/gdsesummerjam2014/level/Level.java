package com.hasherr.gdsesummerjam2014.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.hasherr.gdsesummerjam2014.core.SoundManager;
import com.hasherr.gdsesummerjam2014.entity.Entity;
import com.hasherr.gdsesummerjam2014.entity.Powerup;
import com.hasherr.gdsesummerjam2014.entity.path.Path;
import com.hasherr.gdsesummerjam2014.entity.path.PathType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Evan
 * Date: 7/4/14
 */
public class Level
{
    private TiledMap map;
    private OrthogonalTiledMapRenderer levelRenderer;
    private int[] pathHeights = { 1, 2, 3, 5, 6, 7 };
    private ArrayList<Path> levelPaths;
    private SpriteBatch batch;
    private Powerup powerup;
    private SoundManager soundManager;

    private Timer timer;
    private Task timerTask;
    private float timerInterval;

    public Level(String pathToMap, SpriteBatch batch, PathType pathType)
    {
        map = new TmxMapLoader().load(pathToMap);
        this.batch = batch;
        levelRenderer = new OrthogonalTiledMapRenderer(map, 1f / 64f, batch); // 1/64
        levelPaths = new ArrayList<>();
        powerup = createPowerup();

        for (int i: pathHeights)
        {
            levelPaths.add(new Path(i, pathType));
        }

        if (pathType == PathType.WATER)
            timerInterval = 1f;
        else
            timerInterval = 3f;

        initiateTimerElements();
    }

    private void initiateTimerElements()
    {
        timer = new Timer();
        timerTask = new Task()
        {
            @Override
            public void run()
            {
                for (Path p: levelPaths)
                {
                    p.generatePathEntity();
                }
            }
        };
        timer.scheduleTask(timerTask, 0f, timerInterval);
        timer.start();
    }

    public void drawLevel(OrthographicCamera camera)
    {
        levelRenderer.setView(camera);
        levelRenderer.render();
        batch.begin();

        for (Path p: levelPaths)
        {
            p.renderPathObjects(batch);
        }
        powerup.render(batch);
    }

    public void updatePaths()
    {
        for (Path p: levelPaths)
        {
            p.updatePathObjects();
        }
        powerup.update();
    }

    public ArrayList<Entity> getEntities()
    {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Path p: levelPaths)
        {
            for (Entity e: p.getPathEntities())
            {
                entities.add(e);
            }
        }
        return entities;
    }

    private Powerup createPowerup()
    {
        Random rand = new Random();
        int randX = 0 + rand.nextInt(17 - 0 + 1);
        int randY = 0 + rand.nextInt(8 - 0 + 1);
        return new Powerup(new Vector2(randX, randY));
    }

    public Powerup getPowerup()
    {
        return powerup;
    }

    public void powerupActivated()
    {
        Timer t = new Timer();
        timer.scheduleTask(new Task()
        {
            @Override
            public void run()
            {
                for (Entity e: getEntities())
                {
                    e.timeConstant = 0.4f;
                }
            }
        }, 0f, 4f, 1);
        timer.start();
        powerup.position.set(-500f, -500f); // Set it to nowhere.
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("Sounds/powerup.wav"));
        sound.play();
        for (Entity e: getEntities())
        {
            e.timeConstant = 1f;
        }
    }
}
