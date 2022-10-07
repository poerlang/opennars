package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;
import com.poerlang.nars3dview.butterfly.game_objects.*;
import com.poerlang.nars3dview.butterfly.game_objects.models.Models;

import static com.poerlang.nars3dview.butterfly.game_objects.Ground.groundSize;

public class ButterflyMainGame {
    public static Array<GameObject> instances;

    static {
        Bullet.init();
    }

    private float spawnTimer;
    final static short ALL_FLAG = -1;
    public Butterfly butterfly;

    public void init() {

        Models.initStaticModel();

        Environment3D.initCollisionWorld();

        instances = new Array<>();

        Ground ground = new Ground();
        ground.body.setUserValue(instances.size);
        instances.add(ground);
        Environment3D.addRigidBody(ground.body);
        ground.init();

        createWall().setPos(0f,Ground.height*1.5f,groundSize*0.5f + Ground.height*0.56f).init();
        createWall().setPos(0f,Ground.height*1.5f,-(groundSize*0.5f + Ground.height*0.56f)).init();
        createWall().setAngle(90f).setPos(-(groundSize*0.5f + Ground.height*0.56f),Ground.height*1.5f,0).init();
        createWall().setAngle(-90f).setPos((groundSize*0.5f + Ground.height*0.56f),Ground.height*1.5f,0).init();

        butterfly = new Butterfly();
        butterfly.body.setUserValue(instances.size);
        instances.add(butterfly);
        // Environment3D.addRigidBody(butterfly.body, Butterfly.FLAG, Butterfly.FLAG |Food.FLAG | Stone.FLAG);
        Environment3D.addRigidBody(butterfly.body);
        butterfly.init();

        Food food = new Food();
        food.body.setUserValue(instances.size);
        instances.add(food);
        Environment3D.addRigidBody(food.body);
        food.init();
    }

    private Wall createWall() {
        Wall wall = new Wall();
        wall.body.setUserValue(instances.size);
        instances.add(wall);
        Environment3D.addRigidBody(wall.body);
        return wall;
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        float delta = Math.min(1f / 60f, deltaTime);
        for (GameObject obj : instances) {
            if (obj.moving) {
                obj.update(delta);
            }
        }
        Environment3D.stepSimulation(delta, 5, 1f / 120f);
        // spawn(delta);
        for (ModelInstance instance : instances) {
            modelBatch.render(instance, environment);
        }
    }
    ModelBatch batch = new ModelBatch();
    public void renderCam2(PerspectiveCamera cam2, Environment environment) {
        batch.begin(cam2);
        for (ModelInstance instance : instances) {
            batch.render(instance, environment);
        }
        batch.end();
    }
    public void dispose() {
        for (GameObject obj : instances)
            obj.dispose();

        instances.clear();
        for (Constructor c : Models.constructors.values())
            c.dispose();

        Models.constructors.clear();
        Environment3D.dispose();
        Models.staticModel.dispose();
    }
}
