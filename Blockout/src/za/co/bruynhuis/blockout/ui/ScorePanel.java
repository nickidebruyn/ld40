/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.tween.WidgetAccessor;
import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.math.Vector3f;

/**
 *
 * @author NideBruyn
 */
public class ScorePanel extends Panel {

    private Label scoreLabel;
    private Label bestLabel;
    private Label bestScoreLabel;
    private Label lootLabel;
    private Image lootImage;
    private TouchButton infoButton;

    public ScorePanel(Panel parent, boolean showScore, boolean showInfo) {
        super(parent, null, parent.getWindow().getWidth(), 70);

//        setTransparency(0.8f);

        if (showScore) {
            scoreLabel = new Label(this, "0", 48, 200, 70);
            scoreLabel.setAlignment(TextAlign.CENTER);
            scoreLabel.setVerticalAlignment(TextAlign.CENTER);
            scoreLabel.centerAt(0, 5);

        }

        if (showInfo) {
            infoButton = new TouchButton(this, "info", "Interface/info-icon.png", 48, 48, true);
            infoButton.leftCenter(10, 0);
            infoButton.addEffect(new TouchEffect(infoButton));

        } else {
            bestLabel = new Label(this, "BEST", 16, 100, 60);
            bestLabel.setAlignment(TextAlign.LEFT);
            bestLabel.setVerticalAlignment(TextAlign.CENTER);
            bestLabel.leftCenter(10, 15);

            bestScoreLabel = new Label(this, "0", 34, 100, 60);
            bestScoreLabel.setAlignment(TextAlign.LEFT);
            bestScoreLabel.setVerticalAlignment(TextAlign.CENTER);
            bestScoreLabel.leftCenter(10, -14);

        }

        lootLabel = new Label(this, "0", 24, 100, 60);
        lootLabel.setAlignment(TextAlign.RIGHT);
        lootLabel.setVerticalAlignment(TextAlign.CENTER);
        lootLabel.rightCenter(50, 0);

        lootImage = new Image(this, "Textures/star.png", 32, 32, true);
        lootImage.rightCenter(5, 0);
        lootImage.setBackgroundColor(ColorUtils.rgb(255, 255, 255));

        parent.add(this);

    }
    
    public void addInfoButtonListener(TouchButtonListener listener) {
        infoButton.addTouchButtonListener(listener);
    }

    public void updateScore(int score) {
        scoreLabel.setText("" + score);

    }

    public void updateLoot(int loot) {
        lootLabel.setText(loot + " x ");

    }
    
    public void updateValues() {
        if (bestScoreLabel != null) {
            bestScoreLabel.setText("" + window.getApplication().getGameSaves().getGameData().getScore());
        }
        
        updateLoot(window.getApplication().getGameSaves().getGameData().getLevel());
    }

    @Override
    public void show() {
        updateValues();

        final Vector3f bestFit = getPosition().clone();
        centerTop(0, 0);
        super.show();
        centerTop(0, -70);

        Tween.to(this, WidgetAccessor.POS_XY, 0.6f)
                .delay(0.1f)
                .target(bestFit.x, bestFit.y)
                .setCallback(new TweenCallback() {
            public void onEvent(int i, BaseTween<?> bt) {
                centerTop(0, 0);
            }
        })
                .start(window.getApplication().getTweenManager());

    }
}
