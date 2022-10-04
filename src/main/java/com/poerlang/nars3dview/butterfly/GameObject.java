package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

public class GameObject extends ModelInstance implements Disposable {

    public final btCollisionObject body;
    public boolean moving;
    public GameObject(Model model, String node, btCollisionShape shape) {
        super(model, node);
        body = new btCollisionObject();
        body.setCollisionShape(shape);
    }

    @Override
    public void dispose () {
        body.dispose();
    }
}