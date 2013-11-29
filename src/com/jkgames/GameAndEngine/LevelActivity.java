package com.jkgames.GameAndEngine;

import android.os.Handler;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.layer.tiled.tmx.*;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;


public class LevelActivity extends BaseGameActivity {

    private String tag = "LevelActivity";

    private Handler mHandler;
    protected Camera mCamera;
    protected Scene mMainScene;
    private TMXTiledMap mWAVTMXMap;
    private TMXLayer tmxLayer;
    private TMXTile tmxTile;
    private int[] coffins = new int[50];
    private int coffinPtr = 0;
    private int mCoffinGID = -1;
    private int mOpenCoffinGID = 1;

    @Override
    public Engine onLoadEngine() {
        mHandler = new Handler();
        mCamera = Helpers.createDefaultCamera();
        return Helpers.createDefaultEngine(mCamera);
    }

    @Override
    public void onLoadResources() {
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        try {
            final TMXLoader tmxLoader = new TMXLoader(
                    this, this.mEngine.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA,
                    new TMXLoader.ITMXTilePropertiesListener() {
                        @Override
                        public void onTMXTileWithPropertiesCreated(
                                final TMXTiledMap pTMXTiledMap,
                                final TMXLayer pTMXLayer,
                                final TMXTile pTMXTile,
                                final TMXProperties<TMXTileProperty>
                                        pTMXTileProperties) {
//                            if(pTMXTileProperties.
//                                    containsTMXProperty("coffin", "true")) {
//                                coffins[coffinPtr++] =
//                                        pTMXTile.getTileRow() * 15 +
//                                                pTMXTile.getTileColumn();
//                                if (mCoffinGID<0){
//                                    mCoffinGID =
//                                            pTMXTile.getGlobalTileID();
//                                }
//                            }
                        }
                    });
            this.mWAVTMXMap = tmxLoader.loadFromAsset(this,
                    "levelMap.tmx");
        } catch (final TMXLoadException tmxle) {
            Debug.e(tmxle);
        }

        tmxLayer = this.mWAVTMXMap.getTMXLayers().get(0);
        scene.attachChild(tmxLayer);

        return scene;
    }

    @Override
    public void onLoadComplete() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
