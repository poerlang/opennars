package com.poerlang.nars3dview.items.line3d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.poerlang.nars3dview.Item3d;
import com.poerlang.nars3dview.setting.Settings;
import com.poerlang.nars3dview.items.Line3d;

public class Line3dMeshSegment {

    private Line3d line3d;

    // Settings
    Color colorStart;
    Color colorEnd;

    // Coordinates
    public Vector3 position;
    public Vector3 direction = new Vector3();
    public Vector3 up = new Vector3();
    public Vector3 cross = new Vector3();

    public Vector3 a = new Vector3();
    public Vector3 b = new Vector3();
    public Vector3 c = new Vector3();
    public Vector3 d = new Vector3();

    public float[] vertices = new float[42];

    public Line3dMeshSegment(Line3d parent){
        this.line3d = parent;
//        lerpColors();
    }
    public void create(){
       line3d.getMeshPartBuilder().triangle(
           a, colorStart,
           b, colorStart,
           c, colorEnd
       );

        line3d.getMeshPartBuilder().triangle(
           c, colorEnd,
           d, colorEnd,
           a, colorStart
       );
    }

    Vector3 point2 = new Vector3();
    Vector3 point1 = new Vector3();
    public void setPositionByTwoPoint(Vector3 start, Vector3 end) {
        point2.x = Item3d.cam.direction.x;
        point2.y = Item3d.cam.direction.y;
        point2.z = Item3d.cam.direction.z;

        this.direction.set(point2);
        this.up.set(Item3d.cam.up);

        cross.set(direction).crs(up);
        point2.set(start);
        point1.set(end);
        Vector3 dd = point1.sub(point2);
        cross.set(dd).crs(this.direction);

        this.cross.setLength(Settings.lineSetting.lineWidth.get() * 0.5f);

        a.set(start);
        a.add(cross);

        b.set(start);
        b.sub(cross);

        c.set(end);
        c.sub(cross);

        d.set(end);
        d.add(cross);

        this.updateVertices();
    }

    public Line3dMeshSegment lerpColors() {
        colorStart = line3d.getColorStart();
        colorEnd = line3d.getColorEnd();
        colorStart.set(colorStart);
        colorEnd.set(colorEnd);
        return this;
    }

    public void updateVertices(){
        imgui.app.Color startColor;
        imgui.app.Color endColor;
        if(!line3d.isSelect){
            startColor = Settings.lineSetting.normalStartColor;
            endColor = Settings.lineSetting.normalEndColor;
        }else {
            startColor = Settings.lineSetting.selectStartColor;
            endColor = Settings.lineSetting.selectEndColor;
        }

        vertices[0] = a.x;
        vertices[1] = a.y;
        vertices[2] = a.z;

        vertices[3] = startColor.getRed();
        vertices[4] = startColor.getGreen();
        vertices[5] = startColor.getBlue();
        vertices[6] = startColor.getAlpha();

        vertices[7] = b.x;
        vertices[8] = b.y;
        vertices[9] = b.z;

        vertices[10] = startColor.getRed();
        vertices[11] = startColor.getGreen();
        vertices[12] = startColor.getBlue();
        vertices[13] = startColor.getAlpha();

        vertices[14] = c.x;
        vertices[15] = c.y;
        vertices[16] = c.z;

        vertices[17] = endColor.getRed();
        vertices[18] = endColor.getGreen();
        vertices[19] = endColor.getBlue();
        vertices[20] = endColor.getAlpha();

        vertices[21] = c.x;
        vertices[22] = c.y;
        vertices[23] = c.z;

        vertices[24] = endColor.getRed();
        vertices[25] = endColor.getGreen();
        vertices[26] = endColor.getBlue();
        vertices[27] = endColor.getAlpha();

        vertices[28] = d.x;
        vertices[29] = d.y;
        vertices[30] = d.z;

        vertices[31] = endColor.getRed();
        vertices[32] = endColor.getGreen();
        vertices[33] = endColor.getBlue();
        vertices[34] = endColor.getAlpha();

        vertices[35] = a.x;
        vertices[36] = a.y;
        vertices[37] = a.z;

        vertices[38] = startColor.getRed();
        vertices[39] = startColor.getGreen();
        vertices[40] = startColor.getBlue();
        vertices[41] = startColor.getAlpha();

    }

}
