/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.effects.WaveControl;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
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
import za.co.bruynhuis.escapedeep.control.OceanControl;
import za.co.bruynhuis.escapedeep.control.PlatformControl;

/**
 *
 * @author NideBruyn
 */
public class Game extends SimplePhysics2DGame {

    private Sprite oceanSprite;
    private float rowSpacing = 4;
    private float levelWidth = 14;
    private Timer nextLevelTimer = new Timer(100);
    private boolean canAddRow = true;
    private float lastHeight;

    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    @Override
    public void init() {
        startPosition = new Vector3f(0, 1, 0);
        loadSky(0, 0);
        loadOcean(0, -12f);
        addGround(0, 0);

        for (int i = 1; i < 4; i++) {
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
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
    }

    private void addGround(float x, float y) {
        int startIndex = -(int) (levelWidth * 0.5f);
        int endIndex = (int) (levelWidth * 0.5f);

        for (int i = startIndex; i <= endIndex; i++) {

            addPlatform(i, y);

        }

        lastHeight = y;

    }
    
    private void loadSky(float x, float y) {
        Sprite sky = new Sprite("sky", 26, 20);
        sky.setMaterial(baseApplication.getModelManager().getMaterial("Materials/lightning.j3m"));
        sky.move(x, y, -10f);
        addSky(sky, 1);
    }

    private void loadOcean(float x, float y) {
//        oceanSprite = new Sprite("ground", levelWidth * 2, 20);
//        oceanSprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/ocean.j3m"));
//        oceanSprite.setQueueBucket(RenderQueue.Bucket.Transparent);
//        oceanSprite.move(x, y, 1f);
//        addVegetation(oceanSprite);
//        oceanSprite.addControl(new OceanControl(this, 10));
        
        Spatial ocean = baseApplication.getAssetManager().loadModel("Models/water.j3o");
        ocean.scale(3.3f);
//        ocean.setQueueBucket(RenderQueue.Bucket.Transparent);
        WaveControl waveControl = new WaveControl("Textures/water.png", 3, 4, 0.04f);
        ocean.addControl(waveControl);
        waveControl.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.PremultAlpha);
        waveControl.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        ocean.addControl(new OceanControl(this, 10));
        
        ocean.move(x, y, 1f);
        levelNode.attachChild(ocean);
    }

    private void addPlatform(float x, float y) {
        Sprite sprite = new Sprite("platform", 1f, 1f);
        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/platform.j3m"));

        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        rigidBodyControl.setPhysicLocation(x, y);
        sprite.addControl(rigidBodyControl);

        sprite.addControl(new PlatformControl(this, 6));

        addTerrain(sprite);
    }

    private void addDoor(float x, float y) {
//        Sprite sprite = new Sprite("platform", 1f, 1f);
//        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/platform.j3m"));
//        
//        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
//        sprite.move(x, y, 0f);
//
//        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0f);
//        rigidBodyControl.setRestitution(0f);
//        rigidBodyControl.setFriction(0.1f);
//        rigidBodyControl.setPhysicLocation(x, y);
//        sprite.addControl(rigidBodyControl);
//        
//        sprite.addControl(new PlatformControl(this, 5));
//        
//        addTerrain(sprite);
    }

    private void addPlatformRow(float y) {

        int startIndex = -(int) (levelWidth * 0.5f);
        int endIndex = (int) (levelWidth * 0.5f);
        int doorIndex = FastMath.nextRandomInt(startIndex, endIndex);

        for (int i = startIndex; i <= endIndex; i++) {

            if (i == doorIndex) {
                addDoor(i, y);
            } else {
                addPlatform(i, y);
            }

        }

        lastHeight = y;
        
    }
    
    public void addNextRow() {
        if (canAddRow) {
            addPlatformRow(lastHeight + rowSpacing);
            canAddRow = false;
            nextLevelTimer.reset();
        }
    }
}
