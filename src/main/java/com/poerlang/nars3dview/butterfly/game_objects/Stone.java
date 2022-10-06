package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Stone extends GameObject{
    public final static short FLAG = 1 << 5;
    public final static String NAME = "stone";
    public Stone() {
        super(NAME);
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        body.setContactCallbackFlag(FLAG);
        body.setContactCallbackFilter(Ground.FLAG);
    }
}
