package com.poerlang.nars3dview.setting;

import imgui.app.Color;

public class LineSetting{
    public Color normalStartColor = new Color(0.8f,0.8f,1,0.2f);
    public Color normalEndColor = new Color(1,1,1,0.1f);

    public Color selectStartColor = new Color(1,0,0,0.1f);
    public Color selectEndColor = new Color(1,0.3f,0,0.3f);

    private static final float[] lineWidth = {0.5f};
}
