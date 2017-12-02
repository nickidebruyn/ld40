/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.escapedeep.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.texture.Texture;

/**
 *
 * @author NideBruyn
 */
public class PlayerScorePanel extends Panel {
    
    private Label scoreLabel;
    private Label bulletLabel;
    private Image bulletImage;

    public PlayerScorePanel(Panel parent) {
        super(parent, null, parent.getWidth(), 60);
        
        scoreLabel = new Label(this, "Score: 0", 22);
        scoreLabel.setAlignment(TextAlign.LEFT);
        scoreLabel.leftCenter(10, 0);
        
        bulletLabel = new Label(this, "Bullet", 22);
        bulletLabel.setAlignment(TextAlign.RIGHT);
        bulletLabel.rightCenter(10, 0);
        
        bulletImage = new Image(this, "Interface/bullet.png", 46, 46, true);
        bulletImage.rightCenter(160, 0);
        fixTexture(bulletImage.getPicture().getMaterial().getTextureParam("Texture"));
        
        parent.add(this);
    }
    
    private void fixTexture(MatParam mp) {
        if (mp != null) {
            MatParamTexture mpt = (MatParamTexture) mp;
            mpt.getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }
    
    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }
    
    public void setBulletActive(boolean visible) {
        bulletImage.setVisible(visible);
        bulletLabel.setVisible(visible);
    }
}
