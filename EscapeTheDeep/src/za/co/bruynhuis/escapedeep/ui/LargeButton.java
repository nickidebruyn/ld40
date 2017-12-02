/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.ui;

import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class LargeButton extends TouchButton {

    public LargeButton(Panel panel, String id, String text) {
        super(panel, id, "Interface/button.png", 240, 60, new FontStyle(26), true);
        setTextColor(ColorRGBA.DarkGray);
        setText(text);
        addEffect(new TouchEffect(this));
    }   
    
    
}
