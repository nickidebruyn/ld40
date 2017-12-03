/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.game;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.AnimationListener;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.PhysicsCollisionListener;
import com.bruynhuis.galago.sprite.physics.PhysicsSpace;
import com.bruynhuis.galago.sprite.physics.PhysicsTickListener;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.util.Debug;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import za.co.bruynhuis.escapedeep.control.BulletControl;

/**
 *
 * @author NideBruyn
 */
public class Player extends SimplePhysics2DPlayer implements PhysicsCollisionListener, PhysicsTickListener {

    private AnimatedSprite sprite;
    private RigidBodyControl rigidBodyControl;
    private float jumpForce = 16.0f;
    private boolean onGround = false;
    private boolean left, right = false;
    private float moveSpeed = 4f;
    private Timer jumpDelayTimer = new Timer(0.5f);
    private float gravity = 1.8f;
    private Timer walkTimer = new Timer(15f);
    private boolean walking = false;
    private boolean hasBullet = false;
    private boolean shooting = false;
    private boolean intro;

    public Player(SimplePhysics2DGame simplePhysics2DGame, boolean intro) {
        super(simplePhysics2DGame);
        this.intro = intro;
    }

    @Override
    protected void init() {
        lives = 0;

        sprite = new AnimatedSprite(Platform2DGame.TYPE_PLAYER, 1.2f, 1.2f, 8, 8, 10);
        sprite.setMaterial(game.getBaseApplication().getModelManager().getMaterial("Materials/tileset.j3m"));
        game.fixTexture(sprite.getMaterial().getTextureParam("ColorMap"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(0, 0, 0f);

        sprite.addAnimation(new Animation("idle", 0, 2, 20));
        sprite.addAnimation(new Animation("walk", 3, 5, 6));
        sprite.addAnimation(new Animation("jump", 3, 3, 10));
        sprite.addAnimation(new Animation("attack", 6, 8, 10));
        sprite.addAnimation(new Animation("die", 8, 8, 1));
        sprite.addAnimationListener(new AnimationListener() {
            public void animationStart(Animation animation) {
            }

            public void animationDone(Animation animation) {
                if (animation.getName().equals("attack")) {
                    if (onGround) {
                        sprite.play("idle", true, false, true);
                        shooting = false;
                    }

                }
            }
        });

        rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(0.8f, 1.1f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        playerNode.addControl(rigidBodyControl);
        playerNode.attachChild(sprite);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().add(playerNode);
        rigidBodyControl.setPhysicLocation(new Vector3f(startPosition.x, startPosition.y, 0f));
        rigidBodyControl.setGravityScale(gravity);

        if (!intro) {
            loadWeather();
        }
        

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

                    if (onGround && (left || right)) {
                        walking = true;
                    } else {
                        walking = false;
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
                        if (onGround && !shooting) {
                            sprite.play("idle", true, false, true);

                        } else {
                            sprite.play("jump", true, false, true);
                        }
                    }

                    if (walking) {
                        walkTimer.update(tpf);
                        if (walkTimer.finished()) {
                            game.getBaseApplication().getSoundManager().playSound("walk");
                            walkTimer.reset();
                        }
                    }
                } else if (game.isGameOver()) {
                    sprite.play("die", true, false, true);

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
//        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(playerNode);
//        game.getBaseApplication().getEffectManager().doEffect("die", getPosition());
        game.getBaseApplication().getEffectManager().doEffect("blood", getPosition().clone());
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsCollisionListener(this);
        game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().removePhysicsTickListener(this);

        rigidBodyControl.setSensor(true);
        rigidBodyControl.clearForces();
        rigidBodyControl.applyImpulse(0, 10f);
        
        SpatialUtils.slerp(sprite, 0, 0, 90, 0.8f, 0, false);


//
//        Tween.to(sprite, SpatialAccessor.SCALE_XYZ, 0.15f)
//                .target(0, 0, 0)
//                .delay(0.005f)
//                .setCallback(new TweenCallback() {
//            public void onEvent(int i, BaseTween<?> bt) {
//            }
//        })
//                .start(game.getBaseApplication().getTweenManager());

//        rigidBodyControl.clearForces();


    }

    public void jump(float extrajumpForcePer) {

        if (onGround && !game.isGameOver()) {
            game.getBaseApplication().getSoundManager().playSound("jump");
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
        walkTimer.start();

    }

    public boolean isFacingForward() {
        return !sprite.isHorizontalFlipped();
    }

    public int getLives() {
        return lives;
    }

    @Override
    protected float getSize() {
        return sprite.getWidth() * 0.6f;
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

        if (game.isStarted() && !game.isGameOver()) {

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

    public void setPosition(Vector3f pos) {
        rigidBodyControl.setPhysicLocation(pos);
    }
    
    public void attack() {
        Debug.log("Attack");

        if (isFacingForward()) {
            //Shoot right
            shootBullet(new Vector3f(1, 0, 0));
            rigidBodyControl.applyImpulse(-2, 0);
        } else {
            //shoot left
            shootBullet(new Vector3f(-1, 0, 0));
            rigidBodyControl.applyImpulse(2, 0);
        }

        hasBullet = false;
        shooting = true;
    }

    /**
     * Initialize a bullet and shoots it in a direction.
     *
     * @param direction
     */
    private void shootBullet(Vector3f direction) {
        game.getBaseApplication().getSoundManager().playSound("shoot");

        Sprite bulletsprite = new Sprite(Game.TYPE_BULLET, 0.5f, 0.5f, 8, 8, 27);
        bulletsprite.setMaterial(game.getBaseApplication().getModelManager().getMaterial("Materials/tileset.j3m"));
        game.fixTexture(bulletsprite.getMaterial().getTextureParam("ColorMap"));
        bulletsprite.addControl(new RotationControl(new Vector3f(0, 0, 20)));

        RigidBodyControl bodyControl = new RigidBodyControl(new CircleCollisionShape(0.1f), 0.1f);
        bodyControl.setRestitution(0);
        bodyControl.setFriction(1f);
        bodyControl.getBody().setGravityScale(0.01f);
        bulletsprite.addControl(bodyControl);
        bodyControl.setPhysicLocation(getPosition().add(direction.x * 0.6f, direction.y * 0.6f, 0));
        game.addBullet(bodyControl);
        bulletsprite.addControl(new BulletControl((Game) game, bulletsprite, 10, direction, 5));

        sprite.play("attack", false, false, true);
    }

    public boolean isHasBullet() {
        return hasBullet;
    }

    public void setHasBullet(boolean hasBullet) {
        this.hasBullet = hasBullet;
    }
}
