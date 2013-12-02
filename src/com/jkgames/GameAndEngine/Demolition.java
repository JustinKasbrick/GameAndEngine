package com.jkgames.GameAndEngine;

import android.hardware.SensorManager;
import android.opengl.GLES10;
import android.util.Log;
import android.widget.Toast;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.atlas.buildable.builder.ITextureBuilder;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class Demolition extends BaseGameActivity {

    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 320;

    // ===========================================================
    // Fields
    // ===========================================================

    private Camera mCamera;
    private MainCharacter mainCharacter;
    private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion bobTextureRegion;

    private TextureRegion mOnScreenControlBaseTextureRegion;
    private TextureRegion mOnScreenControlKnobTextureRegion;
    private TextureRegion mOnScreenJumpButtonTextureRegion;
    private TextureRegion mOnScreenAttackButtonTextureRegion;

    private static final FixtureDef FIXTURE_DEF =
            PhysicsFactory.createFixtureDef(1, 0f, 0.5f);
    private PhysicsWorld mPhysicsWorld;

    private boolean mPlaceOnScreenControlsAtDifferentVerticalLocations = false;
    private float PtoM =
            PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
    @Override
    public Engine onLoadEngine() {
        this.mCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, -1000, 1000, -1000, 1000);
        final EngineOptions engineOptions = new EngineOptions(true, EngineOptions.ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
        //engineOptions.getTouchOptions().setNeedsMultiTouch(true);

        if(MultiTouch.isSupported(this)) {
            if(MultiTouch.isSupportedDistinct(this)) {
                Toast.makeText(this, "MultiTouch detected --> Both controls will work properly!", Toast.LENGTH_SHORT).show();
            } else {
                this.mPlaceOnScreenControlsAtDifferentVerticalLocations = true;
                Toast.makeText(this, "MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
        }

        return new Engine(engineOptions);
    }

    @Override
    public void onLoadResources() {
        this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.bobTextureRegion =
                BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
                        this, "bob.png");
        this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
                this, "analog.png");
        this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
                this, "analog.png");
        this.mOnScreenAttackButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
                this, "b_button.png");
        this.mOnScreenJumpButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
                this, "a_button.png");
        try {
            mBitmapTextureAtlas.build(new BlackPawnTextureBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2));
        } catch (final ITextureBuilder.TextureAtlasSourcePackingException e) {
            Log.d("Demo", "Sprites won’t fit in worldActivityTextureAtlas");
        }
        this.mEngine.getTextureManager().loadTexture(this.mBitmapTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        final float centerX = (CAMERA_WIDTH - this.bobTextureRegion.getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.bobTextureRegion.getHeight()) / 2;

        this.mPhysicsWorld = new MaxStepPhysicsWorld(60, new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        mainCharacter = new MainCharacter();
        final Sprite face = new Sprite(-15.5f, -150.5f, this.bobTextureRegion);

        final Body body = PhysicsFactory.createBoxBody(mPhysicsWorld,
                face, BodyDef.BodyType.DynamicBody,
                FIXTURE_DEF);
        body.setFixedRotation(true);

        scene.attachChild(face);
        mPhysicsWorld.registerPhysicsConnector(
                new PhysicsConnector(face, body, true, true));
        mCamera.setChaseEntity(face);
        /* Velocity control (left). */
        final float x1 = 50;
        final float y1 = CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight()-50;
        final AnalogOnScreenControl velocityOnScreenControl = new AnalogOnScreenControl(x1, y1, this.mCamera,
                this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f,
                new AnalogOnScreenControl.IAnalogOnScreenControlListener() {
            @Override
            public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
//                mVelocity = body.getLinearVelocity();
//
//                body.setLinearVelocity(mVelocity.add(pValueX, 0));

                body.applyForce(new Vector2(pValueX*30, 0), new Vector2(body.getWorldCenter()));
//                mVelocity = body.getLinearVelocity();
//                float maxSpeed = 2;
//                if(mVelocity.x > maxSpeed)
//                    body.setLinearVelocity(new Vector2(maxSpeed, mVelocity.y));
//                if(mVelocity.x < -maxSpeed)
//                    body.setLinearVelocity(new Vector2(-maxSpeed, mVelocity.y));
//                Vector2Pool.recycle(mVelocity);
            }

            @Override
            public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				/* Nothing. */
            }
        });
        velocityOnScreenControl.getControlBase().setBlendFunction(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
        velocityOnScreenControl.getControlBase().setAlpha(0.5f);

        final Sprite jumpButton = new Sprite(mCamera.getCenterX()+mOnScreenJumpButtonTextureRegion.getWidth(), 100, mOnScreenJumpButtonTextureRegion){
        @Override
        public boolean onAreaTouched(
                final TouchEvent pAreaTouchEvent,
                final float pTouchAreaLocalX,
                final float pTouchAreaLocalY) {
            if(pAreaTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {

                body.applyLinearImpulse(mainCharacter.getJumpVelocity(), body.getWorldCenter());
            }
            return true;
    }};

        scene.setChildScene(velocityOnScreenControl);

//        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2,
//                CAMERA_WIDTH, 2);
//        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
//        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
//        final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2,
//                CAMERA_HEIGHT);
//        final FixtureDef wallFixtureDef =
//                PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
//        PhysicsFactory.createBoxBody(mPhysicsWorld, ground,
//                BodyDef.BodyType.StaticBody, wallFixtureDef);
//        PhysicsFactory.createBoxBody(mPhysicsWorld, roof,
//                BodyDef.BodyType.StaticBody, wallFixtureDef);
//        PhysicsFactory.createBoxBody(mPhysicsWorld, left,
//                BodyDef.BodyType.StaticBody, wallFixtureDef);
//        PhysicsFactory.createBoxBody(mPhysicsWorld, right,
//                BodyDef.BodyType.StaticBody, wallFixtureDef);
//
//        scene.attachChild(ground);
//        scene.attachChild(roof);
//        scene.attachChild(left);
//        scene.attachChild(right);

        Vector2[] v = new Vector2[] {new Vector2(-173, -224.5f), new Vector2(-123, -197.5f), new Vector2(-93, -166.5f),
                new Vector2(-52, -122.5f), new Vector2(-5, -98.5f), new Vector2(21, -82.5f), new Vector2(171, -81.5f),
                new Vector2(173, 223.5f)};

        final FixtureDef wallFixtureDef =
                PhysicsFactory.createFixtureDef(0, 0f, 0.5f);

        createRectangle( 240f, -135f, 253f, 27f, -20f, wallFixtureDef, BodyDef.BodyType.StaticBody, scene );
        createRectangle( -170f, -276f, 25f, 25f, 45f, wallFixtureDef, BodyDef.BodyType.DynamicBody, scene );
        createRectangle( -205.5380592228744f, -161.9619407771256f, 202.9238815542512f, 24.07611844574882f, 45f, wallFixtureDef, BodyDef.BodyType.StaticBody, scene );
        createRectangle( -6.5f, -92.5f, 253f, 27f, 0f, wallFixtureDef, BodyDef.BodyType.StaticBody, scene );



        scene.registerUpdateHandler(mPhysicsWorld);


        return scene;
    }

    @Override
    public void onLoadComplete() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void createRectangle(float x, float y, float width, float height, float angle, FixtureDef wallFixtureDef, BodyDef.BodyType type, Scene scene)
    {
        final Shape rectangle = new Rectangle(x, y, width, height);

        if(angle != 0)
            rectangle.setRotation(angle);

        Body b = PhysicsFactory.createBoxBody(mPhysicsWorld, rectangle,
                type, wallFixtureDef);
        scene.attachChild(rectangle);
        mPhysicsWorld.registerPhysicsConnector(
                new PhysicsConnector(rectangle, b, true, true));
    }
}