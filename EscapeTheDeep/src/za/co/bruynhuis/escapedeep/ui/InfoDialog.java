/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class InfoDialog extends PopupDialog {
    
    protected ShowListener showListener;
    
    
    private Label info;
    private LargeButton exitButton;
    private LargeButton playButton;

    private Image movementImage;
    private Image attackImage;

    public InfoDialog(Window window) {
        super(window, "Interface/panel-large.png", window.getWidth(), window.getHeight(), true);
        
        setTitle("HOW TO PLAY");
        title.setFontSize(32);
        title.setTextColor(ColorRGBA.White);
        title.centerAt(0, 210);
        
        info = new Label(this, "Save as many children as you can. \nJump and go up. Do not drown.\nHere are the controls.", 300, 300, new FontStyle(16));
        info.setTextColor(ColorRGBA.Orange);
        info.centerAt(0, 140);
        
        movementImage = new Image(this, "Interface/movement.png", 300, 200, true);
        movementImage.centerAt(-160, -20);
        
        attackImage = new Image(this, "Interface/attack.png", 300, 100, true);
        attackImage.centerAt(160, -70);
        
        exitButton = new LargeButton(this, "exitbutton", "Exit");
        exitButton.centerAt(-200, -200);
        
        playButton = new LargeButton(this, "playButton", "Play");
        playButton.centerAt(200, -200);
    }
    
    public void addExitButtonListener(TouchButtonListener buttonListener) {
        exitButton.addTouchButtonListener(buttonListener);
    }
    
    public void addPlayButtonListener(TouchButtonListener buttonListener) {
        playButton.addTouchButtonListener(buttonListener);
    }
    
    public void addShowListener(ShowListener showListener) {
        this.showListener = showListener;
        
    }
    
    protected void fireShowShownListener() {
        if (showListener != null) {
            showListener.shown();
        }
    }
    
    protected void fireShowHiddenListener() {
        if (showListener != null) {
            showListener.hidden();
        }
    }
    
    public void show(int best, int score) {
        super.show();
        
        setTransparency(0);
        
        Tween.to(this, WidgetAccessor.OPACITY, 2f)
                .target(1f)
                .setCallback(new TweenCallback() {
                    public void onEvent(int i, BaseTween<?> bt) {
                        fireShowShownListener();
                    }
                })
                .start(window.getApplication().getTweenManager());    
    }
}
