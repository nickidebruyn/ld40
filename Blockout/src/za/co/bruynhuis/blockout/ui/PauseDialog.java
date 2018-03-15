/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.ui;

import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.bruynhuis.galago.util.ColorUtils;

/**
 *
 * @author NideBruyn
 */
public class PauseDialog extends PopupDialog {
    
    private LargeButton resumeButton;
    private LargeButton retryButton;
    private LargeButton menuButton;

    public PauseDialog(Window window) {
        super(window, "Interface/dialog.png", 530, 600, true);
        
        setTitle("PAUSED");
        setTitleColor(ColorUtils.rgb(255, 255, 255));
        setTitleSize(38);
        title.centerTop(0, 20);
        
        resumeButton = new LargeButton(this, "resumebutton", "CONTINUE", ColorUtils.hsv(0.57f, 0.88f, .73f));
        resumeButton.centerAt(0, 80);
        
        retryButton = new LargeButton(this, "retryButton", "RETRY", ColorUtils.hsv(0.95f, 0.85f, .92f));
        retryButton.centerAt(0, 0);
        
        menuButton = new LargeButton(this, "menuButton", "MENU", ColorUtils.hsv(0.83f, 0.7f, .7f));
        menuButton.centerAt(0, -80);
    }
    
    public void addResumeButtonListener(TouchButtonListener listener) {
        resumeButton.addTouchButtonListener(listener);
    }
    
    public void addRetryButtonListener(TouchButtonListener listener) {
        retryButton.addTouchButtonListener(listener);
    }
    
    public void addMenuButtonListener(TouchButtonListener listener) {
        menuButton.addTouchButtonListener(listener);
    }
}
