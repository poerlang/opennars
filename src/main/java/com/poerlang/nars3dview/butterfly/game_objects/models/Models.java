package com.poerlang.nars3dview.butterfly.game_objects.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.ArrayMap;
import com.poerlang.nars3dview.butterfly.Constructor;
import com.poerlang.nars3dview.butterfly.game_objects.Ground;

public class Models {
    public static Model staticModel;
    public static ArrayMap<String, Constructor> constructors;
    public static void initStaticModel(){
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        MeshPartBuilder groundPart = mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.FOREST)));
        BoxShapeBuilder.build(groundPart,25f, Ground.height, 20f);

        mb.node().id = "food";
        MeshPartBuilder spherePart = mb.part("food", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)));
        SphereShapeBuilder.build(spherePart,1f, 1f, 1f, 10, 10);

        mb.node().id = "stone";
        MeshPartBuilder boxPart = mb.part("stone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)));
        BoxShapeBuilder.build(boxPart,1f, 1f, 1f);

        mb.node().id = "butterfly";
        MeshPartBuilder cone = mb.part("butterfly", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createEmissive(Color.GOLD)));
        ConeShapeBuilder.build(cone,1f, 2f, 1f, 6);
        staticModel = mb.end();

        constructors = new ArrayMap<>(String.class, Constructor.class);
        constructors.put("ground", new Constructor(staticModel, "ground", new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f))));
        constructors.put("food", new Constructor(staticModel, "food", new btSphereShape(0.5f)));
        constructors.put("stone", new Constructor(staticModel, "stone", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        constructors.put("butterfly", new Constructor(staticModel, "butterfly", new btConeShape(0.5f, 2f)));
    }
}
