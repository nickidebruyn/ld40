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
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
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

    private AnimatedSprite sprite;
    private RigidBodyControl rigidBodyControl;
    private float jumpForce = 14.0f;
    private boolean onGround = false;
    private boolean left, right = false;
    private float moveSpeed = 4f;
    private Timer jumpDelayTimer = new Timer(0.5f);
    private float gravity = 2.5f;

    public Player(SimplePhysics2DGame simplePhysics2DGame) {
        super(simplePhysics2DGame);
    }

    @Override
    protected void init() {
        lives = 0;

        sprite = new AnimatedSprite(Platform2DGame.TYPE_PLAYER, 1.2f, 1.2f, 4, 3, 10);
        sprite.setMaterial(game.getBaseApplication().getModelManager().getMaterial("Materials/player.j3m"));
        game.fixTexture(sprite.getMaterial().getTextureParam("ColorMap"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(0, 0, 0f);

        sprite.addAnimation(new Animation("idle", 0, 4, 10));
        sprite.addAnimation(new Animation("walk", 5, 9, 10));
        sprite.addAnimation(new Animation("jump", 10, 10, 10));

        rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(0.6f, 1f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        playerNode.addControl(rigidBodyControl);
        playerNode.attachChild(sprite);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        rigidBodyControl.setPhysicLocation(new Vector3f(startPosition.x, startPosition.y, 0f));
        rigidBodyControl.setGravityScale(gravity);

        loadWeather();

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

                    
                    if (left && !right) {
                        sprite.flipHorizontal(true);

                        if (onGround) {
                            sprite.play("walk", true, false, true);
                        } else {
                            sprite.play("jump", false, false, true);
                        }

                    } else if (!left && right) {

                        sprite.flipHorizontal(false);
                        if (onGround) {
                            sprite.play("walk", true, false, true);
                        } else {
                            sprite.play("jump", false, false, true);
                        }

                    } else {
                        if (onGround) {
                            sprite.play("idle", true, false, true);
                        } else {
                            sprite.play("jump", true, false, true);
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

        sprite.play("idle", true, false, true);
    }

    private void loadWeather() {
        Spatial rain = game.getBaseApplication().getModelManager().getModel("Models/rain.j3o");
        rain.setLocalTranslation(0, 10, 2);
        playerNode.attachChild(rain);
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
//        if (point.y <= getPosition().y - dist) {

        if (jumpDelayTimer.finished()) {
            onGround = true;
        }

//        }

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
        return sprite.getWidth();
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
