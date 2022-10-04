package com.poerlang.nars3dview.butterfly.game_objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Disposable;
import com.poerlang.nars3dview.butterfly.game_objects.models.Models;

public class GameObject extends ModelInstance implements Disposable {
    public final btCollisionObject body;
    public boolean moving;
    public String modelNodeName;

    public GameObject(String modelNodeName) {
        super(Models.staticModel, modelNodeName);
        this.modelNodeName = modelNodeName;
        body = new btCollisionObject();
        body.setCollisionShape(Models.constructors.get(modelNodeName).shape);
    }
    float x;
    float z;
    public void move(float x,float z){
        this.x += x;
        this.z += z;
        this.transform.trn(this.x, Ground.height+0.3f, this.z);
    }

    @Override
    public void dispose () {
        body.dispose();
    }

    public void update(float delta) {}
}