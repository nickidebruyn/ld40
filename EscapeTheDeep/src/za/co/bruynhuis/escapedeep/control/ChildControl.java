/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.control;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import za.co.bruynhuis.escapedeep.game.Game;

/**
 *
 * @author NideBruyn
 */
public class ChildControl extends AbstractControl {

    private Game game;
    private Sprite sprite;
    private RigidBodyControl rigidBodyControl;
    private float moveSpeed;
    private Vector3f direction;
    private float maxXPos;
    private float killDistance;
    private Timer turnDelay = new Timer(100);

    public ChildControl(Game game, Sprite sprite, float speed, Vector3f direction, float maxXPos, float killDistance) {
        this.game = game;
        this.sprite = sprite;
        this.moveSpeed = speed;
        this.direction = direction;
        this.maxXPos = maxXPos;
        this.killDistance = killDistance;
    }

    public Vector3f getLocation() {
        return rigidBodyControl.getPhysicLocation();
    }

    @Override
    protected void controlUpdate(float tpf) {

        if (game.isStarted() && !game.isPaused()) {

            //First we need to get this
            if (rigidBodyControl == null) {
                rigidBodyControl = sprite.getControl(RigidBodyControl.class);
                turnDelay.start();
            }

            //Now we can execute the movement logic
            if (rigidBodyControl != null) {

                turnDelay.update(tpf);

                if (direction.x > 0) {
                    sprite.flipHorizontal(false);

                    //Check to turn
                    if (rigidBodyControl.getPhysicLocation().x >= maxXPos) {
                        doTurn();
                    }

                } else {
                    sprite.flipHorizontal(true);

                    //Check to turn
                    if (rigidBodyControl.getPhysicLocation().x <= -maxXPos) {
                        doTurn();
                    }

                }

                rigidBodyControl.getBody().setLinearVelocity(direction.x * moveSpeed, rigidBodyControl.getLinearVelocity().y);

                if (game.getPlayer().getPosition().y > rigidBodyControl.getPhysicLocation().y
                        && game.getPlayer().getPosition().y - rigidBodyControl.getPhysicLocation().y >= killDistance) {
                    doDie();
                }
            }
        }

        if (rigidBodyControl != null) {
            //Correct the rotation so that the player don;t twist
            rigidBodyControl.getBody().setAngularVelocity(0);
            rigidBodyControl.getBody().setAngularDamping(0);
            rigidBodyControl.getBody().getTransform().setRotation(0);

        }

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void doDie() {
        System.out.println(" ********* Removing child");
//        game.getBaseApplication().getEffectManager().doEffect("blood", sprite.getLocalTranslation().clone());
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
        sprite.removeFromParent();

    }

    public void doTurn() {
        if (turnDelay.finished()) {
            direction = direction.multLocal(-1, 0, 0);
            turnDelay.reset();
        }        

    }
}
