/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.control;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import za.co.bruynhuis.escapedeep.game.Game;

/**
 *
 * @author NideBruyn
 */
public class FireballControl extends AbstractControl {
    
    private Game game;
    private final float killDistance;
    private RigidBodyControl rigidBodyControl;

    public FireballControl(Game game, float killDistance) {
        this.game = game;
        this.killDistance = killDistance;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {
            
            if (rigidBodyControl == null) {
                rigidBodyControl = spatial.getControl(RigidBodyControl.class);
            }
            
//            Debug.log("Distance form water: " + spatial.getWorldTranslation().distance(game.getPlayer().getPosition()));
            
            if (game.getPlayer().getPosition().y > rigidBodyControl.getPhysicLocation().y &&
                    game.getPlayer().getPosition().y - rigidBodyControl.getPhysicLocation().y >= killDistance) {
                game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
                spatial.removeFromParent();  
            }
            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
