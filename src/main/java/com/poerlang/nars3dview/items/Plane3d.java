package com.poerlang.nars3dview.items;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Pool;
import com.poerlang.nars3dview.setting.Settings;

public class Plane3d implements Pool.Poolable {
    public Decal decal;
    private float mySize = 1;
    public static float decalInitScale = 2f; // 由于 plane 的贴图有透明外框，整体看上去偏小，为平衡视觉上的大小，这里适当放大倍数
    Plane3d(){
        decal = Decal.newDecal(mySize*decalInitScale, mySize*decalInitScale, Item3d.textures[1],true);
    }

    @Override
    public void reset() {

    }

    public void setSize(float size) {
        mySize = size;
        this.updateSize();
    }

    public void updateSize() {
        decal.setWidth(mySize*decalInitScale * Settings.planeSetting.nodeSize.get());
        decal.setHeight(mySize*decalInitScale * Settings.planeSetting.nodeSize.get());
    }
}
