package za.co.bruynhuis.escapedeep;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
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
        super("Escape the deep", 1280, 720, "escapethedeep.save", null, null, false);
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
        showScreen("play");
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return true;
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        screenManager.loadScreen("play", new PlayScreen());
    }

    @Override
    public void initModelManager(ModelManager modelManager) {
        modelManager.loadMaterial("Materials/player.j3m");
        modelManager.loadMaterial("Materials/ground.j3m");
        modelManager.loadMaterial("Materials/ocean.j3m");
        modelManager.loadMaterial("Materials/platform.j3m");
        modelManager.loadMaterial("Materials/lightning.j3m");
        
        modelManager.loadModel("Models/rain.j3o");
                
    }

    @Override
    protected void initSound(SoundManager soundManager) {
    }

    @Override
    protected void initEffect(EffectManager effectManager) {
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
    }

    @Override
    protected void initFonts(FontManager fontManager) {
    }

}
