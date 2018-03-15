/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Expo;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.listener.RewardAdListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.HPanel;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SharedSystem;
import za.co.bruynhuis.blockout.MainApplication;
import za.co.bruynhuis.blockout.ui.ExitDialog;
import za.co.bruynhuis.blockout.ui.LargeButton;
import za.co.bruynhuis.blockout.ui.RewardAdButton;
import za.co.bruynhuis.blockout.ui.RoundButton;
import za.co.bruynhuis.blockout.ui.ScorePanel;

/**
 *
 * @author NideBruyn
 */
public class MenuScreen extends AbstractScreen implements RewardAdListener {

    private MainApplication mainApplication;
    private Image heading;
    private LargeButton playButton;
    private LargeButton rateButton;
    private RewardAdButton rewardAdButton;
    private ScorePanel scorePanel;
    private HPanel hPanel;
    private RoundButton soundButton;
    private RoundButton leaderboardButton;
    private RoundButton achievementButton;
    private RoundButton shareButton;
    private boolean updateValues = false;
    private ExitDialog exitDialog;
    private boolean rewarded = false;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        heading = new Image(hudPanel, "Interface/heading-menu.png", 392, 382, true);
        heading.centerTop(0, 40);

        playButton = new LargeButton(hudPanel, "playbutton", "PLAY", ColorUtils.hsv(0.95f, 0.85f, .92f));
        playButton.centerAt(0, -80);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    baseApplication.doAnalyticsAction("menu screen", "play_action", "Play button clicked.");
                    showScreen("play");
                }
            }
        });

        rateButton = new LargeButton(hudPanel, "rateButton", "RATE", ColorUtils.hsv(0.57f, 0.88f, .73f));
        rateButton.centerAt(0, -160);
        rateButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    baseApplication.doAnalyticsAction("menu screen", "rate_action", "Rate button clicked.");
                    mainApplication.doRateApplication();
                    mainApplication.getGameSaves().getGameData().setRated(true);
                    mainApplication.getGameSaves().save();
                    rateButton.setVisible(false);
                    rewardAdButton.setVisible(true);
                }
            }
        });

        rewardAdButton = new RewardAdButton(hudPanel);
        rewardAdButton.centerAt(0, -160);
        rewardAdButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    baseApplication.doAnalyticsAction("menu screen", "reward_action", "Reward button clicked.");
                    mainApplication.showAds(MainApplication.ADMOB_REWARDS, true);
                }
            }
        });
        
        Tween.to(rewardAdButton, SpatialAccessor.POS_XYZ, 0.3f)
                .ease(Circ.OUT)
                .target(rewardAdButton.getPosition().x + 10, rewardAdButton.getPosition().y, rewardAdButton.getPosition().z)
                .repeatYoyo(Tween.INFINITY, 0f)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        

        scorePanel = new ScorePanel(hudPanel, false, false);
        scorePanel.centerTop(0, 0);

        hPanel = new HPanel(hudPanel, 340, 64);
        hudPanel.add(hPanel);
        hPanel.centerAt(0, -260);

        soundButton = new RoundButton(hPanel, "soundbutton", "Interface/sound-icon-on.png");
        soundButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");

                    if (baseApplication.getGameSaves().getGameData().isSoundOn()) {
                        baseApplication.getGameSaves().getGameData().setSoundOn(false);
                        baseApplication.doAnalyticsAction("playscreen", "sound_action", "Sound off.");
                    } else {
                        baseApplication.getGameSaves().getGameData().setSoundOn(true);
                        baseApplication.doAnalyticsAction("playscreen", "sound_action", "Sound on.");

                    }
                    baseApplication.getGameSaves().save();

                    updateSoundIcon();

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
                    baseApplication.doAnalyticsAction("playscreen", "show_leaderboard", "Leaderboard button clicked.");


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
                    baseApplication.doAnalyticsAction("playscreen", "show_achievements", "Achievements button clicked.");

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
                    baseApplication.doAnalyticsAction("playscreen", "share_app", "Share button clicked.");

                }
            }
        });

        hPanel.layout();

        exitDialog = new ExitDialog(window);
        exitDialog.addYesButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("button");
                    exitScreen();
                }
            }
        });

        exitDialog.addNoButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("button");
                    showScreen("play");
                }
            }
        });

        exitDialog.addRateButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    mainApplication.getSoundManager().playSound("button");
                    mainApplication.doRateApplication();
                    mainApplication.getGameSaves().getGameData().setRated(true);
                    mainApplication.getGameSaves().save();
                    exitDialog.hide();
                    rateButton.setVisible(false);
                    rewardAdButton.setVisible(true);
                }
            }
        });

    }

    protected void updateSoundIcon() {

        if (baseApplication.getGameSaves().getGameData().isSoundOn()) {
            soundButton.updatePicture("Interface/sound-icon-on.png");
        } else {
            soundButton.updatePicture("Interface/sound-icon-off.png");
        }

        baseApplication.getSoundManager().muteAll(!baseApplication.getGameSaves().getGameData().isSoundOn());

    }

    @Override
    protected void load() {
        updateSoundIcon();
        heading.setScale(0);

        rewarded = false;
        mainApplication.addRewardAdListener(this);
    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        scorePanel.show();
        rateButton.setVisible(!mainApplication.getGameSaves().getGameData().isRated());
        rewardAdButton.setVisible(mainApplication.getGameSaves().getGameData().isRated() && mainApplication.isRewardAdLoaded());

        Tween.to(heading, WidgetAccessor.SCALE_XY, 1f)
                .delay(0.1f)
                .target(1, 1)
                .ease(Expo.OUT)
                .setCallback(new TweenCallback() {
            public void onEvent(int i, BaseTween<?> bt) {
                heading.setScale(1);
            }
        })
                .start(window.getApplication().getTweenManager());
    }

    @Override
    protected void exit() {
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
        
        if (rewarded) {
            mainApplication.getSoundManager().playSound("loot");
            int loot = mainApplication.getGameSaves().getGameData().getLevel();
            loot += 100;
            mainApplication.getGameSaves().getGameData().setLevel(loot);
            scorePanel.updateLoot(loot);
            rewarded = false;
            
            rewardAdButton.setVisible(false);
        }
    }

    @Override
    public void doEscape(boolean touchEvent) {
        if (isActive() && isEnabled() && isInitialized() && !exitDialog.isVisible()) {
            exitDialog.show();
        }
    }

    public void doAdRewarded(int amount, String type) {
        rewarded = true;

    }

    public void doAdClosed() {
    }

    public void doAdLoaded() {
        rewardAdButton.setVisible(mainApplication.getGameSaves().getGameData().isRated());
    }
}
