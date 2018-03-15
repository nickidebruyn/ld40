/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.blockout.game;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NideBruyn
 */
public class Game extends BasicGame {

    private static final String TILE_TYPE = "TILE_TYPE";
    private static final String REMOVE_TILE = "REMOVE_TILE";
    private static final String MOVE_TILE = "MOVE_TILE";
    private int size;
    private float tileSize = 1f;
    private float animationSpeed = 30f;
    private Node tilesNode;
    private Node backgroundNode;
    private Timer recalcTimer = new Timer(10);
    private boolean gameOver = false;

    public Game(BaseApplication baseApplication, Node rootNode, int size) {
        super(baseApplication, rootNode);
        this.size = size;
    }

    @Override
    public void init() {

        //First we load the background tiles
        backgroundNode = new Node("BACKGROUND");
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                addBackgroundTile(c, r);
            }
        }

        levelNode.attachChild(backgroundNode);
        backgroundNode.center();
        backgroundNode.move(0, 0, -1);

        //Second we load the grid of tiles
        tilesNode = new Node("TILES-NODE");
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                //Random setting of tiles
                if (FastMath.nextRandomInt(0, 1) == 0) {
                    addTile(c, r, 1);
                }
                
            }
        }

        levelNode.attachChild(tilesNode);
        tilesNode.center();
        tilesNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                recalcTimer.update(tpf);
                if (recalcTimer.finished()) {
                    calculateDeletableTiles();
                    disposeRemovableTiles();
                    recalcTimer.stop();
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
    }

    protected void addBackgroundTile(int x, int y) {
        //Add the background sprite
        Sprite background = new Sprite("background", tileSize, tileSize);
        background.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/tile-background.j3m"));
        background.move(x, y, 0);
        backgroundNode.attachChild(background);

    }

    /**
     * Add a type of tile
     * @param x
     * @param y
     * @param type 
     */
    protected void addTile(int x, int y, int type) {
        Sprite tile = new Sprite("tile", tileSize * 0.8f, tileSize * tileSize * 0.8f);
        tile.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/tile"+type+".j3m"));
        tile.setUserData(TILE_TYPE, type + "");
        tile.move(x, y, 0);

        //Attach the tile
        tilesNode.attachChild(tile);

    }

    /**
     * This method will add the new tile
     *
     * @param tile1
     */
    public void placeTile(final Sprite tile1) {

        final Vector3f tile1Pos = tile1.getLocalTranslation().clone();

        //Only move tiles that are tiles
        recalcTimer.reset();

    }

    /**
     * This method will loop over all the tiles and calculate if the current
     * tile has 2 adjasent tiles. If this is true we mark the tile as removable.
     */
    protected void calculateDeletableTiles() {
        for (Spatial spatial : tilesNode.getChildren()) {
            Sprite topTile = getTileIfSameAt(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y + tileSize, (String) spatial.getUserData(TILE_TYPE));
            Sprite bottomTile = getTileIfSameAt(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y - tileSize, (String) spatial.getUserData(TILE_TYPE));
            Sprite leftTile = getTileIfSameAt(spatial.getLocalTranslation().x - 1, spatial.getLocalTranslation().y, (String) spatial.getUserData(TILE_TYPE));
            Sprite rightTile = getTileIfSameAt(spatial.getLocalTranslation().x + 1, spatial.getLocalTranslation().y, (String) spatial.getUserData(TILE_TYPE));

            if (topTile != null && bottomTile != null) {
                spatial.setUserData(REMOVE_TILE, true);
                topTile.setUserData(REMOVE_TILE, true);
                bottomTile.setUserData(REMOVE_TILE, true);
            }
            if (leftTile != null && rightTile != null) {
                spatial.setUserData(REMOVE_TILE, true);
                leftTile.setUserData(REMOVE_TILE, true);
                rightTile.setUserData(REMOVE_TILE, true);
            }


        }
    }

    /**
     * Get a tile at a given location with a given type.
     *
     * @param x
     * @param y
     * @param type
     * @return
     */
    protected Sprite getTileIfSameAt(float x, float y, String type) {
        Sprite tile = null;
        for (Spatial spatial : tilesNode.getChildren()) {
//            log("Tile: " + spatial.getUserData(TILE_TYPE));
            if (spatial.getLocalTranslation().x == x && spatial.getLocalTranslation().y == y && type.equals(spatial.getUserData(TILE_TYPE))) {
                tile = (Sprite) spatial;
                break;
            }
        }
        return tile;
    }

    /**
     * Get a tile at a given location.
     *
     * @param x
     * @param y
     * @param type
     * @return
     */
    protected Sprite getTileAt(float x, float y) {
        Sprite tile = null;
        for (Spatial spatial : tilesNode.getChildren()) {
            if (spatial.getLocalTranslation().x == x && spatial.getLocalTranslation().y == y) {
                tile = (Sprite) spatial;
                break;
            }
        }
        return tile;
    }

    protected void disposeRemovableTiles() {
        List<Spatial> removeList = new ArrayList<Spatial>();

        //Add remove tiles to remove list
        for (Spatial spatial : tilesNode.getChildren()) {
            if (spatial.getUserData(REMOVE_TILE) != null) {
                removeList.add(spatial);
            }
        }

        //Remove tiles
        for (Spatial spatial : removeList) {
            baseApplication.getEffectManager().doEffect("destroy", spatial.getWorldTranslation().clone());
            spatial.removeFromParent();

        }


    }

    public boolean isTile(Spatial spatial) {
        return spatial.getUserData(TILE_TYPE) != null && spatial instanceof Sprite;
    }

    public void reviveGame() {


        started = true;
        paused = false;
        gameOver = false;

    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    
    
}
