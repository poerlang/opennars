package com.poerlang.nars3dview.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Pool;

public class Mesh3d implements Pool.Poolable {
    public static ModelInstance meshStaticModel;
    public ModelInstance meshModelInstance;

    static {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        meshStaticModel = new ModelInstance(model);
    }

    public Mesh3d() {
        meshModelInstance = new ModelInstance(meshStaticModel);
    }

    @Override
    public void reset() {

    }
}
