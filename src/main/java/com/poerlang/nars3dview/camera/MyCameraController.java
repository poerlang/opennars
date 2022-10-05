package com.poerlang.nars3dview.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class MyCameraController extends CameraInputController {
    private static final float MIN_FOV = 0.01f;
    private static final float MAX_FOV = 179f;
    protected float velocity = 5;
    public int strafeLeftKey = Input.Keys.A;
    public int strafeRightKey = Input.Keys.D;
    public int upKey = Input.Keys.Q;
    public int downKey = Input.Keys.E;
    protected final IntIntMap keys = new IntIntMap();
    private PerspectiveCamera perspectiveCamera;

    public MyCameraController(PerspectiveCamera perspectiveCamera) {
        super(perspectiveCamera);
        this.perspectiveCamera = perspectiveCamera;
        this.pinchZoomFactor = 20f;
        zoom(0f);
    }
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }
    private final Vector3 tmp = new Vector3();

    public void update () {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (keys.containsKey(forwardKey)) {
            tmp.set(camera.direction).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(backwardKey)) {
            tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(strafeLeftKey)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(strafeRightKey)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(upKey)) {
            tmp.set(camera.up).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(downKey)) {
            tmp.set(camera.up).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (autoUpdate) camera.update(true);
    }

    @Override
    protected boolean process (float deltaX, float deltaY, int button) {
        boolean r = button == rotateButton;
        return super.process(deltaX*(r?1:9f), deltaY*(r?1:6), button);
    }
}
