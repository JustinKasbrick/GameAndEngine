package com.jkgames.GameAndEngine;


import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import javax.microedition.khronos.opengles.GL10;

public class MainMenuActivity extends BaseGameActivity implements MenuScene.IOnMenuItemClickListener {

    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 320;
    protected static final int MENU_ABOUT = 0;
    protected static final int MENU_NEW_GAME = 1;
    protected static final int MENU_CONTINUE = 2;
    protected static final int MENU_PLAY = 100;
    protected static final int MENU_SCORES = MENU_PLAY + 1;
    protected static final int MENU_OPTIONS = MENU_SCORES + 1;
    protected static final int MENU_HELP = MENU_OPTIONS + 1;

    protected Camera mCamera;
    protected Scene mMainScene;
    private BitmapTextureAtlas mMenuBackTexture;
    private TextureRegion mMenuBackTextureRegion;
    protected MenuScene mStaticMenuScene, mPopUpMenuScene;
    private BitmapTextureAtlas mPopUpTexture;
    private BitmapTextureAtlas mFontTexture;
    private BitmapTextureAtlas mMenuItemTexture;
    private Font mFont;
    protected TextureRegion mPopUpAboutTextureRegion;
    protected TextureRegion mContinueTextureRegion;
    protected TextureRegion mNewGameTextureRegion;
    protected TextureRegion mMenuPlayTextureRegion;
    protected TextureRegion mMenuScoresTextureRegion;
    protected TextureRegion mMenuOptionsTextureRegion;
    protected TextureRegion mMenuHelpTextureRegion;
    private boolean popupDisplayed;
    protected Handler mHandler;

    @Override
    public Engine onLoadEngine() {
        mHandler = new Handler();
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH,
                CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true,
                EngineOptions.ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(CAMERA_WIDTH,
                        CAMERA_HEIGHT), this.mCamera));
    }

    @Override
    public void onLoadResources() {
        /* Load Font/Textures. */
        this.mFontTexture = new BitmapTextureAtlas(256, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        FontFactory.setAssetBasePath("font/");
        this.mFont = FontFactory.createFromAsset(this.mFontTexture,
                this, "Flubber.ttf", 32, true, Color.RED);
        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.mEngine.getFontManager().loadFont(this.mFont);

        this.mMenuBackTexture = new BitmapTextureAtlas(1024, 1024,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mMenuBackTextureRegion =
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuBackTexture,
                        this, "glBackground.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mMenuBackTexture);

        this.mMenuItemTexture = new BitmapTextureAtlas(512, 512,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mNewGameTextureRegion =
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuItemTexture,
                        this, "NewGame_button.png", 0, 0);
        this.mContinueTextureRegion =
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuItemTexture,
                        this, "Continue_button.png", 0, 50);

        this.mEngine.getTextureManager().loadTexture(this.mMenuItemTexture);
        popupDisplayed = false;
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        this.createStaticMenuScene();
        //this.createPopUpMenuScene();
        /* Center the background on the camera. */
        final int centerX = ( CAMERA_WIDTH - this.mMenuBackTextureRegion.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - this.mMenuBackTextureRegion.getHeight()) / 2;
        this.mMainScene = new Scene();
        /* Add the background and static menu */
        final Sprite menuBack = new Sprite(centerX, centerY, this.mMenuBackTextureRegion);
        mMainScene.setBackground(new SpriteBackground(menuBack));
        mMainScene.setChildScene(mStaticMenuScene);
        return this.mMainScene;
    }

    @Override
    public void onLoadComplete() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

//    @Override
//    public boolean onKeyDown(final int pKeyCode,
//                             final KeyEvent pEvent) {
//        if(pKeyCode == KeyEvent.KEYCODE_MENU &&
//                pEvent.getAction() == KeyEvent.ACTION_DOWN) {
//            if(popupDisplayed) {
//                /* Remove the menu and reset it. */
//                this.mPopUpMenuScene.back();
//                mMainScene.setChildScene(mStaticMenuScene);
//                popupDisplayed = false;
//            } else {
//                /* Attach the menu. */
//                this.mMainScene.setChildScene(
//                        this.mPopUpMenuScene, false, true, true);
//                popupDisplayed = true;
//            }
//            return true;
//        } else {
//            return super.onKeyDown(pKeyCode, pEvent);
//        }
//    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
            switch(pMenuItem.getID()) {
                case MENU_NEW_GAME:
                    mHandler.post(mLaunchNewGameTask);
                    return true;
                case MENU_CONTINUE:
                    Toast.makeText(MainMenuActivity.this,
                            "Continue selected", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return false;
            }
    }

    // ===========================================================
// Methods
// ===========================================================
//    protected void createStaticMenuScene() {
//        this.mStaticMenuScene = new MenuScene(this.mCamera);
//        final IMenuItem playMenuItem = new ColorMenuItemDecorator(
//                new TextMenuItem(MENU_PLAY, mFont, "Play Game"),
//                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
//        playMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
//                GL10.GL_ONE_MINUS_SRC_ALPHA);
//        this.mStaticMenuScene.addMenuItem(playMenuItem);
//        final IMenuItem scoresMenuItem =
//                new ColorMenuItemDecorator(
//                        new TextMenuItem(MENU_SCORES, mFont, "Scores"),
//                        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
//        scoresMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
//                GL10.GL_ONE_MINUS_SRC_ALPHA);
//        this.mStaticMenuScene.addMenuItem(scoresMenuItem);
//        final IMenuItem optionsMenuItem =
//                new ColorMenuItemDecorator(
//                        new TextMenuItem(MENU_OPTIONS, mFont, "Options"),
//                        0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
//        optionsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
//                GL10.GL_ONE_MINUS_SRC_ALPHA);
//        this.mStaticMenuScene.addMenuItem(optionsMenuItem);
//        final IMenuItem helpMenuItem = new ColorMenuItemDecorator(
//                new TextMenuItem(MENU_HELP, mFont, "Help"),
//                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f);
//        helpMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
//                GL10.GL_ONE_MINUS_SRC_ALPHA);
//        this.mStaticMenuScene.addMenuItem(helpMenuItem);
//        this.mStaticMenuScene.buildAnimations();
//        this.mStaticMenuScene.setBackgroundEnabled(false);
//        this.mStaticMenuScene.setOnMenuItemClickListener(this);
//    }

    protected void createStaticMenuScene() {
        this.mStaticMenuScene = new MenuScene(this.mCamera);

        final SpriteMenuItem newGameMenuItem = new SpriteMenuItem(MENU_NEW_GAME, this.mNewGameTextureRegion);
        newGameMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mStaticMenuScene.addMenuItem(newGameMenuItem);

        final SpriteMenuItem continueMenuItem = new SpriteMenuItem(MENU_CONTINUE, this.mContinueTextureRegion);
        continueMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        this.mStaticMenuScene.addMenuItem(continueMenuItem);

        this.mStaticMenuScene.buildAnimations();
        this.mStaticMenuScene.setBackgroundEnabled(false);
        this.mStaticMenuScene.setOnMenuItemClickListener(this);
    }

    private Runnable mLaunchNewGameTask = new Runnable() {
        @Override
        public void run() {
            Intent myIntent = new Intent(MainMenuActivity.this, NewGameActivity.class);
            MainMenuActivity.this.startActivity(myIntent);
        };
    };
}
