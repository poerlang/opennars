package com.poerlang.nars3dview.gui;

import com.poerlang.nars3dview.MainGame;
import com.poerlang.nars3dview.font.ChineseCharRanges;
import com.poerlang.nars3dview.font.FontAwesomeIcons;
import com.poerlang.nars3dview.guiexamples.Extra;
import com.poerlang.nars3dview.setting.Settings;
import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.*;
import imgui.type.ImString;
import org.opennars.main.Shell;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import static com.poerlang.nars3dview.MainGame.butterflyMainGame;
import static com.poerlang.nars3dview.MainGame.nar;

public class GUI extends Application {
    private final ImString str = new ImString(5);
    private final float[] flt = new float[1];
    private int count = 0;

    @Override
    protected void configure(final Configuration config) {
        config.setTitle("Example Application");
    }

    @Override
    protected void initImGui(final Configuration config) {
        super.initImGui(config);

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);                                // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);

        initFonts(io);
    }

    /**
     * Example of fonts configuration
     * For more information read: https://github.com/ocornut/imgui/blob/33cdbe97b8fd233c6c12ca216e76398c2e89b0d8/docs/FONTS.md
     */
    public static void initFonts(final ImGuiIO io) {
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();
        final ImFontConfig iconConfig = new ImFontConfig();
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        fontConfig.setFontDataOwnedByAtlas(false);
        fontConfig.setGlyphOffset(0f,0f);
        iconConfig.setGlyphOffset(0f,2f);
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);
        final short[] glyphRanges = rangesBuilder.buildRanges();
        ImFont imFont = fontAtlas.addFontFromMemoryTTF(loadFromResources("./LXGWWenKai-Regular.ttf"), 20, fontConfig, ChineseCharRanges.ranges);
        io.setFontDefault(imFont);
        iconConfig.setMergeMode(true);
        fontAtlas.addFontFromMemoryTTF(loadFromResources("./fa-regular-400.otf"), 18, iconConfig, glyphRanges); // font awesome
        fontAtlas.addFontFromMemoryTTF(loadFromResources("./fa-solid-900.ttf"), 18, iconConfig, glyphRanges); // font awesome
        io.getFonts().build();
        fontConfig.destroy();
    }

    public static String testFile = "nal/single_step/nal1.3.nal";
    static int waitCountToShowCycle = 0;
    public static void startNARS() throws IOException, ParserConfigurationException, ParseException, ClassNotFoundException, InterruptedException, InvocationTargetException, InstantiationException, NoSuchMethodException, IllegalAccessException, SAXException {
//        第 3 个参数是启动时默认运行的 nal 文件（在assets目录中）； 第 4 给参数是一开始默认运行的循环数
        String[] strParams ={"null","null",testFile,"100"};
//        String[] strParams ={"null","null","null","null"};
        nar = Shell.main(strParams);
    }

    public static void showGUI(){
        MainGame.imGuiHover = false;
        ImGuiViewport imGuiViewport = imgui.internal.ImGui.getMainViewport();
        ImGui.setNextWindowPos(imGuiViewport.getPosX()+10,imGuiViewport.getPosY()+10);
        //第一个窗口，用来启动 NARS：
        ImGui.begin("NARS Control", ImGuiWindowFlags.AlwaysAutoResize|ImGuiCond.Once);
        ImGui.separator();
        if(nar == null){
            if(ImGui.arrowButton("Start NARS", ImGuiDir.Right)){
                try {
                    startNARS();
                } catch (IOException | ParserConfigurationException | ParseException | ClassNotFoundException | InterruptedException | InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException | SAXException e) {
                    e.printStackTrace();
                }
            }
            ImGui.sameLine();
            ImGui.text("Start NARS");
        }else{
            if(nar.running || waitCountToShowCycle>0){
                if(waitCountToShowCycle>0){
                    waitCountToShowCycle--;
                }
                if(ImGui.button("Pause NARS")){
                    // nar.stop();//暂时不使用线程，而是直接用 cycle step 来驱动，所以这里也暂时不用 stop
                }
                ImGui.sameLine(); ImGui.textColored(0,1f,0,1,"NARS is running "+FontAwesomeIcons.Heart);
            }else{
                if(ImGui.button("Cycle NARS")){
                    waitCountToShowCycle = 10;
                    nar.cycles(Settings.narsSetting.step.get());
                }
                ImGui.sameLine(); ImGui.textColored(0.6f,0.6f,0.6f,1,"NARS has stopped "+ FontAwesomeIcons.Snowflake);
            }
            ImGui.sliderInt("Cycle n Step", Settings.narsSetting.step.getData(), 1,300);
            ImGui.text("Test File: "); ImGui.sameLine(); ImGui.textColored(0.6f,0.6f,0.6f,1,testFile);
            ImGui.separator();

            ImGui.text("Current Cycle: ");
            ImGui.sameLine();
            ImGui.textColored(0.1f,0.8f,0,1,nar.cycle+"");
        }
        if(nar!=null){
            ImGui.text("Concept Number:");
            ImGui.sameLine();
            ImGui.textColored(1f,0.8f,0.3f,1,nar.memory.concepts.size()+"");

            ImGui.separator();
            ImGui.sliderFloat("Refresh Percentage", Settings.renderSetting.refreshPercentage.getData(),0,1,"%.6f %");
            ImGui.sliderInt("Level Threshold", Settings.renderSetting.levelThreshold.getData(),1,nar.narParameters.CONCEPT_BAG_LEVELS);
            ImGui.sliderFloat("Priority Threshold", Settings.renderSetting.priorityThreshold.getData(),0,1f);
            ImGui.sliderInt("Max 3d object", Settings.renderSetting.maxConceptIn3dView.getData(),1,1000);
            ImGui.sliderFloat("Concept vs Link", Settings.renderSetting.ConceptVsLink.getData(),0,1,"%.6f %");
            ImGui.separator();
        }else{
            ImGui.text("Concept Number: 0");
        }
        ImVec2 windowAPos = ImGui.getWindowPos();
        ImVec2 windowASize = ImGui.getWindowSize();
        if(ImGui.isAnyItemHovered() || ImGui.isWindowHovered()) MainGame.imGuiHover = true;
        ImGui.end();

        if(nar!=null) {
            //第二个窗口，用来设置线宽、颜色等：
            ImGui.setNextWindowPos(windowAPos.x, windowAPos.y + windowASize.y + 20, ImGuiCond.Always);
            ImGui.begin("3D View Setting", ImGuiWindowFlags.AlwaysAutoResize);
            ImGui.sliderFloat("Line3d Width", Settings.lineSetting.lineWidth.getData(), 0, 5);
            ImGui.sliderFloat("Concept Base size", Settings.planeSetting.nodeSize.getData(), 0.01f, 5);
            ImGui.text("Line3d Normal Color:");
            ImGui.colorEdit4("Normal Start Point", Settings.lineSetting.normalStartColor.data);
            ImGui.colorEdit4("Normal End Point", Settings.lineSetting.normalEndColor.data);

            ImGui.separator();

            ImGui.text("Line3d Selected Color:");
            ImGui.colorEdit4("Selected Start Point", Settings.lineSetting.selectStartColor.data);
            ImGui.colorEdit4("Selected End Point", Settings.lineSetting.selectEndColor.data);
            if(ImGui.isAnyItemHovered() || ImGui.isWindowHovered()) MainGame.imGuiHover = true;
            ImGui.end();
        }

        //第三个窗口，输入 nars 语
        InputTextEdit.show();


        //第四给窗口，测试具身相关的操作
        imgui.internal.ImGui.setNextWindowSize(winW, winH, ImGuiCond.Once);
        imgui.internal.ImGui.setNextWindowPos(imGuiViewport.getPosX() + imGuiViewport.getSizeX() - winW - 10, imGuiViewport.getPosY()+imGuiViewport.getSizeY() -(winH+10) , ImGuiCond.Always);
        ImGui.begin("Operator Test",ImGuiWindowFlags.AlwaysAutoResize);
        ImVec2 windowSize = ImGui.getWindowSize();
        winW = windowSize.x;
        winH = windowSize.y;
        ImGui.dragFloat2("Force dir",Settings.butterflySetting.forceDir);
        ImGui.sliderFloat("Force num",Settings.butterflySetting.forceNum.getData(),0.001f,30f);
        if(ImGui.button("Add Force (Impulse)")){
            butterflyMainGame.butterfly.move();
        }
        if(ImGui.isAnyItemHovered() || ImGui.isWindowHovered()) MainGame.imGuiHover = true;
        ImGui.end();
    }
    static float winW = 300;
    static float winH = 190;
    @Override
    public void process() {
        ImGui.text("Hello, World! " + FontAwesomeIcons.Smile);
        ImGui.text("Hello, World! "+FontAwesomeIcons.Heart);
//        if (ImGui.button(FontAwesomeIcons.Save + " Save")) {
//        if (ImGui.button(FontAwesomeIcons.Allergies+" Save")) {
        if (ImGui.button("Save")) {
            count++;
        }
        ImGui.sameLine();
        ImGui.text(String.valueOf(count));
        ImGui.inputText("string", str, ImGuiInputTextFlags.CallbackResize);
        ImGui.text("Result: " + str.get());
        ImGui.sliderFloat("float", flt, 0, 1);
        ImGui.separator();
        ImGui.text("Extra");
        Extra.show(this);
    }

    public static byte[] loadFromResources(String name) {
//        try {
//            return Files.readAllBytes(Paths.get(GUI.class.getResource(name).toURI()));
//        } catch (IOException | URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//
        try {
            Path path = Paths.get(name);
            System.out.println(path.toAbsolutePath());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(final String[] args) {
        launch(new GUI());
//        System.exit(0);
    }
}