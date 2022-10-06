package com.poerlang.nars3dview.butterfly;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.utils.Array;
import com.poerlang.nars3dview.butterfly.game_objects.GameObject;

public class MyContactListener extends ContactListener {
    @Override
    public boolean onContactAdded (int userValue0, int partId0, int index0, boolean match0, int userValue1, int partId1,
                                   int index1, boolean match1) {
        Array<GameObject> instances = ButterflyMainGame.instances;
        if (match0)
            ((ColorAttribute)instances.get(userValue0).materials.get(0).get(ColorAttribute.Emissive)).color.set(Color.GOLD);
        if (match1)
            ((ColorAttribute)instances.get(userValue1).materials.get(0).get(ColorAttribute.Emissive)).color.set(Color.GOLD);
        return true;
    }
}