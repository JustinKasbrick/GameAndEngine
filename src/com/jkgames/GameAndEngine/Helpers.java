package com.jkgames.GameAndEngine;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

public class Helpers {

    public static final int DEFAULT_CAMERA_WIDTH = 480;
    public static final int DEFAULT_CAMERA_HEIGHT = 320;

    public static Camera createDefaultCamera() {
        return new Camera(0, 0, DEFAULT_CAMERA_WIDTH, DEFAULT_CAMERA_HEIGHT);

    }

    public static Engine createDefaultEngine(Camera camera)
    {
        return new Engine(new EngineOptions(true,
                EngineOptions.ScreenOrientation.LANDSCAPE,
                new RatioResolutionPolicy(DEFAULT_CAMERA_WIDTH,
                        DEFAULT_CAMERA_HEIGHT), camera));
    }

}
