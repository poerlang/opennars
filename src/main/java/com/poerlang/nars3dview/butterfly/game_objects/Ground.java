package com.poerlang.nars3dview.butterfly.game_objects;

public class Ground extends GameObject{
    public final static short FLAG = 1 << 3;
    public final static String NAME = "ground";
    public static float height = 1f;

    public Ground() {
        super(NAME);
    }
}
