package za.co.bruynhuis.blockout;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.listener.GoogleAPIErrorListener;
import com.bruynhuis.galago.ui.listener.SavedGameListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import za.co.bruynhuis.blockout.screens.GameoverScreen;
import za.co.bruynhuis.blockout.screens.MenuScreen;
import za.co.bruynhuis.blockout.screens.PlayScreen;

/**
 * Blockout is a block matching game where the player need to match three or more of the same color blocks.
 * 
 * Every time he needs to place a new block and try not to fill up the whole grid.
 * 
 * @author nickidebruyn
 */
public class MainApplication extends Base2DApplication implements GoogleAPIErrorListener, SavedGameListener {
    
    public static final String GAME_NAME = "BlockoutSave";
    public static final String GAMES_PLAYED = "gamesplayed";
    public static final String BEST_SCORE = "BEST_SCORE";  
    public static final String LOOT = "LOOT";  
    
    public static final String BEST_SCORE_LEADER_BOARD_ID = "";
    
    public static final String ACHIEVEMENT_REACHED_20 = "";
    public static final String ACHIEVEMENT_REACHED_50 = "";
    public static final String ACHIEVEMENT_REACHED_100 = "";
    public static final String ACHIEVEMENT_REACHED_200 = "";
    
    public static final String ACHIEVEMENT_PLAYED_20 = "";
    public static final String ACHIEVEMENT_PLAYED_100 = "";
    public static final String ACHIEVEMENT_PLAYED_200 = "";
    
    private boolean showHelp = true;

    public MainApplication() {
        super("Blockout", 480, 800, "blockout.save", "Interface/Fonts/OpenSansSemibold.fnt", "Interface/splash.png", false);
    }

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        splashInfoMessage = "";
        frustumSize = 6.25f;
////        BACKGROUND_COLOR = Game.BACKGROUND_COLOR;        
//        viewPort.setBackgroundColor(Game.BACKGROUND_COLOR);
        
    }
    

    @Override
    protected void postInitApp() {        
        showScreen("menu");
        setGoogleAPIErrorListener(this);
        setSavedGameListener(this);
        
        doGoogleSignIn();
//        
//        getGameSaves().getGameData().setLevel(5000);
//        getGameSaves().save();
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return false;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen("menu", new MenuScreen());
        screenManager.loadScreen("play", new PlayScreen());
        screenManager.loadScreen("gameover", new GameoverScreen());
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
    }

    @Override
    protected void initSound(SoundManager soundManager) {
        soundManager.loadSoundFx("button", "Sounds/button.ogg");
        soundManager.loadSoundFx("destroy", "Sounds/pop.ogg");
        soundManager.loadSoundFx("pickup", "Sounds/pickup.ogg");
        soundManager.loadSoundFx("hit", "Sounds/pop.ogg");
        soundManager.loadSoundFx("levelup", "Sounds/levelup.ogg");
        soundManager.loadSoundFx("loot", "Sounds/loot.ogg");
        soundManager.loadSoundFx("gameover", "Sounds/gameover.ogg");
        soundManager.loadSoundFx("reverse", "Sounds/reverse.ogg");
        
        soundManager.loadMusic("rumble", "Sounds/rumble.ogg");
        soundManager.setMusicVolume("rumble", 0.3f);
        
        soundManager.loadMusic("laser", "Sounds/laser.ogg");
        soundManager.setMusicVolume("laser", 0.3f);
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        effectManager.loadEffect("destroy", "Models/effects/destroy.j3o");
        effectManager.loadEffect("pickup", "Models/effects/pickup.j3o");
        effectManager.loadEffect("bump", "Models/effects/bump.j3o");
        effectManager.loadEffect("loot", "Models/effects/loot.j3o");
//        effectManager.loadEffect("reward", "Models/effects/reward.j3o");
        
        effectManager.loadEffect("pickup-shake", "Models/effects/pickup-shake.j3o");
        effectManager.loadEffect("pickup-laser", "Models/effects/pickup-laser.j3o");
        effectManager.loadEffect("pickup-reverse", "Models/effects/pickup-reverse.j3o");
        effectManager.loadEffect("pickup-double", "Models/effects/pickup-double.j3o");
    }

    @Override
    protected void initTextures(TextureManager textureManager) { 
    }
    
    public void onGoogleAPIError(String errorMessage) {
    }

    public void onGoogleAPIConnected(String message) {
        //The connection to google was successful
        getGameSaves().getGameData().setOnlinePlayer(true);
        getGameSaves().save();

        doOpenSavedGameAction(GAME_NAME);

    }

    public void onGoogleAPIDisconnected(String message) {
        getGameSaves().getGameData().setOnlinePlayer(false);
        getGameSaves().save();

        //TODO: We can get any screen here and set a UI component to show that the player is offline

    }

    public void onSavedGameError(String errorMessage) {
//        showScreen("menu");
    }

    public void onSavedGameOpened(String name, String data) {
        if (data != null) {

            StringReader sr = new StringReader(data);
            Properties readProp = new Properties();
            try {
                readProp.load(sr);
                getGameSaves().getGameData().getProperties().putAll(readProp);
                String level = readProp.getProperty(MainApplication.GAMES_PLAYED);
                if (level != null) {
                    try {
                        int highestLevel = Integer.parseInt(level);
                        getGameSaves().getGameData().setGamesPlayed(highestLevel);

                    } catch (Exception e) {
                    }

                }
                
                String score = readProp.getProperty(MainApplication.BEST_SCORE);
                if (score != null) {
                    try {
                        int scoreInt = Integer.parseInt(score);
                        getGameSaves().getGameData().setScore(scoreInt);

                    } catch (Exception e) {
                    }

                }
                
                String loot = readProp.getProperty(MainApplication.LOOT);
                if (loot != null) {
                    try {
                        int lootInt = Integer.parseInt(loot);
                        getGameSaves().getGameData().setLevel(lootInt);

                    } catch (Exception e) {
                    }

                }
                
                getGameSaves().save();
                updateAllScreens();


            } catch (IOException ex) {
                Logger.getLogger(MainApplication.class.getName()).log(Level.SEVERE, null, ex);
                doAlert("Error on open: " + ex);
            }

        }

    }

    public void onSavedGameSaved() {
    }

    public void saveGameDataToCloud() {
        //Save level progress to the cloud
        Properties propToSave = new Properties();
        propToSave.putAll(getGameSaves().getGameData().getProperties());
        propToSave.put(MainApplication.GAMES_PLAYED, getGameSaves().getGameData().getGamesPlayed() + "");
        propToSave.put(MainApplication.BEST_SCORE, getGameSaves().getGameData().getScore()+ "");
        propToSave.put(MainApplication.LOOT, getGameSaves().getGameData().getLevel()+ "");
        StringWriter sw = new StringWriter();
        try {
            propToSave.store(sw, "Game Saved Data");

        } catch (IOException ex) {
            Logger.getLogger(PlayScreen.class.getName()).log(Level.SEVERE, null, ex);
        }

        doCommitSavedGameAction(GAME_NAME, "The saved game for blockout", sw.toString());
    }
    

    public void showLeaderBoard() {
        doShowHighscores(BEST_SCORE_LEADER_BOARD_ID, 0);
    }
 
    public GameoverScreen getGameoverScreen() {
        return (GameoverScreen) screenManager.getScreen("gameover");
    }
    
    public PlayScreen getPlayScreen() {
        return (PlayScreen) screenManager.getScreen("play");
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public void setShowHelp(boolean showHelp) {
        this.showHelp = showHelp;
    }
    
    public void updateAllScreens() {
        MenuScreen menuScreen = (MenuScreen) getScreenManager().getScreen("menu");
        menuScreen.updateUI();
        
        PlayScreen playScreen = (PlayScreen) getScreenManager().getScreen("play");
        playScreen.updateUI();
        
        GameoverScreen gameoverScreen = (GameoverScreen) getScreenManager().getScreen("gameover");
        gameoverScreen.updateUI();
    }

    @Override
    protected void initFonts(FontManager fontManager) {
        fontManager.loadFont(new FontStyle(12));
        fontManager.loadFont(new FontStyle(16));
        fontManager.loadFont(new FontStyle(34));
        fontManager.loadFont(new FontStyle(24));
        fontManager.loadFont(new FontStyle(20));
        fontManager.loadFont(new FontStyle(40));
        fontManager.loadFont(new FontStyle(48));
        fontManager.loadFont(new FontStyle(64));
    }
}
