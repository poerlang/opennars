package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;
import com.poerlang.nars3dview.butterfly.game_objects.GameObject;

public class Constructor implements Disposable {
    public final Model model;
    public final String nodeName;
    public final btCollisionShape shape;
    public Constructor(Model model, String nodeName, btCollisionShape shape) {
        this.model = model;
        this.nodeName = nodeName;
        this.shape = shape;
    }

    public GameObject construct() {
        return new GameObject(nodeName);
    }

    @Override
    public void dispose () {
        shape.dispose();
    }
}