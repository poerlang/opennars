package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Array;
import com.poerlang.nars3dview.butterfly.game_objects.*;
import com.poerlang.nars3dview.butterfly.game_objects.models.Models;

public class ButterflyMainGame {
    public static Array<GameObject> instances;

    static {
        Bullet.init();
    }

    private float spawnTimer;
    final static short ALL_FLAG = -1;

    public void init() {

        Models.initStaticModel();

        Environment3D.initCollisionWorld();

        instances = new Array<>();

        GameObject ground = new Ground();
        instances.add(ground);

        Butterfly butterfly = new Butterfly();
        butterfly.angle(3);
        instances.add(butterfly);
        butterfly.body.setCollisionFlags(butterfly.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        Environment3D.addCollisionObject(butterfly.body, Butterfly.FLAG, (short) (Food.FLAG | Stone.FLAG));
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        float delta = Math.min(1f / 60f, deltaTime);
        for (GameObject obj : instances) {
            if (obj.moving) {
                obj.update(delta);
                obj.body.setWorldTransform(obj.transform);
            }
        }
        Environment3D.checkCollision();
        // spawn(delta);
        for (ModelInstance instance : instances) {
            modelBatch.render(instance, environment);
        }
    }

    public void spawn(float delta) {
        if ((spawnTimer -= delta) >= 0) {
            return;
        }
        spawnTimer = 1.5f;
        GameObject obj = Models.constructors.values[1 + MathUtils.random(Models.constructors.size - 2)].construct();
        obj.moving = true;
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.setWorldTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);
        Environment3D.addCollisionObject(obj.body, Food.FLAG, Ground.FLAG);
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
