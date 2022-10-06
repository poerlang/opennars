package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
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

    public void update(float delta) {
//        angle(delta);
//        move(0,delta);
    }
    public Vector3 angle2vector(float a) {
        float s1 = (float) Math.cos(a);
        float s2 = (float) Math.sin(a);
        return force.set(s1,0f, -s2);
    }
    Vector3 force = new Vector3(0,1,0);
    Quaternion q = new Quaternion();
    public void forward(){
        this.transform.getRotation(q);
        q.getAxisAngle(force);
        float angleAround = q.getAngleAround(Vector3.Y);
        angleAround-=90;
        angle2vector((float) (angleAround*Math.PI/180));
        force.scl(Settings.butterflySetting.force.get());
        this.body.applyCentralImpulse(force);
    }
    public void left() {
        force.set(0,Settings.butterflySetting.RotForce.get(),0);
        this.body.applyTorqueImpulse(force);
    }

    public void right() {
        force.set(0,-Settings.butterflySetting.RotForce.get(),0);
        this.body.applyTorqueImpulse(force);
    }
}
