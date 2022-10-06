package com.poerlang.nars3dview.butterfly.game_objects.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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

import static com.poerlang.nars3dview.butterfly.game_objects.Ground.groundSize;
import static com.poerlang.nars3dview.butterfly.game_objects.Ground.groundSizeHalf;

public class Models {
    public static Model staticModel;
    public static ArrayMap<String, Constructor> constructors;
    public static void initStaticModel(){

//        TextureRegion[] textures = new TextureRegion[]{
//                new TextureRegion(new Texture(Gdx.files.internal("grass.jpg"))),
//        };
        ColorAttribute diffuse = ColorAttribute.createDiffuse(Color.FOREST);
//        TextureAttribute diffuseTexture = TextureAttribute.createDiffuse(textures[0]);
//        TextureAttribute diffuseTexture = TextureAttribute.createNormal(textures[0]);
//        Material groundMat = new Material(diffuse,diffuseTexture);
//        Material groundMat = new Material(diffuseTexture);
        Material groundMat = new Material(diffuse);


        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        MeshPartBuilder groundPart = mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, groundMat);
        BoxShapeBuilder.build(groundPart,groundSize, Ground.height, groundSize);

//        float debugScale = 1f; // show wall
        float debugScale = 0.0000001f; //1f  // hide wall, todo: texture.

        mb.node().id = "wall";
//        MeshPartBuilder wallPart = mb.part("wall", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, groundMat);
        MeshPartBuilder wallPart = mb.part("wall", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createSpecular(Color.LIME)));
        BoxShapeBuilder.build(wallPart,groundSize * debugScale, Ground.height*2 * debugScale, Ground.height * debugScale);

        mb.node().id = "food";
        MeshPartBuilder spherePart = mb.part("food", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createEmissive(Color.BLUE)));
        SphereShapeBuilder.build(spherePart,1f, 1f, 1f, 10, 10);

        mb.node().id = "stone";
        MeshPartBuilder boxPart = mb.part("stone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createEmissive(Color.RED)));
        BoxShapeBuilder.build(boxPart,1f, 1f, 1f);

        mb.node().id = "butterfly";
        MeshPartBuilder cone = mb.part("butterfly", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createEmissive(Color.GOLD)));
        ConeShapeBuilder.build(cone,1f, 2f, 1f, 6);
        staticModel = mb.end();

        constructors = new ArrayMap<>(String.class, Constructor.class);
        constructors.put("ground", new Constructor(staticModel, "ground", new btBoxShape(new Vector3(groundSizeHalf, Ground.height*1.5f, groundSizeHalf)),0f));
        constructors.put("wall", new Constructor(staticModel, "wall", new btBoxShape(new Vector3(groundSize, Ground.height*2.5f, Ground.height*1.5f)),0f));//宽，高，厚
        constructors.put("food", new Constructor(staticModel, "food", new btSphereShape(0.5f),0.3f));
        constructors.put("stone", new Constructor(staticModel, "stone", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)),1f));
        constructors.put("butterfly", new Constructor(staticModel, "butterfly", new btBoxShape(new Vector3(0.6f, 0.7f, 0.6f)),1.5f));
    }
}
