package za.co.bruynhuis.escapedeep;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.bruynhuis.galago.ui.FontStyle;
import za.co.bruynhuis.escapedeep.screens.IntroScreen;
import za.co.bruynhuis.escapedeep.screens.PlayScreen;

/**
 * A game made for Ludum Dare 40.
 * Game description: In this game you are a survivor of earth's final descruction and
 * the whole world is flooding.
 * Escape the rising waters and make it off plannet earch.
 * 
 * @author nickidebruyn
 */
public class MainApplication extends Base2DApplication {

    public MainApplication() {
        super("Escape the deep", 1280, 720, "escapethedeep.save", "Interface/Fonts/GameBoy.ttf", null, true);
    }

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        frustumSize = 7f;
    }

    @Override
    protected void postInitApp() {
        showScreen("intro");
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen("intro", new IntroScreen());
        screenManager.loadScreen("play", new PlayScreen());
        
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
        
        modelManager.loadMaterial("Materials/tileset.j3m");
//        modelManager.loadMaterial("Materials/player.j3m");
//        modelManager.loadMaterial("Materials/banana.j3m");
//        modelManager.loadMaterial("Materials/ground.j3m");
//        modelManager.loadMaterial("Materials/ocean.j3m");
//        modelManager.loadMaterial("Materials/platform.j3m");
        modelManager.loadMaterial("Materials/lightning.j3m");
        
        modelManager.loadModel("Models/rain.j3o");
        modelManager.loadModel("Models/fireball.j3o");
                
    }

    @Override
    protected void initSound(SoundManager soundManager) {
        soundManager.loadMusic("background", "Sounds/SoulfulExpansion.ogg");
        soundManager.loadMusic("intro", "Sounds/intro.ogg");
        soundManager.loadMusic("rain", "Sounds/rain.ogg");
        
        soundManager.loadSoundFx("button", "Sounds/button.ogg");
        soundManager.loadSoundFx("thunder", "Sounds/thunder.ogg");
        soundManager.loadSoundFx("walk", "Sounds/walk.ogg");
        soundManager.loadSoundFx("jump", "Sounds/jump.ogg");
        soundManager.loadSoundFx("pickup", "Sounds/pickup.ogg");
        soundManager.loadSoundFx("pickuplong", "Sounds/pickuplong.ogg");
        soundManager.loadSoundFx("gameover", "Sounds/gameover.ogg");
        soundManager.loadSoundFx("shoot", "Sounds/shoot.ogg");
        soundManager.loadSoundFx("bumptop", "Sounds/bumptop.ogg");
        soundManager.loadSoundFx("thunder1", "Sounds/thunder1.ogg");
        soundManager.loadSoundFx("thunder2", "Sounds/thunder2.ogg");
        
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        effectManager.loadEffect("gold", "Models/gold-effect.j3o");
        effectManager.loadEffect("blood", "Models/blood-effect.j3o");
        effectManager.loadEffect("bump", "Models/bump-effect.j3o");
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
        textureManager.setPixelated(true);
        
    }

    @Override
    protected void initFonts(FontManager fontManager) {
        fontManager.loadFont(new FontStyle(26));
    }

}
