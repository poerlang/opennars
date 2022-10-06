package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

// 环境虽然负责碰撞，但不会直接为 nar 提供信息，nar 必须主动通过自己的器官来观察和获取，才能得到环境信息。
public class Environment3D {
    static btCollisionConfiguration collisionConfig;
    static btDispatcher dispatcher;
    static MyContactListener contactListener;
    static btBroadphaseInterface broadphase;
    private static btSequentialImpulseConstraintSolver constraintSolver;
    private static btDiscreteDynamicsWorld dynamicsWorld;

    public static void initCollisionWorld() {
        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        contactListener = new MyContactListener();
    }

    public static void addRigidBody(btRigidBody body, int collisionFilterGroup, int collisionFilterMask) {
        dynamicsWorld.addRigidBody(body, collisionFilterGroup, collisionFilterMask);
    }

    public static void dispose() {
        dynamicsWorld.dispose();
        constraintSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
        contactListener.dispose();
    }

    public static void addRigidBody(btRigidBody body) {
        dynamicsWorld.addRigidBody(body);
    }

    public static void stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
        dynamicsWorld.stepSimulation(timeStep,maxSubSteps,fixedTimeStep);
    }
}
