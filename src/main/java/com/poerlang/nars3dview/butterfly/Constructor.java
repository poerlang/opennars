package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

public class Constructor implements Disposable {
    public final Model model;
    public final String node;
    public final btCollisionShape shape;
    public Constructor(Model model, String node, btCollisionShape shape) {
        this.model = model;
        this.node = node;
        this.shape = shape;
    }

    public GameObject construct() {
        return new GameObject(model, node, shape);
    }

    @Override
    public void dispose () {
        shape.dispose();
    }
}