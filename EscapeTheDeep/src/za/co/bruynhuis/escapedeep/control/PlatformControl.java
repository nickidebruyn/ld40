/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.control;

import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.util.Timer;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import za.co.bruynhuis.escapedeep.game.Game;

/**
 *
 * @author NideBruyn
 */
public class PlatformControl extends AbstractControl {
    
    private Game game;
    private final float killDistance;
    private RigidBodyControl rigidBodyControl;
    private boolean destroyed = false;
    private Timer destroyTimer = new Timer(20);

    public PlatformControl(Game game, float killDistance) {
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
                game.addNextRow();
            }
            
            
            destroyTimer.update(tpf);
            if (destroyTimer.finished()) {
                game.getBaseApplication().getDyn4jAppState().getPhysicsSpace().remove(rigidBodyControl);
                spatial.removeFromParent();      
                destroyTimer.stop();
                
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    public void doDestroy() {
        
        if (!destroyed) {
            game.getBaseApplication().getEffectManager().doEffect("bump", rigidBodyControl.getPhysicLocation().clone());
            game.getBaseApplication().getSoundManager().playSound("bumptop");
            spatial.setCullHint(Spatial.CullHint.Always);
            destroyed = true;
            destroyTimer.start();
        }
        
    }
}
