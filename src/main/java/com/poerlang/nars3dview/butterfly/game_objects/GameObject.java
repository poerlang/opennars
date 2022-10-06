package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;
import com.poerlang.nars3dview.butterfly.game_objects.models.Models;

class MyMotionState extends btMotionState {
    Matrix4 transform;

    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
public class GameObject extends ModelInstance implements Disposable {
    public final float mass;
    public btRigidBody body;
    public final MyMotionState motionState;
    public btRigidBody.btRigidBodyConstructionInfo constructionInfo;
    public boolean moving;
    public String modelNodeName;
    public static Vector3 localInertia = new Vector3();
    public btCollisionShape shape;
    public GameObject(String modelNodeName) {
        super(Models.staticModel, modelNodeName);
        this.modelNodeName = modelNodeName;
        motionState = new MyMotionState();
        motionState.transform = transform;
        shape = Models.constructors.get(modelNodeName).shape;
        mass = Models.constructors.get(modelNodeName).mass;
        if ( mass > 0f )
            shape.calculateLocalInertia(mass, localInertia);
        else
            localInertia.set(0, 0, 0);
        this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);

        body = new btRigidBody(constructionInfo);
        if( mass > 0 ){
            body.setMotionState(motionState);
            body.proceedToTransform(transform);
        }
    }

    @Override
    public void dispose () {
        shape.dispose();
        constructionInfo.dispose();
        body.dispose();
    }

    public void update(float delta) {}
    public GameObject setAngle(float a){
        transform.setFromEulerAngles(a,0,0);
        return this;
    }
    public GameObject setPos(float x, float y, float z) {
        transform.trn(x, y, z);
        return this;
    }
}