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
public class RewardAdButton extends Panel {
    
    private static final float scale = 0.9f;
    private ControlButton controlButton;
    private Image videoImage;
    private Image lootImage;

    public RewardAdButton(Panel parent) {
        super(parent, "Interface/button.png", 304*scale, 64*scale, true);
        
        setBackgroundColor(ColorUtils.hsv(0, 0, 0.85f));
        
        controlButton = new ControlButton(this, "reviveadbuttonaction", 304*scale, 64*scale, true);
        controlButton.addEffect(new TouchEffect(this));
        controlButton.setText("x 100     watch");
        controlButton.setTextAlignment(TextAlign.LEFT);
        controlButton.setFontSize(20*scale);
        controlButton.setTextColor(ColorUtils.rgb(255, 255, 255));
        controlButton.leftCenter(60, 0);
        
        lootImage = new Image(this, "Textures/star.png", 34, 34, true);
        lootImage.leftCenter(25, 0);
        lootImage.setBackgroundColor(ColorUtils.rgb(255, 255, 255));

        
        videoImage = new Image(this, "Interface/video-icon.png", 42, 42, true);
        videoImage.rightCenter(25, 0);
        videoImage.setBackgroundColor(ColorUtils.rgb(255, 255, 255));
       
        
        parent.add(this);
    }
    
    public void addTouchButtonListener(TouchButtonListener listener) {
        controlButton.addTouchButtonListener(listener);
    }
}
