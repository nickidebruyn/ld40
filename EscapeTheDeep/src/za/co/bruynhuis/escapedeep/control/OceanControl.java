/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import za.co.bruynhuis.escapedeep.game.Game;

/**
 *
 * @author NideBruyn
 */
public class OceanControl extends AbstractControl {
    
    private Game game;
    private float moveSpeed = 0.5f;
    private final float killDistance;

    public OceanControl(Game game, float killDistance) {
        this.game = game;
        this.killDistance = killDistance;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {
            
            spatial.move(0, tpf*moveSpeed, 0);
            
//            Debug.log("Distance form water: " + spatial.getWorldTranslation().distance(game.getPlayer().getPosition()));
            
            if (spatial.getWorldTranslation().distance(game.getPlayer().getPosition()) <= killDistance) {
                game.doGameOver();
                
            } else {
                spatial.setLocalTranslation(
                        spatial.getLocalTranslation().clone().interpolate(
                        new Vector3f(0, game.getPlayer().getPosition().y, spatial.getLocalTranslation().z).subtract(0, killDistance, 0), 0.005f));
            }
            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
}
