package com.poerlang.nars3dview.butterfly.game_objects;

public class Food extends GameObject{
    public final static short FLAG = 1 << 2;
    public final static String NAME = "food";
    public Food() {
        super(NAME);
    }
}
