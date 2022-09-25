package com.poerlang.nars3dview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.poerlang.nars3dview.items.Line3d;
import com.poerlang.nars3dview.items.Mesh3d;
import com.poerlang.nars3dview.items.Plane3d;

import java.util.concurrent.atomic.AtomicLong;

public class Item3d {

    public long uid;
    private Item3dType type;

    public Line3d line3d;
    public Plane3d plane3d;
    public Mesh3d mesh3d;

    private float size = 1;
    private final Vector3 myPosition = new Vector3();

    private static final Material selectionMaterial;
    private static final Material originalMaterial;

    private static final Vector3 p1 = new Vector3();
    private static final Vector3 p2 = new Vector3(5,0,0);
    private static final Vector3 d = new Vector3();
    public static PerspectiveCamera cam;

    public static TextureRegion[] textures;
    private static AtomicLong static_uid = new AtomicLong();
    static {
        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.RED));
        originalMaterial = new Material();
        textures = new TextureRegion[]{
            new TextureRegion(new Texture(Gdx.files.internal("task.png"))),
            new TextureRegion(new Texture(Gdx.files.internal("node.png")))
        };
        Pools.get(Plane3d.class).fill(200);
        Pools.get(Mesh3d.class).fill(200);
        Pools.get(Line3d.class).fill(200);
    }


    public Item3d() {
        this(Item3dType.NONE);
    }
    public Item3d(Item3dType type) {
        this.uid = static_uid.incrementAndGet();
        if(type==Item3dType.MESH){
            toMesh();
        }else if(type==Item3dType.LINE){
            toLine();
        }else if(type==Item3dType.PLANE){
            toPlane();
        }else{
            toNone();
        }
    }
    public boolean isLine() {
        return type==Item3dType.LINE;
    }
    public boolean isNone() {
        return type==Item3dType.NONE;
    }
    public boolean isPlane() {
        return type==Item3dType.PLANE;
    }
    public boolean isMesh() {
        return type==Item3dType.MESH;
    }

    private void toNone() {
        init3d();
        if(type==Item3dType.LINE && line3d!=null){
            Pools.get(Line3d.class).free(line3d);
            this.line3d = null;
        }
        if(type==Item3dType.MESH && mesh3d!=null){
            Pools.get(Mesh3d.class).free(mesh3d);
            this.mesh3d = null;
        }
        if(type==Item3dType.PLANE && plane3d!=null){
            Pools.get(Plane3d.class).free(plane3d);
            this.plane3d = null;
        }
        this.type = Item3dType.NONE;
    }
    protected int init3d(){
        change = true;
        return 0;
    }
    public Item3d toPlane() {
        type = Item3dType.PLANE;
        if(plane3d == null){
            plane3d = Pools.get(Plane3d.class).obtain();
            init3d();
        }
        return this;
    }

    public Item3d toLine() {
        type = Item3dType.LINE;
        if(line3d == null){
            line3d = Pools.get(Line3d.class).obtain();
            init3d();
        }
        return this;
    }

    public Item3d toMesh() {
        type = Item3dType.MESH;
        if(mesh3d == null){
            mesh3d = Pools.get(Mesh3d.class).obtain();
            init3d();
        }
        return this;
    }

    /**
     * 设定 item 的位置，如果设定的是 line3d 类型的位置，则这里只设定了 line3d 的起始端 ，如果要同时设定 line3d 的两个端点，使用 setLinePos
     * @see Item3d#setLinePos
     **/
    public void setPos(Vector3 pos){
        myPosition.set(pos);
        if(isPlane()){
            plane3d.decal.setPosition(pos);
        }else if(isLine()){
            line3d.startPoint.set(pos);
        }else{
            mesh3d.meshModelInstance.transform.setTranslation(pos);
        }
    }
    private Vector3 mid = new Vector3();
    public void setLinePos(Item3d a, Item3d b){
        Vector3 posA = a.getPos();
        Vector3 posB = b.getPos();
        mid.set(posA).lerp(posB,0.5f);
        setLinePos(posA,mid,a.getSize(),b.getSize());
    }
    public void setLinePos(Vector3 posA, Vector3 posB, float radiusA, float radiusB){
        myPosition.set(posA);

        p1.set(posA);
        p2.set(posB);
        d.set(p2);
        d.sub(p1);
        float len = d.len() - radiusA - radiusB;
        Vector3 r1 = d.setLength(radiusA);
        p1.add(r1);
        d.setLength(len);
        p2.set(p1);
        p2.add(d);
        line3d.setStartEndPosition(p1,p2);
    }
    public Vector3 getPos() {
        return myPosition;
    }
    public Vector3 getPos(Vector3 pos) {
        return pos.set(myPosition);
    }
    public void getCenter(Vector3 position) {
        if(isMesh()){
            mesh3d.meshModelInstance.transform.getTranslation(position);
        }else if(isLine()){
            position.set(line3d.getCenter());
        }else{
            position.set(plane3d.decal.getPosition());
        }
    }
    public float getSize() {
       return size;
    }
    public void setLineWidth(float s){
        line3d.setWidth(s);
    }
    public void setSize(float s){
        size = s;
        if(isPlane()){
            plane3d.setSize(s);
        }else if(isLine()){
            setLineWidth(size);
        }else if(isMesh()){
            // 可能可以参考的链接：https://stackoverflow.com/questions/65857776/how-to-first-scale-and-then-rotate-a-model-instance-in-libgdx
            mesh3d.meshModelInstance.transform.scale(size,size,size);
        }
    }
    public void unSelect() {
        if (isPlane()) {
            plane3d.decal.setColor(1f, 1f, 1f, 1f);
        } else if(isMesh()){
            Material mat = mesh3d.meshModelInstance.materials.get(0);
            mat.set(originalMaterial);
        }else if(isLine()){
            line3d.unselect();
        }
    }

    public void select() {
        if (isPlane()) {
            plane3d.decal.setColor(1, 0, 0, 1);
        } else if(isMesh()){
            Material mat = mesh3d.meshModelInstance.materials.get(0);
            originalMaterial.clear();
            originalMaterial.set(mat);
            mat.clear();
            mat.set(selectionMaterial);
        }else if(isLine()){
            line3d.select();
        }
    }

    public void updateLine3d(float deltaTime) {
        line3d.update(deltaTime);
    }

    public boolean change;
    public void calcPos() {
        if(!change) return;
        if(isLine()){
            change = false;
            return; // line 暂时通过直接指定两个端点的方式来处理
        }
        float r = 100f;
        float half = (float) (r*0.5);
        Vector3 vector3 = new Vector3((float) (Math.random() * r - half), (float) Math.random() * r - half, (float) Math.random() * r - half);
        myPosition.set(vector3);
        if(isPlane()){
            plane3d.decal.setPosition(vector3);
        }else if(isMesh()){
            mesh3d.meshModelInstance.transform.setTranslation(myPosition);
        }
        change = false;
    }
}