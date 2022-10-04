package com.poerlang.nars3dview.butterfly.game_objects;

public class Butterfly extends GameObject {

    public final static short FLAG = 1 << 1;
    public final static String NAME = "butterfly";
    public Butterfly() {
        super(NAME);
        moving = true;
    }
    float angle;
    float aSpeed = 3;
    public void angle(float a){
        this.angle += a+aSpeed;
        transform.setFromEulerAngles(this.angle,90,0);
    }
    public void update(float delta) {
        angle(delta);
        move(0,delta);
    }

}
