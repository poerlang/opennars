package com.poerlang.nars3dview;
//提醒：启动时在 build and run 参数中添加： -Dfile.encoding=UTF-8
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.poerlang.nars3dview.butterfly.ButterflyMainGame;
import com.poerlang.nars3dview.butterfly.game_objects.GameObject;
import com.poerlang.nars3dview.butterfly.game_objects.Ground;
import com.poerlang.nars3dview.camera.MyCameraController;
import com.poerlang.nars3dview.items.Item3d;
import com.poerlang.nars3dview.items.Line3d;
import com.poerlang.nars3dview.items.Mesh3d;
import com.poerlang.nars3dview.items.line3d.Line3dMeshSegment;
import com.poerlang.nars3dview.gui.GUI;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;

import static com.badlogic.gdx.Gdx.files;
import static com.poerlang.nars3dview.gui.GUI.initFonts;
import static com.poerlang.nars3dview.View3dRefresh.*;
import static com.poerlang.nars3dview.gui.GUI.operatorWinH;
import static java.lang.System.out;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import org.opennars.entity.*;
import org.opennars.main.Nar;

public class MainGame extends InputAdapter implements ApplicationListener {
    public static MainGame inst;
    public static DecalBatch dbatch;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    public PerspectiveCamera cam;
    public PerspectiveCamera cam2;
    public static Array<Item3d> instances = new Array<Item3d>();
    SpriteBatch batch;
    SpriteBatch batchCam2;
    Texture img;
    ExtendViewport extendViewport;
    ScreenViewport screenViewport;
    Vector3 cam2Pos = new Vector3();
    Vector3 cam2LookAtPos = new Vector3();
    public static Nar nar;
    public static boolean imGuiHover;
    int downScreenX;
    int downScreenY;
    private String glslVersion = null;
    private ModelBatch modelBatch;
    private Environment environment;
    private MyCameraController camController;
    private Stage stage;
    private BitmapFont font;
    private Label label;
    private StringBuilder stringBuilder;
    private Vector3 position = new Vector3();
    private Item3d oldSelectItem = null;
    private Item3d selectItem = null;
    private Long windowHandle = 0l;
    private ArrayList<Vector3> list = new ArrayList<Vector3>(3);
    private int printNum;
    public static PrintWriter utf8Printer;
    static {
        Charset charset = StandardCharsets.UTF_8;
        utf8Printer = new PrintWriter(out, true, charset);
    }

    public static ButterflyMainGame butterflyMainGame;

    private boolean drawRay = false;
    private DirectionalShadowLight shadowLight;
    private ModelBatch shadowBatch;
    private TiledDrawable tiledDrawable;

    public static void log(Object s){
        utf8Printer.println(s);
    }

    static Item3d selone = null;
    public static void setSel(String uidStr) {
        for (Item3d visible : visibles) {
            if (visible.uid == (Long.parseLong(uidStr))){
                selone = visible;
                rayTarget.meshModelInstance.transform.setTranslation(selone.getPos());
                return;
            }
        }

    }

    static int cam2W = 503;
    static int cam2H = 200;
    static float oldW=100;
    static float oldH=100;
    @Override
    public void create() {
        oldW = Gdx.graphics.getWidth();
        oldH = Gdx.graphics.getHeight();
        createLabel();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add((shadowLight = new DirectionalShadowLight(1024, 1024, 30f, 30f, 1f, 100f)).set(0.3f, 0.3f, 0.3f, -1f, -.8f, -.2f));
        environment.shadowMap = shadowLight;
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        modelBatch = new ModelBatch();
        batchCam2 = new SpriteBatch();
        img = new Texture("task.png");
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam2 = new PerspectiveCamera(67, cam2W, cam2H);
        cam.position.set(11f, 11f, 11f);
        cam2.position.set(11f, 11f, 11f);
        cam.lookAt(0, 0, 0);
        cam2.lookAt(0, 0, 0);
        cam.near = 0.01f;
        cam.far = 300f;
        cam2.near = 0.01f;
        cam2.far = 300f;
        Item3d.cam = cam;

        camController = new MyCameraController(cam);

        extendViewport = new ExtendViewport(100, 10,99999,99999,cam);
        screenViewport = new ScreenViewport(cam2);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, camController));
        cam.update();
        cam2.update();

        tiledDrawable = new TiledDrawable(new TextureRegion(Item3d.textures[2]));
        tiledDrawable.tint(Color.RED);

        dbatch = new DecalBatch(new CameraGroupStrategy(cam));

        butterflyMainGame = new ButterflyMainGame();
        butterflyMainGame.init();

        initGUI();
        inst = this;

        rayCopy = new Line3d();
        rayTarget = new Mesh3d();
    }
    static Mesh3d rayTarget;
    static Line3d rayCopy;
    static Vector3 rayCopyStart = new Vector3();
    static Vector3 rayCopyEnd = new Vector3();

    private void initGUI() {
        GL.createCapabilities();
        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);

        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.setConfigViewportsNoTaskBarIcon(true);
        initFonts(io);

        windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init(glslVersion);

        ImPlot.createContext();

        final long backupWindowPtr = GLFW.glfwGetCurrentContext();
        out.println(backupWindowPtr);

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, cam2W, cam2H, true);
    }
    static FrameBuffer fbo;
    private void renderGUI() {

        imGuiGlfw.newFrame();
        ImGui.newFrame();

        GUI.showGUI(); // 绘制界面

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
        glfwPollEvents();
    }

    public static Item3d add(Item3d item3d) {
        if(!instances.contains(item3d,false)){
            instances.add(item3d);
        }
        return item3d;
    }
    public static void clearInstances() {
        instances.clear();
    }

    private void createLabel() {
        stage = new Stage();
        font = new BitmapFont(files.internal("bmfont_config_cn_5000_and_en_002_max.fnt"));
        font.setColor(Color.WHITE);
        font.getCache().setColor(1.0f, 183f/255f, 77f/255f, 0.5f);
        font.getCache().tint(new Color(1f,0,0,1));
        label = new Label("Label", new Label.LabelStyle(font, Color.WHITE));

        label.setPosition(16f,6f, Align.bottom);
        stage.addActor(label);
        stringBuilder = new StringBuilder();
    }
    public static LinkedList<Item3d> plane3ds = new LinkedList<>();
    public static LinkedList<Item3d> visibles = new LinkedList<>();

    @Override
    public void render() {
        camController.update();
        extendViewport.apply();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();

        plane3ds.clear();
        visibles.clear();

        refreshCountDown--;
        refresh3DView();

        modelBatch.begin(cam);
        for (final Item3d instance : instances) {
            if (isVisible(cam, instance)) {
                check_for_remove.put(instance.uid,instance);
                visibles.add(instance);
                if (instance.isMesh()){
                    modelBatch.render(instance.mesh3d.meshModelInstance, environment);
                }else if(instance.isLine()){
                    instance.updateLine3d(deltaTime);
                    modelBatch.render(instance.line3d.meshModelInstanceLine, environment);
                }else if(instance.isPlane()){
                    plane3ds.add(instance);
                }
            }
        }

        if(drawRay){
            rayCopy.update(deltaTime);
            modelBatch.render(rayCopy.meshModelInstanceLine, environment);

            if(selone!=null){
                rayTarget.meshModelInstance.transform.setTranslation(selone.getPos());
            }
            modelBatch.render(rayTarget.meshModelInstance, environment);
        }

        modelBatch.end();

        renderPlanes();


        // 绘制 butterfly 场景
        butterflyMainGame.render(modelBatch,environment);
        shadowLight.begin(Vector3.Zero, cam.direction);
        shadowBatch.begin(shadowLight.getCamera());
        for (GameObject instance : ButterflyMainGame.instances) {
            if(instance instanceof Ground) continue;
            shadowBatch.render(instance);
        }
        shadowBatch.end();
        shadowLight.end();

        // 设置第二个摄像机的画面显示位置
        screenViewport.update(cam2W,cam2H);
        screenViewport.setScreenX(Gdx.graphics.getWidth()-cam2W-10);
        screenViewport.setScreenY((int) (operatorWinH + 20));
        screenViewport.apply();

        fbo.begin();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // 将第二个摄像机挂到 butterfly 上，并拍摄其看到的画面
        cam2.position.set(butterflyMainGame.butterfly.transform.getTranslation(cam2Pos));
        cam2LookAtPos.set(butterflyMainGame.butterfly.force).scl(80);
        cam2.up.set(Vector3.Y);
        cam2.lookAt(cam2LookAtPos);
        butterflyMainGame.renderCam2(cam2,environment);
        fbo.end();

        // 绘制摄像机外框，便于在前后两个摄像机背景色相同时区分画面
        ShapeRenderer sr = new ShapeRenderer();
        sr.setColor(Color.GRAY);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        int lineW = 2;
        int x1 = extendViewport.getScreenWidth()-cam2W-10;
        int y1 = (int) (operatorWinH + 20);
        int x2 = x1+cam2W;
        int y2 = y1+cam2H;
        sr.rectLine(x1, y2, x1, y1, lineW);
        sr.rectLine(x1, y1, x2, y1, lineW);
        sr.rectLine(x2, y1, x2, y2, lineW);
        sr.rectLine(x1, y2, x2, y2, lineW);
        sr.end();


        renderGUI();

        renderLabel();

        stage.getViewport().apply();

        batchCam2.begin();
        Texture colorBufferTexture = fbo.getColorBufferTexture();
        textureRegion.setRegion(colorBufferTexture);
        textureRegion.flip(false, true);
        batchCam2.draw(textureRegion,(newW-cam2W-10),(int) (operatorWinH + 20),cam2W,cam2H);
        batchCam2.end();
    }
    TextureRegion textureRegion = new TextureRegion();
    private void renderLabel() {
        stringBuilder.setLength(0);
        stringBuilder.append("  FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append("  Visible: ").append(visibles.size());
        stringBuilder.append("  Selected Item: ").append(selectItem!=null ? getTermString(selectItem) : "none");
        label.setText(stringBuilder);
        stage.getViewport().apply();
        stage.getBatch().setColor(1,1,1,1);
        stage.draw();
    }

    public static String getTermString(Item3d selectItem) {
        if(selectItem instanceof Concept){
            return ((Concept) selectItem).getTerm().toString();
        }else if(selectItem instanceof TermLink){
            return ((TermLink) selectItem).getTarget().toString();
        }
        return selectItem.toString();
    }

    private void renderPlanes() {
        for (int i = 0; i < plane3ds.size(); i++) {
            Item3d item3d = plane3ds.get(i);
            Decal decal = item3d.plane3d.decal;
            item3d.plane3d.updateSize();
            decal.lookAt(cam.position, cam.up);
            dbatch.add(decal);
        }
        dbatch.flush();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(imGuiHover) return true;
        downScreenX = screenX;
        downScreenY = screenY;
        //返回 false 代表其它同类事件函数可以继续执行，true 代表跳过其它同类事件
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(imGuiHover) return true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(imGuiHover) return true;
        // 拖拽在较小范围时才算点击
        boolean dragInSmallSize = (Math.abs(downScreenX - screenX) + Math.abs(downScreenY - screenY)) < 5;
        if (dragInSmallSize) {
            oldSelectItem = selectItem;
            selectItem = null;
            selectItem = getObject(screenX, screenY);
            if(selectItem != null){
                log("selected item3d: \n"+getTermString(selectItem)+"\nuid: "+selectItem.uid+"\n");
                setSelected(selectItem);
            }else{
                setSelected(null);
            }
        }
        return false;
    }

    protected boolean isVisible(final Camera cam, final Item3d instance) {
        instance.getCenter(position);
        return cam.frustum.sphereInFrustum(position, instance.getSize());
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public void setSelected(Item3d itemNow) {
        // 清除旧的选择：
        Item3d item3d;
        if (oldSelectItem != null) {
            oldSelectItem.unSelect();
            if (itemNow!=null && oldSelectItem.uid == itemNow.uid){
                itemNow.unSelect();
                selectItem = null;
                return;
            }
        }

        // 设置当前选中的物体的高亮颜色或材质：
        selectItem = itemNow;
        if (selectItem != null) {
            selectItem.select();
            camController.target.set(selectItem.getPos());
        }
    }
    public Item3d getObject(int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);

        rayCopyStart.set(ray.origin);
        ray.getEndPoint(rayCopyEnd, 200);
        rayCopy.setStartEndPosition(rayCopyStart, rayCopyEnd);

        Item3d result = null;
        float distance = -1;
        for (int i = 0; i < visibles.size(); ++i) {
            final Item3d instance = visibles.get(i);
            //如果是叠加的情况，在多个选中对象中，跳过距离远的
            instance.getCenter(position);
            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance) continue;

            //计算精准落点，是否落点命中半径内部或顶点组内部
            float size = instance.getSize();
            if (instance.isLine()){
                Line3dMeshSegment s = instance.line3d.getSegmentList().get(0);
                list.clear();
                list.add(s.a);
                list.add(s.b);
                list.add(s.c);
                list.add(s.a);
                list.add(s.c);
                list.add(s.d);
                if (Intersector.intersectRayTriangles(ray, list,  null)) {
                    result = instance;
                    distance = dist2;
                }
            }else{
                if (Intersector.intersectRaySphere(ray, position, size, null)) {
                    result = instance;
                    distance = dist2;
                }
            }
        }
        return result;
    }

    @Override
    public void dispose() {
        batchCam2.dispose();
        butterflyMainGame.dispose();
        img.dispose();
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
    static float newW = 100;
    static float newH = 100;
    @Override
    public void resize(int width, int height) {
        newW = width;
        newH = height;
        extendViewport.update(width,height);
    }
}
