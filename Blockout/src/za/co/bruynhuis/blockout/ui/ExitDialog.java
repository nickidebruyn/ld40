/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.ui;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.bruynhuis.galago.util.ColorUtils;

/**
 *
 * @author NideBruyn
 */
public class ExitDialog extends PopupDialog {
    
    private LargeButton yesButton;
    private LargeButton noButton;
    private LargeButton rateButton;
    private Label infoLabel;

    public ExitDialog(Window window) {
        super(window, "Interface/dialog.png", 530, 500, true);
        
        setTitle("Quit Game");
        setTitleColor(ColorUtils.rgb(255, 255, 255));
        setTitleSize(38);
        title.centerTop(0, 20);
        
        infoLabel = new Label(this, "Do you want to quit the game?", 20, 420, 80);
        infoLabel.setTextColor(ColorUtils.rgb(255, 255, 255));
        infoLabel.centerAt(0, 120);
        
        rateButton = new LargeButton(this, "rateButton", "RATE", ColorUtils.hsv(0.57f, 0.88f, .73f));
        rateButton.centerAt(0, 20);
        
        yesButton = new LargeButton(this, "yesButton", "QUIT", ColorUtils.hsv(0.48f, 1f, .65f));
        yesButton.centerAt(0, -120);
        
        noButton = new LargeButton(this, "noButton", "PLAY", ColorUtils.hsv(0.95f, 0.85f, .92f));
        noButton.centerAt(0, -50);
        
    }
    
    public void addYesButtonListener(TouchButtonListener listener) {
        yesButton.addTouchButtonListener(listener);
    }
    
    public void addNoButtonListener(TouchButtonListener listener) {
        noButton.addTouchButtonListener(listener);
    }
    
    public void addRateButtonListener(TouchButtonListener listener) {
        rateButton.addTouchButtonListener(listener);
    }

    @Override
    public void show() {
        super.show();
        rateButton.setVisible(!window.getApplication().getGameSaves().getGameData().isRated());
    }
    
    
}
