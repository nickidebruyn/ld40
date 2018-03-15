/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.screens;

import com.bruynhuis.galago.games.basic.BasicGameListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import za.co.bruynhuis.blockout.MainApplication;
import za.co.bruynhuis.blockout.game.Game;
import za.co.bruynhuis.blockout.game.Player;
import za.co.bruynhuis.blockout.ui.HelpPanel;
import za.co.bruynhuis.blockout.ui.PauseDialog;
import za.co.bruynhuis.blockout.ui.ScorePanel;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements BasicGameListener, PickListener {

    private MainApplication mainApplication;
    private TouchPickListener touchPickListener;
    private ScorePanel scorePanel;
    private PauseDialog pauseDialog;
    private Game game;
    private Player player;

    private boolean gameOverAction = false;
    private HelpPanel helpPanel;
    private Timer startTimer = new Timer(80);
    private Label bodyLabel;
    private Label fpsLabel;

    private ControlButton statsButton;

    private boolean updateValues = false;
    private boolean reviveGame = false;
    private boolean gameWasRevived = false;

    @Override
    protected void init() {

        mainApplication = (MainApplication) baseApplication;

        scorePanel = new ScorePanel(hudPanel, true, false);
        scorePanel.centerTop(0, 0);

        helpPanel = new HelpPanel(hudPanel);
        helpPanel.center();

        pauseDialog = new PauseDialog(window);
        pauseDialog.addResumeButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doResumeGameAction();
                }
            }
        });

        pauseDialog.addRetryButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doRestartGameAction();
                }
            }
        });

        pauseDialog.addMenuButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    closeGame();
                    showPreviousScreen();
                }
            }
        });

        
        
        bodyLabel = new Label(hudPanel, "Bodies: 0", 12);
        bodyLabel.setTextColor(ColorRGBA.Gray);
        bodyLabel.leftTop(1, 70);
        bodyLabel.setAlignment(TextAlign.LEFT);

        fpsLabel = new Label(hudPanel, "Fps: 0", 12);
        fpsLabel.setTextColor(ColorRGBA.Gray);
        fpsLabel.leftTop(1, 85);
        fpsLabel.setAlignment(TextAlign.LEFT);


        statsButton = new ControlButton(hudPanel, "statsbutton", 50, 50);
        statsButton.centerTop(0, 0);
        statsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {

                    bodyLabel.setVisible(!bodyLabel.isVisible());
                    fpsLabel.setVisible(!fpsLabel.isVisible());

                }
            }
        });


        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);

    }

    @Override
    protected void load() {

        gameOverAction = false;

        if (!reviveGame) {
            scorePanel.updateScore(0);

            game = new Game(mainApplication, rootNode, 5);
            game.load();

            player = new Player(game);
            player.load();

            game.addGameListener(this);
        }


        camera.setLocation(new Vector3f(0, 0.4f, -10));

    }

    @Override
    protected void show() {
        setPreviousScreen("menu");

        if (!reviveGame) {
            if (mainApplication.isShowHelp()) {
                helpPanel.show("Touch screen to shoot.");
                if (mainApplication.getGameSaves().getGameData().getGamesPlayed() > 5) {
                    mainApplication.setShowHelp(false);
                }
            }
            
            mainApplication.showAds(MainApplication.ADMOB, false);
            startTimer.start();
            
        } else {
            touchPickListener.registerWithInput(inputManager);
        }

        scorePanel.show();        

        //debugging
        bodyLabel.setVisible(false);
        fpsLabel.setVisible(false);

    }
    
    public void closeGame() {
        game.close();
        reviveGame = false;
        gameWasRevived = false;
    }
    
    public void reviveGame() {
        gameWasRevived = true;
        reviveGame = true;
        game.reviveGame();
    }

    public boolean isGameWasRevived() {
        return gameWasRevived;
    }

    @Override
    protected void exit() {
        reviveGame = false;
        touchPickListener.unregisterInput();
//        mainApplication.getViewPort().removeProcessor(fpp);
        mainApplication.showAds(MainApplication.ADMOB, false);
    }

    public void doGameOver() {
        gameOverAction = true;

        //BEST SCORE CALCULATION
        int score = player.getScore();
        int oldScore = baseApplication.getGameSaves().getGameData().getScore();

        if (score > oldScore) {
            baseApplication.getGameSaves().getGameData().setScore(score);
            baseApplication.doAddHighscore(MainApplication.BEST_SCORE_LEADER_BOARD_ID, score);

            //Here we check for the achievements
            if (score >= 200) {
                baseApplication.doUnlockAchievement(MainApplication.ACHIEVEMENT_REACHED_200);

            } else if (score >= 100) {
                baseApplication.doUnlockAchievement(MainApplication.ACHIEVEMENT_REACHED_100);

            } else if (score >= 50) {
                baseApplication.doUnlockAchievement(MainApplication.ACHIEVEMENT_REACHED_50);

            } else if (score >= 20) {
                baseApplication.doUnlockAchievement(MainApplication.ACHIEVEMENT_REACHED_20);

            }
        }

        mainApplication.getGameoverScreen().setScore(score);
        mainApplication.getGameoverScreen().setBestScore(baseApplication.getGameSaves().getGameData().getScore());

        //GAMES PLAYED CALCULATION
        int gamesPlayed = mainApplication.getGameSaves().getGameData().getGamesPlayed();
        gamesPlayed++;
        mainApplication.getGameSaves().getGameData().setGamesPlayed(gamesPlayed);

        //Check if games played achievements should be unlocked
        if (gamesPlayed >= 200) {
            baseApplication.doUnlockAchievement(MainApplication.ACHIEVEMENT_PLAYED_200);

        } else if (gamesPlayed >= 100) {
            baseApplication.doUnlockAchievement(MainApplication.ACHIEVEMENT_PLAYED_100);

        } else if (gamesPlayed >= 20) {
            baseApplication.doUnlockAchievement(MainApplication.ACHIEVEMENT_PLAYED_20);

        }

        //Finally save the data
        baseApplication.getGameSaves().save();

        mainApplication.saveGameDataToCloud();


    }

    public void doGameCompleted() {
    }

    public void doScoreChanged(int score) {
        scorePanel.updateScore(score);
//        log("score=" + score);

        if (mainApplication.isShowHelp()) {
            if (score == 1) {
                helpPanel.show("Try to break the tiles.");

            } else if (score == 2) {
                helpPanel.show("When tiles reach floor, you die.");

            } else if (score == 3) {
                helpPanel.show("Collect extra balls and powers.");
                mainApplication.setShowHelp(false);

            }

        }

    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive() && !game.isPaused()) {


        }
    }

    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive() && !game.isPaused()) {

        }

    }

    @Override
    public void update(float tpf) {

        if (isActive() && updateValues) {
            scorePanel.updateValues();
            updateValues = false;
        }

        if (isActive()) {

            if (!game.isStarted()) {

                //Start the game if timer completes
                startTimer.update(tpf);
                if (startTimer.finished()) {
                    baseApplication.getSoundManager().playSound("levelup");
                    game.start(player);
                    touchPickListener.registerWithInput(inputManager);
                    startTimer.stop();
                }
            }

            //Do game update logic
            if (game.isStarted() && !game.isPaused()) {

                bodyLabel.setText("Bodies: " + mainApplication.getDyn4jAppState().getPhysicsSpace().getBodyCount());
                fpsLabel.setText("Fps: " + mainApplication.getFPS());


            }

            if (gameOverAction) {
                gameOverAction = false;
//                baseApplication.doVibrate();
//                cameraShaker.shake(0.08f, 60f);
                mainApplication.getSoundManager().playSound("gameover");
                showScreen("gameover");

//                //Determine if the ad must show.
//                if (mainApplication.getGameSaves().getGameData().getGamesPlayed() % FastMath.nextRandomInt(2, 3) == 0) {
//                    baseApplication.showAds(BaseApplication.ADMOB_INTERSTITIALS, true);
//                }
            }

        }
    }

    public void doPauseGame() {
        if (isActive() && isEnabled() && isInitialized() && game != null && !game.isPaused() && game.isStarted()) {
            baseApplication.doAnalyticsAction("PLAY", "Pause", "Pause button clicked.");
            game.pause();
            pauseDialog.show();
        }
    }

    /**
     * Called when the must be resumed
     */
    protected void doResumeGameAction() {
        if (isActive()) {
            baseApplication.doAnalyticsAction("PLAY", "Resume", "Resume button clicked.");
//            mainApplication.showAds(BaseApplication.ADMOB, true);

            baseApplication.getSoundManager().playSound("button");
            pauseDialog.hide();

        }
    }

    /**
     * Called when the game must be restarted
     */
    protected void doRestartGameAction() {
        if (isActive()) {
            closeGame();
            baseApplication.doAnalyticsAction("PLAY", "Restart", "Restart button clicked.");
            baseApplication.getSoundManager().playSound("button");
            showScreen("play");

        }
    }

    @Override
    protected void pause() {
        doPauseGame();
    }

    @Override
    protected void resume() {
    }

    @Override
    public void doEscape(boolean touchEvent) {
        if (isActive() && isEnabled() && isInitialized() && !game.isPaused()) {
            if (isActive() && game.isStarted() && !game.isPaused()) {
                doPauseGame();
            }
        }
    }

    public void updateUI() {
        updateValues = true;
    }

    public void doAdRewarded(int amount, String type) {
        //TODO
    }

    public void doAdClosed() {
        //TODO
    }
}
