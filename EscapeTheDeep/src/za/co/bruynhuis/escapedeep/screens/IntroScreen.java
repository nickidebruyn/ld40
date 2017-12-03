/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.control.camera.CameraShaker;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.effect.TextWriteEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.Timer;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import za.co.bruynhuis.escapedeep.MainApplication;
import za.co.bruynhuis.escapedeep.game.Game;
import za.co.bruynhuis.escapedeep.game.Player;

/**
 *
 * @author NideBruyn
 */
public class IntroScreen extends AbstractScreen implements KeyboardControlListener {

    private Game game;
    private Player player;
    private MainApplication mainApplication;
    private KeyboardControlInputListener keyboardControlInputListener;
    public static float camHeight = 3f;
    public static float cameraMoveHeight = 6f;
    private CameraShaker cameraShaker;
    private Timer thunderTimer = new Timer(2000);
    private Timer skyTimer = new Timer(400);
    private Label instructionsText;
    private ControlButton skipButton;
    private Image overlay;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        skipButton = new ControlButton(hudPanel, "skipbutton", 200, 50);
        skipButton.setText("Skip Intro");
        skipButton.setFontSize(28);
        skipButton.setTextColor(ColorRGBA.White);
        skipButton.rightBottom(5, 5);
        skipButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen("play");
                }
            }
        });

        instructionsText = new Label(hudPanel, "", 18, 1000, 400);
        instructionsText.setTextColor(ColorRGBA.White);
        instructionsText.setWrapMode(LineWrapMode.Word);
        instructionsText.setAlignment(TextAlign.LEFT);
        instructionsText.setVerticalAlignment(TextAlign.TOP);
        instructionsText.leftTop(100, 100);
        instructionsText.setAnimated(true);
        instructionsText.addEffect(new TextWriteEffect(instructionsText, 8));

        overlay = new Image(hudPanel, "Textures/white.png");
        overlay.setBackgroundColor(ColorRGBA.Black);
        overlay.center();

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);

        cameraShaker = new CameraShaker(camera, rootNode);
    }

    @Override
    protected void load() {

        //Now we load the level.
        game = new Game(mainApplication, rootNode, true);
        game.load();

        player = new Player(game, true);
        player.load();

        player.setPosition(new Vector3f(-3, 1, 0));
        camera.setLocation(new Vector3f(0, player.getPosition().y + camHeight, 10));

        game.loadSky2(0, 0);

        for (int i = -5; i <= 5; i++) {
            game.addPlatform(i, 0, 32);

        }

        game.addChild(5, 1);

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        game.start(player);
        thunderTimer.start();

        instructionsText.setVisible(false);
        instructionsText.setText("In a galaxy far far away a small planet, Cubeecon existed.\n"
                + "All creatures big and small lived in harmony together.\n"
                + "Then the day everyone feared arrived.\n"
                + ".....\n\n\n"
                + "the sky fell apart, the world started to flood\n and you could only save the young ones.");
        instructionsText.show();
        overlay.setTransparency(0);
        mainApplication.getSoundManager().setMusicVolume("intro", 0.3f);
        mainApplication.getSoundManager().playMusic("intro");

        keyboardControlInputListener.registerWithInput(inputManager);

    }

    @Override
    protected void exit() {
        mainApplication.getSoundManager().stopMusic("intro");
        keyboardControlInputListener.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            thunderTimer.update(tpf);

            if (thunderTimer.finished()) {
                skyTimer.start();
                game.addFireball(10, 12, -7, -4);
                thunderTimer.stop();
            }

            skyTimer.update(tpf);

            if (skyTimer.finished()) {
                overlay.setTransparency(0);
                baseApplication.getSoundManager().playSound("thunder2");
                cameraShaker.shake(0.2f, 100f);
                
                Tween.to(overlay, WidgetAccessor.OPACITY, 1f)
                        .target(1f)
                        .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        game.loadOcean(0, -12);
                        game.loadSky(0, 0);


                        Tween.to(overlay, WidgetAccessor.OPACITY, 2f)
                                .target(0f)
                                .delay(2)
                                .setCallback(new TweenCallback() {
                            public void onEvent(int i, BaseTween<?> bt) {
                                fader.setOutWait(60);
                                showScreen("play");
                            }
                        })
                                .start(window.getApplication().getTweenManager());

                    }
                })
                        .start(window.getApplication().getTweenManager());

                skyTimer.stop();
            }
        }
    }

    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
        if (isActive()) {

            if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {
                if (keyboardControlEvent.isButton1() && keyboardControlEvent.isKeyDown()) {
                    showScreen("play");

                }

            }


        }
    }
}
