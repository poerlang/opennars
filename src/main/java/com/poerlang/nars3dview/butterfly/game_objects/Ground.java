package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Ground extends GameObject{

    public final static float groundSize = 26f;
    public final static float groundSizeHalf = groundSize * 0.5f;
    public final static short FLAG = 1 << 4;
    public final static String NAME = "ground";
    public static float height = 1f;

    public Ground() {
        super(NAME);
        body.setDamping(0,0);
    }

    public void init() {
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        body.setContactCallbackFlag(FLAG);
        body.setContactCallbackFilter(0);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
    }
}
