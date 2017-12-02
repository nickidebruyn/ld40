/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.game;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.simplephysics2d.SimplePhysics2DGame;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import za.co.bruynhuis.escapedeep.control.OceanControl;

/**
 *
 * @author NideBruyn
 */
public class Game extends SimplePhysics2DGame {
    
    private Sprite oceanSprite;

    public Game(Base2DApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);
    }

    @Override
    public void init() {
        startPosition = new Vector3f(0, 1, 0);
        loadOcean(0, -10f);
        addGround(0, 0);
    }
    
    private void addGround(float x, float y) {
        Sprite sprite = new Sprite("ground", 10, 1);
        sprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/ground.j3m"));
        
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.move(x, y, 0f);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(new BoxCollisionShape(sprite.getWidth(), sprite.getHeight()), 0f);
        rigidBodyControl.setRestitution(0f);
        rigidBodyControl.setFriction(0.1f);
        rigidBodyControl.setPhysicLocation(x, y);
        sprite.addControl(rigidBodyControl);
        
        addTerrain(sprite);
    }
    
    private void loadOcean(float x, float y) {
        oceanSprite = new Sprite("ground", 14, 20);
        oceanSprite.setMaterial(baseApplication.getModelManager().getMaterial("Materials/ocean.j3m"));        
        oceanSprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        oceanSprite.move(x, y, 1f);      
        addVegetation(oceanSprite);
        
        oceanSprite.addControl(new OceanControl(this, 10));
    }
    
}
