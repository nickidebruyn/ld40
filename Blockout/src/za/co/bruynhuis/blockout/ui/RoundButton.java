/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;

/**
 *
 * @author NideBruyn
 */
public class RoundButton extends TouchButton {
    
    private static final float scale = 1f;

    public RoundButton(Panel panel, String id, String image) {
        super(panel, id, image, 64*scale, 64*scale, true);
        setText(" ");
        addEffect(new TouchEffect(this));
    }
    
}
