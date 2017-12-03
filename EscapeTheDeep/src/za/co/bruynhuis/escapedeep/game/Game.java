/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.effects.WaveControl;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DPlayer;
import com.bruynhuis.galago.sprite.AnimatedSprite;
import com.bruynhuis.galago.sprite.Animation;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.util.Timer;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import za.co.bruynhuis.escapedeep.control.ChildControl;
import za.co.bruynhuis.escapedeep.control.EnemyControl;
import za.co.bruynhuis.escapedeep.control.FireballControl;
import za.co.bruynhuis.escapedeep.control.OceanControl;
import za.co.bruynhuis.escapedeep.control.PickupControl;
import za.co.bruynhuis.escapedeep.control.PlatformControl;

/**
 *
 * @author NideBruyn
 */
public class Game extends SimplePhysics2DGame {

    private float rowSpacing = 4;
    private float levelWidth = 14;
    private Timer nextLevelTimer = new Timer(100);
    private boolean canAddRow = true;
    private float lastHeight;
    private Timer fireballTimer = new Timer(500);
    private float enemygravity = 2.0f;
    private int difficulty = 0;
    private int levelCount = 0;
    private boolean intro;

    public Game(Base2DApplication baseApplication, Node rootNode, boolean intro) {
        super(baseApplication, rootNode);
        this.intro = intro;
    }

    @Override
    public void init() {
        startPosition = new Vector3f(0, 1, 0);

        if (!intro) {
            loadSky(0, 0);
            loadOcean(0, -12f);
            addGround(0, 0);

            for (int i = 1; i < 5; i++) {
                addPlatformRow(i * rowSpacing);
            }

            //A control for handling game specific update behaviour
            levelNode.addControl(new AbstractControl() {
                @Override
                protected void controlUpdate(float tpf) {

                    if (isStarted() && !isGameOver() && !isPaused()) {
//                    Debug.log("Last platform height: " + lastHeight);

                        nextLevelTimer.update(tpf);
                        if (nextLevelTimer.finished()) {
                            canAddRow = true;

                        }

                        fireballTimer.update(tpf);
                        if (fireballTimer.finished()) {
                            fireballTimer.setMaxTime(FastMath.nextRandomInt(300, 600));
                            int helfLevel = (int) (levelWidth / 2);
                            float x = FastMath.nextRandomInt(-helfLevel, helfLevel);
                            float y = lastHeight + 2f;
                            float vx = FastMath.nextRandomInt(6, 8) * 0.1f;
                            if (x > 0) {
                                vx = -vx;
                            }
                            float power = 6f;
                            float vy = -FastMath.nextRandomFloat();
                            addFireball(x, y, vx * power, vy * power);
                            fireballTimer.reset();

                        }
                    }
                }

                @Override
                protected void controlRender(RenderManager rm, ViewPort vp) {
                }
            });
        }

    }

    @Override
    public void start(SimplePhysics2DPlayer physicsPlayer) {
        super.start(physicsPlayer);
        if (!intro) {
            fireballTimer.start();
        }

    }

    public void addGround(float x, float y) {
        int startIndex = -(int) (levelWidth * 0.5f);
        int endIndex = (int) (levelWidth * 0.5f);

        for (int i = startIndex; i <= endIndex; i++) {

            addPlatform(i, y, 32);

        }

        lastHeight = y;

    }

    public void loadSky(float x, float y) {
        Sprite sky = new Sprite("sky", 26, 18);
        sky.setMaterial(baseApplication.getModelManager().getMaterial("Materials/lightning.j3m"));
        sky.move(x, y, -10f);
        addSky(sky, 1);
    }

    public void loadSky2(float x, float y) {
        Sprite sky = new Sprite("sky", 26, 18);
        sky.setImage("Textures/sky-linear.png");
        sky.move(x, y, -11f);
        addSky(sky, 1);
    }

    public void loadOcean(float x, float y) {
        Spatial ocean = baseApplication.getAssetManager().loadModel("Models/water.j3o");
        ocean.scale(3.3f);
        ocean.setQueueBucket(RenderQueue.Bucket.Transparent);
        WaveControl waveControl = new WaveControl("Textures/water.png", 3, 4, 0.04f);
        ocean.addControl(waveControl);
        waveControl.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        waveControl.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        ocean.addControl(new OceanControl(this, 10));

        ocean.move(x, y, 1f);
        levelNode.attachChild(ocean);
    }

    public void addPlatform(float x, float y, int index) {
        Sprite sprite = new Sprite("platform", 1f, 1f, 8, 8, index);
        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/tileset.j3m"));
        fixTexture(sprite.getMaterial().getTextureParam("ColorMap"));

        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        rigidBodyControl.setPhysicLocation(x, y);
        sprite.addControl(rigidBodyControl);

        sprite.addControl(new PlatformControl(this, 8));

        addTerrain(sprite);
    }

    public void addFireball(float x, float y, float vX, float vY) {
        Sprite sprite = new Sprite("platform", 1f, 1f, 8, 8, 31);
        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/tileset.j3m"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, -2f);

        Spatial fireball = baseApplication.getModelManager().getModel("Models/fireball.j3o");
        sprite.attachChild(fireball);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new CircleCollisionShape(sprite.getWidth() * 0.5f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        rigidBodyControl.setGravityScale(0.1f);
        rigidBodyControl.setSensor(true);
        rigidBodyControl.setPhysicLocation(x, y);
        rigidBodyControl.setLinearVelocity(vX, vY);
        sprite.addControl(rigidBodyControl);

        sprite.addControl(new FireballControl(this, 8));

        addStatic(rigidBodyControl);
    }

//    private void addPickup(float x, float y) {
//        Sprite sprite = new Sprite("pickup", 0.6f, 0.6f, 8, 8, 29);
//        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/tileset.j3m"));
//        sprite.move(x, y, 0f);
//
//        sprite.addControl(new PickupControl(this, 8));
//
//        addPickup(sprite);
//    }
    private void addBullet(float x, float y) {
        Sprite sprite = new Sprite("pickup", 0.4f, 0.4f, 8, 8, 27);
        sprite.setUserData("type", "bullet");
        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/tileset.j3m"));
        sprite.move(x, y, 0f);

        sprite.addControl(new PickupControl(this, 8));

        addPickup(sprite);
    }

    private void addBreakable(float x, float y, int index) {
        Sprite sprite = new Sprite("brick", 1f, 1f, 8, 8, index);
        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/tileset.j3m"));

        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        rigidBodyControl.setPhysicLocation(x, y);
        sprite.addControl(rigidBodyControl);

        sprite.addControl(new PlatformControl(this, 8));

        addStatic(rigidBodyControl);
    }

    private void addPlatformRow(float y) {

        int startIndex = -(int) (levelWidth * 0.5f);
        int endIndex = (int) (levelWidth * 0.5f);

        //Add platforms
        //*****************************************
        int doorIndex1 = FastMath.nextRandomInt(startIndex, endIndex);
        int doorIndex2 = FastMath.nextRandomInt(startIndex, endIndex);

        for (int i = startIndex; i <= endIndex; i++) {

            if (i == doorIndex1) {
                //DO nothing                    
            } else if (i == doorIndex2) {
                addBreakable(i, y, 40 + difficulty);

            } else {
                addPlatform(i, y, 32 + difficulty);
            }


        }

        //Add pickups
        //********************************************
        for (int i = startIndex; i <= endIndex; i++) {

            if (FastMath.nextRandomInt(0, 10) > 8) {
                if (FastMath.nextRandomInt(0, 10) > 5) {
                    addChild(i, y + 1f);
                }

            }

        }

        //Add bullets
        //********************************************
        if (FastMath.nextRandomInt(0, 1) > 0) {
            int bulletIndex = FastMath.nextRandomInt(startIndex, endIndex);
            for (int i = startIndex; i <= endIndex; i++) {

                if (i == bulletIndex) {
                    addBullet(i, y + 1f);
                }

            }
        }


        //Add Enemies
        //**********************************************
        if (FastMath.nextRandomInt(0, 1) > 0) {
            int enemyIndex = FastMath.nextRandomInt(startIndex, endIndex);
            for (int i = startIndex; i <= endIndex; i++) {

                if (i == enemyIndex) {
                    int index = FastMath.nextRandomInt(1, 3);
                    if (index == 1) {
                        addEnemy1(i, y + 1f);

                    } else if (index == 2) {
                        addEnemy2(i, y + 1f);

                    } else if (index == 3) {
                        addEnemy3(i, y + 1f);
                    }

                }

            }
        }


        lastHeight = y;
        levelCount++;

        if (difficulty < 6) {
            if (levelCount % 8 == 0) {
                difficulty++;
            }
        }


    }

    public void addNextRow() {
        if (canAddRow) {
            addPlatformRow(lastHeight + rowSpacing);
            canAddRow = false;
            nextLevelTimer.reset();
        }
    }

    public void addChild(float x, float y) {
        AnimatedSprite sprite = new AnimatedSprite(Platform2DGame.TYPE_PLAYER, 1f, 1f, 8, 8, 10);
        sprite.setMaterial(getBaseApplication().getModelManager().getMaterial("Materials/tileset.j3m"));
        fixTexture(sprite.getMaterial().getTextureParam("ColorMap"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        sprite.addAnimation(new Animation("walk", 48, 50, 6));

        //Add help image
        if (!intro) {
            Sprite help = new Sprite("help", 0.8f, 0.3f);
            help.setImage("Textures/help.png");
            fixTexture(help.getMaterial().getTextureParam("ColorMap"));
            help.move(0, 0.5f, 0);
            sprite.attachChild(help);
        }


        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(0.4f, 0.98f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        sprite.addControl(rigidBodyControl);
        rigidBodyControl.setPhysicLocation(new Vector3f(x, y, 0f));
        rigidBodyControl.setGravityScale(enemygravity);

        addEnemy(rigidBodyControl);
        int dirX = x < 0 ? 1 : -1;
        sprite.addControl(new ChildControl(this, sprite, 1f, new Vector3f(dirX, 0, 0), 1f, 8));

        sprite.play("walk", true, false, true);
    }

    private void addEnemy1(float x, float y) {
        AnimatedSprite sprite = new AnimatedSprite(Platform2DGame.TYPE_PLAYER, 1f, 1f, 8, 8, 10);
        sprite.setMaterial(getBaseApplication().getModelManager().getMaterial("Materials/tileset.j3m"));
        fixTexture(sprite.getMaterial().getTextureParam("ColorMap"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        sprite.addAnimation(new Animation("walk", 12, 14, 14));

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(0.6f, 0.9f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        sprite.addControl(rigidBodyControl);
        rigidBodyControl.setPhysicLocation(new Vector3f(x, y, 0f));
        rigidBodyControl.setGravityScale(enemygravity);

        addEnemy(rigidBodyControl);
        int dirX = x < 0 ? 1 : -1;
        sprite.addControl(new EnemyControl(this, sprite, 1.6f, new Vector3f(dirX, 0, 0), levelWidth * 0.5f, 8));

        sprite.play("walk", true, false, true);
    }

    private void addEnemy2(float x, float y) {
        AnimatedSprite sprite = new AnimatedSprite(Platform2DGame.TYPE_PLAYER, 1f, 1f, 8, 8, 10);
        sprite.setMaterial(getBaseApplication().getModelManager().getMaterial("Materials/tileset.j3m"));
        fixTexture(sprite.getMaterial().getTextureParam("ColorMap"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        sprite.addAnimation(new Animation("walk", 19, 21, 10));

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(0.6f, 0.9f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        sprite.addControl(rigidBodyControl);
        rigidBodyControl.setPhysicLocation(new Vector3f(x, y, 0f));
        rigidBodyControl.setGravityScale(enemygravity);

        addEnemy(rigidBodyControl);
        int dirX = x < 0 ? 1 : -1;
        sprite.addControl(new EnemyControl(this, sprite, 2.4f, new Vector3f(dirX, 0, 0), levelWidth * 0.5f, 8));

        sprite.play("walk", true, false, true);
    }

    private void addEnemy3(float x, float y) {
        AnimatedSprite sprite = new AnimatedSprite(Platform2DGame.TYPE_PLAYER, 1f, 1f, 8, 8, 10);
        sprite.setMaterial(getBaseApplication().getModelManager().getMaterial("Materials/tileset.j3m"));
        fixTexture(sprite.getMaterial().getTextureParam("ColorMap"));
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        sprite.addAnimation(new Animation("walk", 24, 26, 8));

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(0.6f, 0.9f), 1f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        sprite.addControl(rigidBodyControl);
        rigidBodyControl.setPhysicLocation(new Vector3f(x, y, 0f));
        rigidBodyControl.setGravityScale(enemygravity);

        addEnemy(rigidBodyControl);
        int dirX = x < 0 ? 1 : -1;
        sprite.addControl(new EnemyControl(this, sprite, 3, new Vector3f(dirX, 0, 0), levelWidth * 0.5f, 8));

        sprite.play("walk", true, false, true);
    }
}
