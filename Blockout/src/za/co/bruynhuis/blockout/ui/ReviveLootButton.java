/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.ColorUtils;

/**
 *
 * @author NideBruyn
 */
public class ReviveLootButton extends Panel {
    
    private static final float scale = 0.9f;
    private ControlButton controlButton;
    private Image lootImage;

    public ReviveLootButton(Panel parent) {
        super(parent, "Interface/button.png", 304*scale, 64*scale, true);
        
        setBackgroundColor(ColorUtils.hsv(0, 0, 0.85f));
        
        controlButton = new ControlButton(this, "revivebuttonaction", 304*scale, 64*scale, true);
        controlButton.addEffect(new TouchEffect(this));
        controlButton.setText("   Continue play = 100x");
        controlButton.setTextAlignment(TextAlign.LEFT);
        controlButton.setFontSize(20);
        controlButton.setTextColor(ColorUtils.rgb(255, 255, 255));
        
        lootImage = new Image(this, "Textures/star.png", 34, 34, true);
        lootImage.rightCenter(15, 0);
        lootImage.setBackgroundColor(ColorUtils.rgb(255, 255, 255));
        
        parent.add(this);
    }
    
    public void addTouchButtonListener(TouchButtonListener listener) {
        controlButton.addTouchButtonListener(listener);
    }
}
