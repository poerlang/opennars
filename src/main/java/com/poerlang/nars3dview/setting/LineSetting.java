package com.poerlang.nars3dview.setting;

import imgui.app.Color;
import imgui.type.ImFloat;

public class LineSetting{
    public Color normalStartColor = new Color(0.8f,0.8f,1,0.2f);
    public Color normalEndColor = new Color(1,1,1,0.1f);

    public Color selectStartColor = new Color(1,0,0,0.1f);
    public Color selectEndColor = new Color(1,0.3f,0,0.3f);

    public ImFloat lineWidth = new ImFloat(0.128f);
}
