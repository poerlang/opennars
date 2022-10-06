package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.poerlang.nars3dview.butterfly.game_objects.models.Models;
import com.poerlang.nars3dview.setting.Settings;

public class Butterfly extends GameObject {

    public final static short FLAG = 1 << 2;
    public final static String NAME = "butterfly";
    public Butterfly() {
        super(NAME);
        moving = true;
        body.setDamping(0,0);
        body.setFriction(0.5f);
    }

    public void init() {
        body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        body.setContactCallbackFlag(FLAG);
        body.setContactCallbackFilter(Ground.FLAG);
        transform.setFromEulerAngles(0,90,0);
        transform.trn(0, 6f, 0);
        body.proceedToTransform(transform);
    }

    float angle;
    float aSpeed = 3;
    public void angle(float a){
        this.angle += a+aSpeed;
        transform.setFromEulerAngles(this.angle,90,0);
    }
    Vector3 force = new Vector3(0,1,0);
    public void move(){
        force.set(Settings.butterflySetting.forceDir[0],0,Settings.butterflySetting.forceDir[1])
                .nor()
                .scl(Settings.butterflySetting.forceNum.get());
        this.body.applyCentralImpulse(force);
    }
    public void update(float delta) {
//        angle(delta);
//        move(0,delta);
    }

    public void left() {
        force.set(0,Settings.butterflySetting.forceNum.get(),0);
        this.body.applyTorqueImpulse(force);
    }

    public void right() {
        force.set(0,-Settings.butterflySetting.forceNum.get(),0);
        this.body.applyTorqueImpulse(force);
    }
}
