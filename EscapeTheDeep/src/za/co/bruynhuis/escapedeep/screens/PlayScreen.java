/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.screens;

import com.bruynhuis.galago.control.camera.CameraShaker;
import com.bruynhuis.galago.filters.ChromaticAberrationFilter;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGameListener;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Spatial;
import za.co.bruynhuis.escapedeep.MainApplication;
import za.co.bruynhuis.escapedeep.control.BulletControl;
import za.co.bruynhuis.escapedeep.control.ChildControl;
import za.co.bruynhuis.escapedeep.control.EnemyControl;
import za.co.bruynhuis.escapedeep.control.PickupControl;
import za.co.bruynhuis.escapedeep.control.PlatformControl;
import za.co.bruynhuis.escapedeep.game.Game;
import za.co.bruynhuis.escapedeep.game.Player;
import za.co.bruynhuis.escapedeep.ui.GameOverDialog;
import za.co.bruynhuis.escapedeep.ui.InfoDialog;
import za.co.bruynhuis.escapedeep.ui.PlayerScorePanel;

/**
 *
 * @author NideBruyn
 */
public class PlayScreen extends AbstractScreen implements SimplePhysics2DGameListener, KeyboardControlListener {

    private Game game;
    private Player player;
    private MainApplication mainApplication;
    private KeyboardControlInputListener keyboardControlInputListener;
    private Label bodiesLabel;
    private PlayerScorePanel playerScorePanel;
    private InfoDialog infoDialog;
    private GameOverDialog gameOverDialog;
    public static float camHeight = 3f;
    public static float cameraMoveHeight = 6f;
    private boolean infoWasShown = false;
    private CameraShaker cameraShaker;
    private Timer thunderTimer = new Timer(200);
    private FilterPostProcessor fpp;
    private ChromaticAberrationFilter chromaticAberrationFilter;
    private BulletControl bulletToDispose;
    private EnemyControl enemyToDispose;
    private ChildControl childToDispose;
    private ChildControl childToKill;
    private PlatformControl breakableControl;
    private boolean enemyHitPlayer = false;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        bodiesLabel = new Label(hudPanel, "Bodies: 0", 16);
        bodiesLabel.setAlignment(TextAlign.LEFT);
        bodiesLabel.rightBottom(0, 0);

        playerScorePanel = new PlayerScorePanel(hudPanel);
        playerScorePanel.centerTop(0, 0);

        infoDialog = new InfoDialog(window);
        infoDialog.addPlayButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doStartGameAction();
                }
            }
        });

        infoDialog.addExitButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doExitGameAction();
                }
            }
        });

        gameOverDialog = new GameOverDialog(window);
        gameOverDialog.addPlayButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen("play");
                }
            }
        });

        gameOverDialog.addExitButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doExitGameAction();
                }
            }
        });

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);

        cameraShaker = new CameraShaker(camera, rootNode);
    }

    private void doStartGameAction() {
        infoDialog.hide();
        gameOverDialog.hide();
        mainApplication.getSoundManager().playSound("button");
        game.start(player);
        thunderTimer.start();

    }

    private void doExitGameAction() {
        infoDialog.hide();
        gameOverDialog.hide();
        mainApplication.getSoundManager().playSound("button");
        exitScreen();

    }

    @Override
    protected void load() {

        enemyToDispose = null;
        bulletToDispose = null;
        enemyHitPlayer = false;
        childToDispose = null;
        childToKill = null;
        breakableControl = null;

        playerScorePanel.setScore(0);

        //Now we load the level.
        game = new Game(mainApplication, rootNode, false);
        game.load();

        player = new Player(game, true);
        player.load();

        fpp = new FilterPostProcessor(assetManager);
        baseApplication.getViewPort().addProcessor(fpp);

        chromaticAberrationFilter = new ChromaticAberrationFilter();
        fpp.addFilter(chromaticAberrationFilter);

        game.addGameListener(this);

        camera.setLocation(new Vector3f(0, player.getPosition().y + camHeight, 10));

        mainApplication.getSoundManager().setMusicVolume("background", 0.4f);
        mainApplication.getSoundManager().playMusic("background");

        mainApplication.getSoundManager().setMusicVolume("rain", 0.6f);
        mainApplication.getSoundManager().playMusic("rain");

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
//        baseApplication.showStats();
        bodiesLabel.hide();
        keyboardControlInputListener.registerWithInput(inputManager);

        gameOverDialog.hide();

        if (!infoWasShown) {
            infoDialog.show();
            infoWasShown = true;

        } else {
            doStartGameAction();
        }

    }

    @Override
    protected void exit() {
        mainApplication.getSoundManager().stopMusic("background");
        mainApplication.getSoundManager().stopMusic("rain");
        keyboardControlInputListener.unregisterInput();
        baseApplication.getViewPort().removeProcessor(fpp);
        game.close();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {

        thunderTimer.stop();
        chromaticAberrationFilter.doEffect(0.25f, new Vector3f(0.025f, 0.02f, -0.025f));

        mainApplication.getSoundManager().setMusicVolume("background", 0.1f);
        mainApplication.getSoundManager().setMusicVolume("rain", 0.1f);
        mainApplication.getSoundManager().playSound("gameover");

        //BEST SCORE CALCULATION
        int score = player.getScore();
        int oldScore = baseApplication.getGameSaves().getGameData().getScore();

        if (score > oldScore) {
            baseApplication.getGameSaves().getGameData().setScore(score);

        }

        //GAMES PLAYED CALCULATION
        int gamesPlayed = mainApplication.getGameSaves().getGameData().getGamesPlayed();
        gamesPlayed++;
        mainApplication.getGameSaves().getGameData().setGamesPlayed(gamesPlayed);

        gameOverDialog.show(oldScore, score);

        //Finally save the data
        baseApplication.getGameSaves().save();
    }

    @Override
    public void doGameCompleted() {
    }

    @Override
    public void doScoreChanged(int score) {
        playerScorePanel.setScore(score);
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {

            bodiesLabel.setText("Bodies: " + mainApplication.getDyn4jAppState().getPhysicsSpace().getBodyCount());

            if (!game.isGameOver()) {
                //update the camera                  
                camera.setLocation(camera.getLocation().interpolate(camera.getLocation().clone().setX(0).setY(player.getPosition().y + PlayScreen.camHeight), 0.015f));
                playerScorePanel.setBulletActive(player.isHasBullet());


                if (bulletToDispose != null) {
                    bulletToDispose.doDie();
                    bulletToDispose = null;
                }

                if (enemyToDispose != null) {
                    enemyToDispose.doDie();
                    enemyToDispose = null;
                }
                
                if (childToDispose != null) {
                    childToDispose.doDie();
                    baseApplication.getSoundManager().playSound("pickuplong");
                    player.addScore(1);
                    childToDispose = null;
                }
                
                if (childToKill != null) {
                    game.getBaseApplication().getEffectManager().doEffect("blood", childToKill.getSpatial().getLocalTranslation().clone());
                    childToKill.doDie();
                    childToKill = null;
                }

                if (enemyHitPlayer) {
                    enemyHitPlayer = false;
                    player.doDamage(5);
                }

                if (breakableControl != null) {
                    breakableControl.doDestroy();
                    breakableControl = null;
                }

                thunderTimer.update(tpf);
                if (thunderTimer.finished()) {
                    thunderTimer.setMaxTime(FastMath.nextRandomInt(500, 800));
                    if (FastMath.nextRandomInt(0, 1) == 0) {
                        baseApplication.getSoundManager().playSound("thunder1");
                        cameraShaker.shake(0.1f, 100);
                        
                    } else {
                        baseApplication.getSoundManager().playSound("thunder2");
                        cameraShaker.shake(0.15f, 60);
                    }
                                        
                    thunderTimer.reset();

                }
            }



        }
    }

    @Override
    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
        if (collided.getControl(PlatformControl.class) != null) {
            float dis = collided.getControl(RigidBodyControl.class).getPhysicLocation().y - player.getPosition().y;

//            Debug.log("Kill dis: " + dis);

            if (dis > 0.9f) {
                breakableControl = collided.getControl(PlatformControl.class);

            }
        }
    }

    @Override
    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
//        Debug.log("Pickup");
        if (collided.getControl(PickupControl.class) != null) {

            if (collided.getUserData("type") != null && collided.getUserData("type").equals("bullet")) {
//                Debug.log("Got a bullet");
                if (!player.isHasBullet()) {
                    collided.getControl(PickupControl.class).doDestroy();
                    baseApplication.getSoundManager().playSound("pickup");
                    player.setHasBullet(true);

                }
            } else {
                baseApplication.getSoundManager().playSound("pickup");
                baseApplication.getEffectManager().doEffect("gold", collided.getLocalTranslation().clone());

                collided.getControl(PickupControl.class).doDestroy();

                player.addScore(1);
            }

        }
    }

    public void doCollisionPlayerWithPlayer(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {

        if (collided.getControl(EnemyControl.class) != null) {
            float dis = player.getPosition().y - collided.getControl(RigidBodyControl.class).getPhysicLocation().y;

//            Debug.log("Kill dis: " + dis);

            if (dis > 0.9f) {
                enemyToDispose = collided.getControl(EnemyControl.class);
                player.jump(0.5f);

            } else {
                enemyHitPlayer = true;
            }

        }
        
        if (collided.getControl(ChildControl.class) != null) {
            childToDispose = collided.getControl(ChildControl.class);
        }

    }

    @Override
    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
//        Debug.log("Enemy with bullet: " + collided.getName());
        if (collided.getControl(BulletControl.class) != null
                && collider.getControl(EnemyControl.class) != null) {
            bulletToDispose = collided.getControl(BulletControl.class);
            enemyToDispose = collider.getControl(EnemyControl.class);

        }
        
        if (collided.getControl(BulletControl.class) != null
                && collider.getControl(ChildControl.class) != null) {
            bulletToDispose = collided.getControl(BulletControl.class);
            childToKill = collider.getControl(ChildControl.class);

        }

    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
        
        if (collided.getControl(EnemyControl.class) != null) {
            collided.getControl(EnemyControl.class).doTurn();
        }

        if (collider.getControl(EnemyControl.class) != null) {
            collider.getControl(EnemyControl.class).doTurn();
        }
        
        if (collided.getControl(ChildControl.class) != null) {
            collided.getControl(ChildControl.class).doTurn();
        }

        if (collider.getControl(ChildControl.class) != null) {
            collider.getControl(ChildControl.class).doTurn();
        }

    }

    @Override
    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionTerrainWithBullet(Spatial collided, Spatial collider) {
    }

    public void doCollisionEnemyWithPickup(Spatial collided, Spatial collider) {
    }

    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
        if (isActive()) {

            if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {

                if (keyboardControlEvent.isLeft()) {
                    player.setLeft(keyboardControlEvent.isKeyDown());
                }

                if (keyboardControlEvent.isRight()) {
                    player.setRight(keyboardControlEvent.isKeyDown());
                }

                if (keyboardControlEvent.isUp()
                        && keyboardControlEvent.isKeyDown()) {
                    player.jump(1);
                }

                if (keyboardControlEvent.isButton2() && keyboardControlEvent.isKeyDown()) {
                    if (player.isHasBullet()) {
                        player.attack();
                    }

                }

            } else if (!game.isStarted() && !game.isGameOver()) {

                if (keyboardControlEvent.isButton1() && keyboardControlEvent.isKeyDown()) {
                    doStartGameAction();

                }
            } else if (game.isGameOver() && gameOverDialog.isVisible()) {

                if (keyboardControlEvent.isButton1() && keyboardControlEvent.isKeyDown()) {
                    showScreen("play");

                }
            }

        }
    }
}
