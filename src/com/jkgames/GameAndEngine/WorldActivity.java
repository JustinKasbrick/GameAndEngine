package com.jkgames.GameAndEngine;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.modifier.*;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.atlas.buildable.builder.ITextureBuilder;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.modifier.ease.EaseQuadOut;

public class WorldActivity extends BaseGameActivity {

    private String tag = "WorldActivity";

    private Camera mCamera;
    protected Handler mHandler;


    private BuildableBitmapTextureAtlas worldActivityTextureAtlas;
    private TextureRegion worldTextureRegion;
    private TextureRegion bobTextureRegion;
    private TextureRegion levelTextureRegion;
    //private TextureRegion starTextureRegion;

    @Override
    public Engine onLoadEngine() {
        mHandler = new Handler();
        mCamera = Helpers.createDefaultCamera();
        return Helpers.createDefaultEngine(mCamera);
    }

    @Override
    public void onLoadResources() {
        this.worldActivityTextureAtlas = new BuildableBitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.worldTextureRegion =
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.worldActivityTextureAtlas,
                        this, "world.png");
        this.bobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.worldActivityTextureAtlas,
                this, "bob.png");
        this.levelTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.worldActivityTextureAtlas,
                this, "levelCircle.png");
        try {
            worldActivityTextureAtlas.build(new BlackPawnTextureBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2));
        } catch (final ITextureBuilder.TextureAtlasSourcePackingException e) {
            Log.d(tag, "Sprites won’t fit in worldActivityTextureAtlas");
        }
        this.mEngine.getTextureManager().loadTexture(this.worldActivityTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final Scene scene = new Scene();
        /* Center the camera. */
        final int centerX = (Helpers.DEFAULT_CAMERA_WIDTH - worldTextureRegion.getWidth()) / 2;
        final int centerY = (Helpers.DEFAULT_CAMERA_HEIGHT - worldTextureRegion.getHeight()) / 2;

        /* Create the sprites and add them to the scene. */
        final Sprite background = new Sprite(centerX, centerY, worldTextureRegion);
        scene.attachChild(background);

        final Sprite[] level = {new Sprite(100, 100, levelTextureRegion){
            @Override
            public boolean onAreaTouched(
                    final TouchEvent pAreaTouchEvent,
                    final float pTouchAreaLocalX,
                    final float pTouchAreaLocalY) {
                if(pAreaTouchEvent.getAction() == TouchEvent.ACTION_UP)
                {
                    mHandler.post(mLaunchLevel);
                }
                return true;
            }
        },
                new Sprite(150, 200, levelTextureRegion){
                    @Override
                    public boolean onAreaTouched(
                            final TouchEvent pAreaTouchEvent,
                            final float pTouchAreaLocalX,
                            final float pTouchAreaLocalY) {
                        switch(pAreaTouchEvent.getAction()) {
                            case TouchEvent.ACTION_DOWN:
                                Toast.makeText(WorldActivity.this,
                                        "Sprite touch DOWN",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case TouchEvent.ACTION_UP:
                                Toast.makeText(WorldActivity.this,
                                        "Sprite touch UP",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case TouchEvent.ACTION_MOVE:
                                this.setPosition(pAreaTouchEvent.getX() -
                                        this.getWidth() / 2,
                                        pAreaTouchEvent.getY() -
                                                this.getHeight() / 2);
                                break;
                        }
                        return true;
                    }
                }};
        scene.setTouchAreaBindingEnabled(true);
        for(int i=0; i<level.length; i++)
        {
            scene.registerTouchArea(level[i]);

            scene.getLastChild().attachChild(level[i]);

        }


        final Sprite bob = new Sprite(100+5, 100-15, bobTextureRegion);
        scene.attachChild(bob);



//        final Sprite bob2 = new Sprite(20.0f,
//                Helpers.DEFAULT_CAMERA_HEIGHT - 40.0f,
//                bobTextureRegion);
//        bob2.registerEntityModifier(
////                new SequenceEntityModifier(
////                        new ParallelEntityModifier(
//                                new MoveModifier(3, 100,
//                                        150+5, 100, 200-15,
//                                        EaseQuadOut.getInstance() )
////                                new AlphaModifier(3, 0.0f, 1.0f),
////                                new ScaleModifier(3, 0.5f, 1.0f)
////                        ),
////                        new RotationModifier(3, 0, 360)
//                //)
//        );
//        scene.attachChild(bob2);

        return scene;
    }

    @Override
    public void onLoadComplete() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Runnable mLaunchLevel = new Runnable() {
        @Override
        public void run() {
            Intent myIntent = new Intent(WorldActivity.this, LevelActivity.class);
            WorldActivity.this.startActivity(myIntent);
        };
    };
}
