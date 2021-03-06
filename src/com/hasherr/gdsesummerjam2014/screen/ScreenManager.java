package com.hasherr.gdsesummerjam2014.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hasherr.gdsesummerjam2014.entity.path.PathType;
import com.hasherr.gdsesummerjam2014.screen.display.CreditsScreen;
import com.hasherr.gdsesummerjam2014.screen.display.DeathScreen;
import com.hasherr.gdsesummerjam2014.screen.display.PauseScreen;
import com.hasherr.gdsesummerjam2014.screen.display.StartScreen;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Evan
 * Date: 7/4/14
 */
public class ScreenManager
{
    private ArrayList<Screen> currentScreens;
    private ArrayList<PathType> levelCycle = new ArrayList<>();
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private boolean isPaused;

    public ScreenManager(SpriteBatch batch, OrthographicCamera camera)
    {
        currentScreens = new ArrayList<>();
        currentScreens.add(new StartScreen(batch, camera, this));

        this.batch = batch;
        this.camera = camera;

        levelCycle.add(PathType.ROAD);
        levelCycle.add(PathType.WATER);

        isPaused = false;
    }

    public void render()
    {
        for (Screen screen : currentScreens)
        {
            screen.render();
        }
    }

    public void update()
    {
        Screen lastScreen = getLastScreen();
        lastScreen.update();


        if (lastScreen instanceof GameScreen)
            doGameScreenUpdate((GameScreen)lastScreen);

    }

    private void doGameScreenUpdate(GameScreen lastScreen)
    {
        int score = lastScreen.getScore();
        if (Gdx.input.isKeyPressed(Keys.ESCAPE))
        {
            pauseScreen();
        }

        if (lastScreen.isDisposable)
        {
            currentScreens.add(new DeathScreen(batch, camera, this, score));
        }

        if (lastScreen.isReadyForSwitch())
        {
            score++;
            currentScreens.clear();
            if (lastScreen.levelType == PathType.WATER)
                currentScreens.add(new GameScreen(batch, camera, PathType.ROAD));
            else
                currentScreens.add(new GameScreen(batch, camera, PathType.WATER));
            ((GameScreen)currentScreens.get(currentScreens.size() - 1)).setScore(score);
        }
    }

    public void startGame()
    {
        currentScreens.clear();
        currentScreens.add(new GameScreen(batch, camera, PathType.WATER));
    }

    public void showCredits()
    {
        currentScreens.clear();
        currentScreens.add(new CreditsScreen(batch, camera, this));
    }

    public void showTitle()
    {
        currentScreens.clear();
        currentScreens.add(new StartScreen(batch, camera, this));
    }

    public void pauseScreen()
    {
        if (getLastScreen() instanceof GameScreen)
        {
            currentScreens.add(new PauseScreen(batch, camera, this));
        }
    }

    public void unpause()
    {
        currentScreens.remove(getLastScreen());
    }

    private Screen getLastScreen()
    {
        return currentScreens.get(currentScreens.size() - 1);
    }

    public void clear()
    {
        currentScreens.clear();
    }
}
