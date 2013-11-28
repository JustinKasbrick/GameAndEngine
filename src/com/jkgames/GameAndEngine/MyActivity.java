package com.jkgames.GameAndEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.bitmap.BitmapTexture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;


public class MyActivity extends BaseGameActivity {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 320;

    // ===========================================================
    // Fields
    // ===========================================================
    private Camera mCamera;
    private BitmapTextureAtlas mTexture;
    private TextureRegion mSplashTextureRegion;
    private Handler mHandler;
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public Engine onLoadEngine() {
        mHandler = new Handler();
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH,
                CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true,
                EngineOptions.ScreenOrientation.LANDSCAPE,
                new FillResolutionPolicy(),
                this.mCamera));
    }

    @Override
    public void onLoadResources() {
        this.mTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mSplashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTexture, this,
                "splashScreen.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mTexture);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final Scene scene = new Scene();
        /* Center the splash on the camera. */
        final int centerX =
                (CAMERA_WIDTH - this.mSplashTextureRegion.getWidth()) / 2;
        final int centerY =
                (CAMERA_HEIGHT -
                        this.mSplashTextureRegion.getHeight()) / 2;
        /* Create the sprite and add it to the scene. */
        final Sprite splash = new Sprite(centerX,
                centerY, this.mSplashTextureRegion);

        scene.setBackground(new SpriteBackground(splash));

        return scene;
    }

    @Override
    public void onLoadComplete() {
        mHandler.postDelayed(mLaunchTask, 3000);
    }

    private Runnable mLaunchTask = new Runnable() {
        @Override
        public void run() {
            Intent myIntent = new Intent(MyActivity.this, MainMenuActivity.class);
            MyActivity.this.startActivity(myIntent);
        };
    };
}
