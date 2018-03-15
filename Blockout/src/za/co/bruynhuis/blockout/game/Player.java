/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.game;

import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.games.basic.BasicPlayer;
import com.jme3.math.Vector3f;

/**
 *
 * @author NideBruyn
 */
public class Player extends BasicPlayer {

    public Player(BasicGame basicGame) {
        super(basicGame);
    }
    
    @Override
    protected void init() {
    }

    @Override
    public Vector3f getPosition() {
        return playerNode.getWorldTranslation();
    }

    @Override
    public void doDie() {
    }
    
}
