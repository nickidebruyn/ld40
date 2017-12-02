/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.screens;

import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGameListener;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import za.co.bruynhuis.escapedeep.MainApplication;
import za.co.bruynhuis.escapedeep.game.Game;
import za.co.bruynhuis.escapedeep.game.Player;

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
    
    public static float camHeight = 3f;
    public static float cameraMoveHeight = 6f;
    
    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        bodiesLabel = new Label(hudPanel, "Bodies: 0", 16);
        bodiesLabel.setAlignment(TextAlign.LEFT);
        bodiesLabel.leftTop(0, 0);
        
        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);
        
    }
    
    private void doStartGameAction() {
        game.start(player);

    }

    @Override
    protected void load() {

        //Now we load the level.
        game = new Game(mainApplication, rootNode);
        game.load();

        player = new Player(game);
        player.load();
        
        game.addGameListener(this);
        
        camera.setLocation(new Vector3f(0, player.getPosition().y + camHeight, 10));
        
        doStartGameAction();
    }

    @Override
    protected void show() {
        baseApplication.showStats();
        keyboardControlInputListener.registerWithInput(inputManager);
    }

    @Override
    protected void exit() {
        keyboardControlInputListener.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
    }

    @Override
    public void doGameOver() {
        exitScreen();
    }

    @Override
    public void doGameCompleted() {
    }

    @Override
    public void doScoreChanged(int score) {
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            
            bodiesLabel.setText("Bodies: " + mainApplication.getDyn4jAppState().getPhysicsSpace().getBodyCount());
            //update the camera
//            camera.setLocation(new Vector3f(player.getPosition().x, game.getStartPosition().y + 3, 10));
            
            
            camera.setLocation(camera.getLocation().interpolate(camera.getLocation().clone().setX(0).setY(player.getPosition().y + PlayScreen.camHeight), 0.015f));            
            
            
//            if (player.getPosition().y > cameraMoveHeight) {
//                camera.setLocation(camera.getLocation().interpolate(camera.getLocation().clone().setX(0).setY(player.getPosition().y + PlayScreen.camHeight), 0.025f));
//            } else {
//                camera.setLocation(camera.getLocation().interpolate(camera.getLocation().clone().setX(0).setY(PlayScreen.camHeight), 0.025f));
//            }
        }
    }

    @Override
    public void doCollisionPlayerWithTerrain(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithTerrain(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
    }
    

    public void doCollisionPlayerWithPlayer(Spatial collided, Spatial collider) {
    }


    @Override
    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionPlayerWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionObstacleWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithBullet(Spatial collided, Spatial collider) {
    }

    @Override
    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
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
                
                if (keyboardControlEvent.isButton2() && keyboardControlEvent.isKeyDown()) {
                    player.jump(1);
                }
                
            }
            
        }
    }
    
}
