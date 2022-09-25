package com.poerlang.nars3dview.items;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Pool;
import com.poerlang.nars3dview.Item3d;

public class Plane3d implements Pool.Poolable {
    public Decal decal;
    private float mySize = 1;
    public static float decalInitScale = 2f;
    Plane3d(){
        decal = Decal.newDecal(mySize*decalInitScale, mySize*decalInitScale, Item3d.textures[1],true);
    }

    @Override
    public void reset() {

    }

    public void setSize(float size) {
        mySize = size;
        decal.setWidth(size*decalInitScale);//适当放大倍数
        decal.setHeight(size*decalInitScale);
    }
}
