/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.ui;

import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 *
 * @author NideBruyn
 */
public class GameOverDialog extends PopupDialog {
    
    private Label info;
    private Label scoreLabel;
    private Label bestLabel;
    private LargeButton exitButton;
    private LargeButton playButton;
    private Image playerHappyImage;
    private Image playerSadImage;

    public GameOverDialog(Window window) {
        super(window, "Interface/panel-large.png", window.getWidth(), window.getHeight(), true);
        
        setTitle("GAME OVER");
        title.setFontSize(32);
        title.setTextColor(ColorRGBA.White);
        title.centerAt(0, 210);
        
        info = new Label(this, "Oh no, maybe next time\n you can save this little guy.", 300, 300, new FontStyle(16));
        info.setTextColor(ColorRGBA.Orange);
        info.centerAt(0, 140);
        
        Label label = new Label(this, "SCORE: ", 100, 30, new FontStyle(16));
        label.setTextColor(ColorRGBA.White);
        label.setAlignment(TextAlign.RIGHT);
        label.centerAt(-60, -80);
        
        scoreLabel = new Label(this, "0", 100, 30, new FontStyle(16));
        scoreLabel.setTextColor(ColorRGBA.Yellow);
        scoreLabel.setAlignment(TextAlign.LEFT);
        scoreLabel.centerAt(60, -80);
        
        label = new Label(this, "BEST: ", 100, 30, new FontStyle(16));
        label.setTextColor(ColorRGBA.White);
        label.setAlignment(TextAlign.RIGHT);
        label.centerAt(-60, -120);
        
        bestLabel = new Label(this, "0", 100, 30, new FontStyle(16));
        bestLabel.setTextColor(ColorRGBA.LightGray);
        bestLabel.setAlignment(TextAlign.LEFT);
        bestLabel.centerAt(60, -120);
        
        playerSadImage = new Image(this, "Interface/playersad.png", 120, 120, true);
        playerSadImage.centerAt(0, 10);
        fixTexture(playerSadImage.getPicture().getMaterial().getTextureParam("Texture"));
        
        playerHappyImage = new Image(this, "Interface/playerhappy.png", 120, 120, true);
        playerHappyImage.centerAt(0, 10);
        fixTexture(playerHappyImage.getPicture().getMaterial().getTextureParam("Texture"));
        
        SpatialUtils.bounce(playerHappyImage.getWidgetNode(), playerHappyImage.getPosition().x, playerHappyImage.getPosition().y + 40, playerHappyImage.getPosition().z, 0.3f, 0, 100);
        
        exitButton = new LargeButton(this, "exitbutton", "Exit");
        exitButton.centerAt(-200, -200);
        
        playButton = new LargeButton(this, "TryButton", "Retry");
        playButton.centerAt(200, -200);
    }
    
    private void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }
    
    public void addExitButtonListener(TouchButtonListener buttonListener) {
        exitButton.addTouchButtonListener(buttonListener);
    }
    
    public void addPlayButtonListener(TouchButtonListener buttonListener) {
        playButton.addTouchButtonListener(buttonListener);
    }
    
    public void show(int best, int score) {
        super.show();
        
        this.bestLabel.setText("" + best);
        this.scoreLabel.setText("" + score);
        
        if (best >= score) {
            playerSadImage.setVisible(true);
            playerHappyImage.setVisible(false);
            setTitle("GAME OVER");
            
        } else {
            playerSadImage.setVisible(false);
            playerHappyImage.setVisible(true);
            setTitle("HIGH SCORE");
        }
    }
    
    
}
