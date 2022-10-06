package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Food extends GameObject{
    public final static short FLAG = 1 << 3;
    public final static String NAME = "food";
    public Food() {
        super(NAME);
        body.setFriction(0.5f);
    }
    public void init() {
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        body.setContactCallbackFlag(FLAG);
        body.setContactCallbackFilter(Ground.FLAG);
        transform.setFromEulerAngles(0,90,0);
        transform.trn(MathUtils.random(6f), MathUtils.random(6f), MathUtils.random(6f));
        body.proceedToTransform(transform);
    }
}
