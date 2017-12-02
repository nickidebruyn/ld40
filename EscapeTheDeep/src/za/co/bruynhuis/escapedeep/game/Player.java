/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.game;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author NideBruyn
 */
public class Player extends SimplePhysics2DPlayer implements PhysicsCollisionListener, PhysicsTickListener {

    private Sprite sprite;
    private RigidBodyControl rigidBodyControl;
    private float jumpForce = 16.0f;
    private boolean onGround = false;
    private boolean left, right = false;
    private float moveSpeed = 3f;
    private Timer jumpDelayTimer = new Timer(0.5f);
    private float gravity = 3f;

    public Player(SimplePhysics2DGame simplePhysics2DGame) {
        super(simplePhysics2DGame);
    }

    @Override
    protected void init() {
        lives = 0;

        sprite = new Sprite(Platform2DGame.TYPE_PLAYER, 1f, 1f);
        sprite.setMaterial(game.getBaseApplication().getModelManager().getMaterial("Materials/player.j3m"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(0, 0, 1f);

        rigidBodyControl = new RigidBodyControl(new CircleCollisionShape(0.5f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        playerNode.addControl(rigidBodyControl);
        playerNode.attachChild(sprite);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        rigidBodyControl.setPhysicLocation(new Vector3f(startPosition.x, startPosition.y, 0f));
        rigidBodyControl.setGravityScale(gravity);


        playerNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {

                if (game.isStarted() && !game.isPaused() && !game.isGameOver()) {

                    jumpDelayTimer.update(tpf);

                    if (rigidBodyControl != null) {

                        if (getPosition().y < -10f) {
                            doDamage(10);
                        }

                    }

                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        //Add the collision listener
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsCollisionListener(this);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().addPhysicsTickListener(this);

    }

    @Override
    public Vector3f getPosition() {
        return rigidBodyControl.getPhysicLocation().clone();
    }

    @Override
    public void doDie() {
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(playerNode);
//        game.getBaseApplication().getEffectManager().doEffect("die", getPosition());

        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsCollisionListener(this);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);

        Tween.to(sprite, SpatialAccessor.SCALE_XYZ, 0.15f)
                .target(0, 0, 0)
                .delay(0.005f)
                .setCallback(new TweenCallback() {
            public void onEvent(int i, BaseTween<?> bt) {
            }
        })
                .start(game.getBaseApplication().getTweenManager());

        rigidBodyControl.clearForces();

    }

    public void jump(float extrajumpForcePer) {

        if (onGround && !game.isGameOver()) {
            rigidBodyControl.clearForces();
            float jumpAmount = jumpForce * extrajumpForcePer;
            rigidBodyControl.applyImpulse(0, jumpAmount);
            onGround = false;
            jumpDelayTimer.reset();
        }

    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void collision(Spatial spatialA, CollisionShape collisionShapeA, Spatial spatialB, CollisionShape collisionShapeB, Vector3f point) {
        float dist = getPosition().distance(point.multLocal(1, 1, 0));
//        log("Point: " + point + ";  Player: " + getPosition());
//        log("On Gound dist: " + dist);
        if (point.y <= getPosition().y - dist) {

            if (jumpDelayTimer.finished()) {
                onGround = true;
            }

        }

    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isGoingDown() {
        return rigidBodyControl.getLinearVelocity().y < 0.1f && rigidBodyControl.getLinearVelocity().y > -0.1f;
    }

    @Override
    public void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        jumpDelayTimer.start();

    }

    public boolean isFacingForward() {
        return !sprite.isHorizontalFlipped();
    }

    public int getLives() {
        return lives;
    }

    @Override
    protected float getSize() {
        return 1f;
    }

    public void doLevelCompleteAction() {
//        doSmileFace();
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(playerNode);

        Tween.to(sprite, SpatialAccessor.SCALE_XYZ, 0.5f)
                .target(0, 0, 0)
                .delay(0.05f)
                .setCallback(new TweenCallback() {
            public void onEvent(int i, BaseTween<?> bt) {
            }
        })
                .start(game.getBaseApplication().getTweenManager());
    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
    }

    public void physicsTick(PhysicsSpace space, float tpf) {

        if (left && !right) {
            rigidBodyControl.move(-tpf * moveSpeed, 0);

        } else if (!left && right) {
            rigidBodyControl.move(tpf * moveSpeed, 0);

        }

        //Correct the rotation so that the player don;t twist
        rigidBodyControl.getBody().setAngularVelocity(0);
        rigidBodyControl.getBody().setAngularDamping(0);
        rigidBodyControl.getBody().getTransform().setRotation(0);

    }
}
