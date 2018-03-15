/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class LargeButton extends TouchButton {
    
    private static final float scale = 0.9f;

    public LargeButton(Panel panel, String id, String text, ColorRGBA colorRGBA) {
        super(panel, id, "Interface/button.png", 304*scale, 64*scale, true);
        setText(text);
        setTextColor(ColorUtils.rgb(255, 255, 255));
        setFontSize(34*scale);
        setBackgroundColor(colorRGBA);
        addEffect(new TouchEffect(this));
    }
    
}
