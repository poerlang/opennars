package com.poerlang.nars3dview.setting;

import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

public class RenderSetting {
    public ImBoolean AutoRender = new ImBoolean(true);
    public ImFloat refreshPercentage = new ImFloat(1f);
    public ImInt maxConceptIn3dView = new ImInt(1000);

    public ImInt levelThreshold = new ImInt(50);
    public ImFloat priorityThreshold = new ImFloat(0.01f);
}
