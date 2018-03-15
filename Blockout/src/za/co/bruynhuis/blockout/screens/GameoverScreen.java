/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.screens;

import aurelienribon.tweenengine.Tween;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.listener.RewardAdListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.HPanel;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SharedSystem;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import za.co.bruynhuis.blockout.MainApplication;
import za.co.bruynhuis.blockout.ui.LargeButton;
import za.co.bruynhuis.blockout.ui.ReviveAdButton;
import za.co.bruynhuis.blockout.ui.ReviveLootButton;
import za.co.bruynhuis.blockout.ui.RoundButton;
import za.co.bruynhuis.blockout.ui.ScorePanel;

/**
 *
 * @author NideBruyn
 */
public class GameoverScreen extends AbstractScreen implements RewardAdListener {

    private MainApplication mainApplication;
    private Label scoreLabel;
    private Label bestScoreLabel;
    private LargeButton replayButton;
    private LargeButton menuButton;
    private ReviveLootButton reviveLootButton;
    private ReviveAdButton reviveAdButton;
    private ScorePanel scorePanel;
    private HPanel hPanel;
    private RoundButton likeButton;
    private RoundButton leaderboardButton;
    private RoundButton achievementButton;
    private RoundButton shareButton;
    private boolean updateValues = false;
    private Timer reviveCounterTimer = new Timer(50);
    private int REVIVE_COST = 100;

    public void setBestScore(int score) {
        this.bestScoreLabel.setText("BEST " + score);
    }

    public void setScore(int score) {
        this.scoreLabel.setText("" + score);
    }

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        scoreLabel = new Label(hudPanel, "0", 64, 400, 60);
        scoreLabel.setTextColor(ColorRGBA.White);
        scoreLabel.centerAt(0, 200);

        bestScoreLabel = new Label(hudPanel, "Best 0", 40, 400, 60);
        bestScoreLabel.setTextColor(ColorRGBA.White);
        bestScoreLabel.centerAt(0, 120);

        replayButton = new LargeButton(hudPanel, "replayButton", "RETRY", ColorUtils.hsv(0.95f, 0.85f, .92f));
        replayButton.centerAt(0, 40);
        replayButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    mainApplication.getPlayScreen().closeGame();
                    showScreen("play");
                }
            }
        });

        menuButton = new LargeButton(hudPanel, "menuButton", "MENU", ColorUtils.hsv(0.83f, 0.7f, .7f));
        menuButton.centerAt(0, -40);
        menuButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    mainApplication.getPlayScreen().closeGame();
                    showScreen("menu");
                }
            }
        });

        reviveLootButton = new ReviveLootButton(hudPanel);
        reviveLootButton.centerAt(0, -150);
        reviveLootButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("loot");
                    reviveCounterTimer.reset();
                    reviveLootButton.setVisible(false);
                    int loot = mainApplication.getGameSaves().getGameData().getLevel();
                    loot -= REVIVE_COST;
                    mainApplication.getGameSaves().getGameData().setLevel(loot);
                    scorePanel.updateLoot(loot);
                }
            }
        });

        reviveAdButton = new ReviveAdButton(hudPanel);
        reviveAdButton.centerAt(0, -150);
        reviveAdButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("button");
                    reviveLootButton.setVisible(false);
                    reviveAdButton.setVisible(false);
                    mainApplication.showAds(MainApplication.ADMOB_REWARDS, true);
                }
            }
        });

        Tween.to(reviveLootButton, SpatialAccessor.POS_XYZ, 0.2f)
                .target(reviveLootButton.getPosition().x + 10, reviveLootButton.getPosition().y, reviveLootButton.getPosition().z)
                .repeatYoyo(Tween.INFINITY, 0)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        Tween.to(reviveAdButton, SpatialAccessor.POS_XYZ, 0.2f)
                .target(reviveAdButton.getPosition().x + 10, reviveAdButton.getPosition().y, reviveAdButton.getPosition().z)
                .repeatYoyo(Tween.INFINITY, 0)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        scorePanel = new ScorePanel(hudPanel, false, true);
        scorePanel.centerTop(0, 0);
        scorePanel.addInfoButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    mainApplication.doLinkToUrl("https://www.facebook.com/breakout");
                }
            }
        });

        hPanel = new HPanel(hudPanel, 340, 64);
        hudPanel.add(hPanel);
        hPanel.centerAt(0, -260);

        likeButton = new RoundButton(hPanel, "likeButton", "Interface/like-icon.png");
        likeButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    mainApplication.doRateApplication();
                    baseApplication.doAnalyticsAction("GameoverScreen", "Like app", "Like button clicked.");
                    mainApplication.getGameSaves().getGameData().setRated(true);
                    mainApplication.getGameSaves().save();

                }
            }
        });

        leaderboardButton = new RoundButton(hPanel, "leaderboardButton", "Interface/leaderboard-icon.png");
        leaderboardButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    mainApplication.showLeaderBoard();
                    baseApplication.doAnalyticsAction("GameoverScreen", "show_leaderboard", "Leaderboard button clicked.");


                }
            }
        });


        achievementButton = new RoundButton(hPanel, "achievementButton", "Interface/achievement-icon.png");
        achievementButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    baseApplication.doShowAchievements();
                    baseApplication.doAnalyticsAction("GameoverScreen", "show_achievements", "Achievements button clicked.");

                }
            }
        });

        shareButton = new RoundButton(hPanel, "shareButton", "Interface/share-icon.png");
        shareButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    baseApplication.doShareApplication();
                    baseApplication.doAnalyticsAction("GameoverScreen", "share_app", "Share button clicked.");

                }
            }
        });

        hPanel.layout();
    }

    @Override
    protected void load() {
        reviveCounterTimer.stop();
        
        mainApplication.addRewardAdListener(this);
    }

    @Override
    protected void show() {
        setPreviousScreen("menu");
        scorePanel.show();

        int loot = mainApplication.getGameSaves().getGameData().getLevel(); 
         
        if (mainApplication.getPlayScreen().isGameWasRevived()) {
            reviveLootButton.setVisible(false);
            reviveAdButton.setVisible(false);
            
            if (mainApplication.getGameSaves().getGameData().getGamesPlayed() % 2 == 0) {
                baseApplication.showAds(BaseApplication.ADMOB_INTERSTITIALS, true);
            } else {
                baseApplication.showAds(BaseApplication.ADMOB, true);
            }
            
        } else if (loot > REVIVE_COST) {
            
            reviveLootButton.setVisible(true);
            reviveAdButton.setVisible(false);
            
        } else {
            reviveLootButton.setVisible(false);
            reviveAdButton.setVisible(mainApplication.isRewardAdLoaded());
            
        }
    }

    @Override
    protected void exit() {
        baseApplication.showAds(BaseApplication.ADMOB, false);
    }

    @Override
    protected void pause() {
    }

    public void updateUI() {
        updateValues = true;
    }

    @Override
    public void update(float tpf) {
        if (isActive() && updateValues) {
            scorePanel.updateValues();
            updateValues = false;
        }

        if (isActive()) {

            reviveCounterTimer.update(tpf);

            if (reviveCounterTimer.finished()) {
                reviveCounterTimer.stop();
                reviveLootButton.setVisible(false);
                mainApplication.getPlayScreen().reviveGame();
                mainApplication.getGameSaves().save();
                mainApplication.saveGameDataToCloud();
                showScreen("play");
            }

        }
    }

    @Override
    public void doEscape(boolean touchEvent) {
        if (isActive() && isEnabled() && isInitialized()) {
            mainApplication.getPlayScreen().closeGame();
            showPreviousScreen();
        }
    }

    public void doAdRewarded(int amount, String type) {
        mainApplication.getPlayScreen().reviveGame();
        showScreen("play");

    }

    public void doAdClosed() {
    }

    public void doAdLoaded() {
    }
}
