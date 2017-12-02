/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.control;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import za.co.bruynhuis.escapedeep.game.Game;

/**
 *
 * @author NideBruyn
 */
public class BulletControl extends AbstractControl {

    private Game game;
    private Sprite sprite;
    private RigidBodyControl rigidBodyControl;
    private float moveSpeed;
    private Vector3f direction;

    public BulletControl(Game game, Sprite sprite, float speed, Vector3f direction) {
        this.game = game;
        this.sprite = sprite;
        this.moveSpeed = speed;
        this.direction = direction;
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
            }

            //Now we can execute the movement logic
            if (rigidBodyControl != null) {
                rigidBodyControl.getBody().setLinearVelocity(direction.x * moveSpeed, direction.y * moveSpeed);

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
        System.out.println(" ********* Removing bullet");
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
        sprite.removeFromParent();

    }
    
}
