package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class ButterflyMainGame {
    Model model;
    ModelInstance ground;
    ModelInstance ball;
    public static Array<GameObject> instances;
    static {
        Bullet.init();
    }

    private btSphereShape ballShape;
    private btBoxShape groundShape;
    private btCollisionObject groundObject;
    private btCollisionObject ballObject;
    private ArrayMap<String, Constructor> constructors;
    private float spawnTimer;
    private MyContactListener contactListener;
    btBroadphaseInterface broadphase;
    btCollisionWorld collisionWorld;
    final static short GROUND_FLAG = 1 << 8;
    final static short OBJECT_FLAG = 1 << 9;
    final static short ALL_FLAG = -1;
    public void init(){
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(5f, 1f, 5f);
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                .cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
                .capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.MAGENTA))).cylinder(1f, 2f, 1f, 10);
        model = mb.end();

        constructors = new ArrayMap<String, Constructor>(String.class, Constructor.class);
        constructors.put("ground", new Constructor(model, "ground", new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f))));
        constructors.put("sphere", new Constructor(model, "sphere", new btSphereShape(0.5f)));
        constructors.put("box", new Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        constructors.put("cone", new Constructor(model, "cone", new btConeShape(0.5f, 2f)));
        constructors.put("capsule", new Constructor(model, "capsule", new btCapsuleShape(.5f, 1f)));
        constructors.put("cylinder", new Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f))));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        contactListener = new MyContactListener();

        instances = new Array<GameObject>();
        GameObject object = constructors.get("ground").construct();
        instances.add(object);
        collisionWorld.addCollisionObject(object.body, GROUND_FLAG, ALL_FLAG);

    }

    boolean collision;
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    public void render(ModelBatch modelBatch, Environment environment) {
        final float delta = Math.min(1f/60f, Gdx.graphics.getDeltaTime());
        for (GameObject obj : instances) {
            if (obj.moving) {
                obj.transform.trn(0f, -delta, 0f);
                obj.body.setWorldTransform(obj.transform);
//                checkCollision(obj.body, instances.get(0).body);
            }
        }
        collisionWorld.performDiscreteCollisionDetection();
        if ((spawnTimer -= delta) < 0) {
            spawn();
            spawnTimer = 1.5f;
        }
        for (ModelInstance instance : instances) {
            modelBatch.render(instance, environment);
        }
    }
    public void spawn() {
        GameObject obj = constructors.values[1 + MathUtils.random(constructors.size - 2)].construct();
        obj.moving = true;
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.setWorldTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);
        collisionWorld.addCollisionObject(obj.body, OBJECT_FLAG, GROUND_FLAG);
    }

    boolean checkCollision (btCollisionObject obj0, btCollisionObject obj1) {
        CollisionObjectWrapper co0 = new CollisionObjectWrapper(obj0);
        CollisionObjectWrapper co1 = new CollisionObjectWrapper(obj1);

        btCollisionAlgorithm algorithm = dispatcher.findAlgorithm(co0.wrapper, co1.wrapper,null, ebtDispatcherQueryType.BT_CONTACT_POINT_ALGORITHMS);

        btDispatcherInfo info = new btDispatcherInfo();
        btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);

        algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

        boolean r = result.getPersistentManifold().getNumContacts() > 0;

        dispatcher.freeCollisionAlgorithm(algorithm.getCPointer());
        result.dispose();
        info.dispose();
        co1.dispose();
        co0.dispose();

        return r;
    }
    public void dispose () {
        for (GameObject obj : instances)
            obj.dispose();
        instances.clear();

        for (Constructor ctor : constructors.values())
            ctor.dispose();
        constructors.clear();

        collisionWorld.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();

        contactListener.dispose();

        model.dispose();
    }
}
