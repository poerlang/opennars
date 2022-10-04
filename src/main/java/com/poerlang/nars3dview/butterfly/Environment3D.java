package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.physics.bullet.collision.*;

// 环境虽然负责碰撞，但不会直接为 nar 提供信息，nar 必须主动通过自己的器官来观察和获取，才能得到环境信息。
public class Environment3D {
    static btCollisionConfiguration collisionConfig;
    static btDispatcher dispatcher;
    static MyContactListener contactListener;
    static btBroadphaseInterface broadphase;
    static btCollisionWorld collisionWorld;
    public static void initCollisionWorld() {
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        contactListener = new MyContactListener();
    }

    public static void addCollisionObject(btCollisionObject body, short groundFlag, short allFlag) {
        collisionWorld.addCollisionObject(body, groundFlag, allFlag);
    }

    public static void dispose() {
        collisionWorld.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
        contactListener.dispose();
    }

    public static void checkCollision() {
        collisionWorld.performDiscreteCollisionDetection();
    }
}
