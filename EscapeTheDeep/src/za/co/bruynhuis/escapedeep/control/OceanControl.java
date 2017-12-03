/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.control;

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
            
            if (game.getPlayer().getPosition().y - spatial.getWorldTranslation().y <= killDistance + 3) {
                spatial.move(0, tpf*moveSpeed, 0);
                
            } else {
                spatial.move(0, tpf*moveSpeed*2.5f, 0); //Add more to the speed so that the water can keep up with the player
                
            }
            
            
//            Debug.log("Distance form water: " + spatial.getWorldTranslation().distance(game.getPlayer().getPosition()));
            
            if (game.getPlayer().getPosition().y - spatial.getWorldTranslation().y <= killDistance) {
                game.getPlayer().doDamage(5);
                
            }
            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
}
