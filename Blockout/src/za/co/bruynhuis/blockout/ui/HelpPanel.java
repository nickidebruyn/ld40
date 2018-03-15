/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author NideBruyn
 */
public class HelpPanel extends Panel {
    
    private Label infoLabel;

    public HelpPanel(Panel parent) {
        super(parent, null, parent.getWindow().getWidth(), parent.getWindow().getHeight());
        
        infoLabel = new Label(this, "Touch screen to shoot.", 20, 470, 100);
        infoLabel.setTextColor(ColorRGBA.Gray);
        infoLabel.setAlignment(TextAlign.CENTER);
        infoLabel.setVerticalAlignment(TextAlign.CENTER);
        infoLabel.centerAt(0, -180);
        
        parent.add(this);
        
    }
    

    public void show(String text) {        
        infoLabel.setText(text);
        center();
        Vector3f pos = getPosition().clone();
        super.show();
        this.setTransparency(0);
        Tween.to(this, WidgetAccessor.OPACITY, 2f)
                .target(1f)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        setTransparency(1);
                        hide();
                    }
                })
                .start(window.getApplication().getTweenManager());
        
        centerBottom(0, -window.getHeight());
        Tween.to(this, WidgetAccessor.POS_XY, 1f)
                .target(pos.x, pos.y)
                .start(window.getApplication().getTweenManager());
    }
    
    @Override
    public void hide() {
        setTransparency(1);
        Tween.to(this, WidgetAccessor.OPACITY, 1f)
                .target(0f)
                .delay(1f)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        setTransparency(0);
                        HelpPanel.super.hide();
                        
                    }
                })
                .start(window.getApplication().getTweenManager());
        
    }    
    
    
}
