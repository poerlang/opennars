package com.poerlang.nars3dview.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.poerlang.nars3dview.items.line3d.Line3dMeshSegment;

import java.util.ArrayList;
import java.util.List;

public class Line3d implements Pool.Poolable {

    public Vector3 startPoint = new Vector3();
    public Vector3 endPoint = new Vector3();
    private Model model;
    public int segmentNum;
    private final List<Line3dMeshSegment> segmentList = new ArrayList<>();

    private Color colorStart;
    private Color colorEnd;
    private MeshPartBuilder meshPartBuilder;

    private final float[] vertices;
    private final Vector3 center = new Vector3();
    public ModelInstance meshModelInstanceLine;
    public float width = 0.5f;
    public boolean isSelect;

    public Line3d() {
        this(1);
    }
    /**
     * 3D空间中的粗线（可调整宽度和渐变色）。参考 com.ceesiz.trail.trail ，但去除动态渐进跟踪的部分，并加入向摄像头偏转、以及尾部定位等功能。
     * @param segmentNum 段数，目前只支持一段
     */
    public Line3d(int segmentNum) {
        this.segmentNum = segmentNum;
        vertices = new float[segmentNum * 42];
        unselect();
        create();
    }

    public void unselect() {
        isSelect = false;
    }

    public void select() {
        isSelect = true;
    }

    public void create(){
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();
        meshPartBuilder = modelBuilder.part(
                "TRIANGLE",
                GL20.GL_TRIANGLES,
                3,
                new Material(
                        new IntAttribute(IntAttribute.CullFace, 0),
                        new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
                )
        );
        createSegments();
        model = modelBuilder.end();
        meshModelInstanceLine = new ModelInstance(this.getModel());
    }

    public void update(float dt){
        int index = 0;

        for (int i = 0; i < segmentNum; i++) {
            Line3dMeshSegment meshSegment = segmentList.get(i);
            meshSegment.setPositionByTwoPoint(startPoint, endPoint);
            for (int j = 0; j < 42; j++) {
                vertices[index] = meshSegment.vertices[j];
                index++;
            }
        }

        model.meshParts.get(0).mesh.setVertices(vertices);
    }

    public void setStartEndPosition(Vector3 p1, Vector3 p2){
        startPoint.set(p1);
        endPoint.set(p2);
    }

    public void createSegments(){
        Line3dMeshSegment segment = new Line3dMeshSegment(this);
            segment.create();

        segmentList.add(segment);
    }

    public MeshPartBuilder getMeshPartBuilder() {
        return meshPartBuilder;
    }

    public void setGradientColors(Color colorStart, Color colorEnd) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
//        for (Line3dMeshSegment segment : segmentList) {
//            segment.lerpColors().lerpColors();
//        }
    }

    public void setColor(Color color) {
        this.colorStart = color;
        this.colorEnd = color;
    }

    public Color getColorEnd() {
        return colorEnd;
    }

    public Color getColorStart() {
        return colorStart;
    }

    public List<Line3dMeshSegment> getSegmentList() {
        return segmentList;
    }

    public Model getModel() {
        return model;
    }

    public Vector3 getCenter() {
        center.set(startPoint).lerp(endPoint, 0.5f);
        return center;
    }

    public float getWidth(){
        return width;
    }

    public void setWidth(float size) {
        width = size;
    }

    @Override
    public void reset() {

    }
}
