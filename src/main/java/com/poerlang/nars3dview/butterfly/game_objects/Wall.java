package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public class Wall extends GameObject{
    public final static short FLAG = 1 << 6;
    public final static String NAME = "wall";

    public Wall() {
        super(NAME);
    }

    public void init() {
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        body.setContactCallbackFlag(Ground.FLAG);
        body.setContactCallbackFilter(0);
        body.setActivationState(Collision.DISABLE_DEACTIVATION);
        body.proceedToTransform(transform);
    }
    public Wall setPos(float x, float y, float z) {
        return (Wall) super.setPos(x,y,z);
    }
    public Wall setAngle(float a){
        return (Wall) super.setAngle(a);
    }
}
