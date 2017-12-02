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
public class PickupControl extends AbstractControl {
    
    private Game game;
    private final float killDistance;

    public PickupControl(Game game, float killDistance) {
        this.game = game;
        this.killDistance = killDistance;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        if (game.isStarted() && !game.isGameOver() && !game.isPaused()) {

            if (game.getPlayer().getPosition().y > spatial.getLocalTranslation().y &&
                    game.getPlayer().getPosition().y - spatial.getLocalTranslation().y >= killDistance) {
                doDestroy();
            }
            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    public void doDestroy() {
        spatial.removeFromParent();

    }
}
