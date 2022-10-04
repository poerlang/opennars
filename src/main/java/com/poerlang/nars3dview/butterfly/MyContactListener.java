package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.utils.Array;

public class MyContactListener extends ContactListener {
    @Override
    public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
        Array<GameObject> instances = ButterflyMainGame.instances;
        instances.get(userValue1).moving = false;
        if (userValue1 == 0)
            instances.get(userValue0).moving = false;
        else if (userValue0 == 0)
            instances.get(userValue1).moving = false;
        return true;
    }
}