package com.poerlang.nars3dview;

import com.poerlang.nars3dview.font.FontAwesomeIcons;
import com.poerlang.nars3dview.guiexamples.Extra;
import com.poerlang.nars3dview.setting.Settings;
import imgui.*;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.*;
import imgui.type.ImString;
import org.opennars.entity.Concept;
import org.opennars.entity.TermLink;
import org.opennars.language.Term;
import org.opennars.main.Shell;
import org.opennars.storage.Bag;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

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
        fontAtlas.addFontDefault(); // Add default font for latin glyphs

        // You can use the ImFontGlyphRangesBuilder helper to create glyph ranges based on text input.
        // For example: for a game where your script is known, if you can feed your entire script to it (using addText) and only build the characters the game needs.
        // Here we are using it just to combine all required glyphs in one place
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        rangesBuilder.addRanges(fontAtlas.getGlyphRangesDefault());
        rangesBuilder.addRanges(fontAtlas.getGlyphRangesCyrillic());
//        rangesBuilder.addRanges(fontAtlas.getGlyphRangesChineseFull());
//        rangesBuilder.addRanges(fontAtlas.getGlyphRangesJapanese());
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);

        // Font config for additional fonts
        // This is a natively allocated struct so don't forget to call destroy after atlas is built
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setFontDataOwnedByAtlas(false);
        fontConfig.setGlyphOffset(0,3f);
        fontConfig.setMergeMode(true);  // Enable merge mode to merge cyrillic, japanese and icons with default font
//        fontConfig.setPixelSnapH(true);
        final short[] glyphRanges = rangesBuilder.buildRanges();
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("Tahoma.ttf"), 14, fontConfig, glyphRanges); // cyrillic glyphs
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("NotoSansCJKjp-Medium.otf"), 14, fontConfig, glyphRanges); // japanese glyphs
        fontAtlas.addFontFromMemoryTTF(loadFromResources("./fa-regular-400.otf"), 14, fontConfig, glyphRanges); // font awesome
        fontAtlas.addFontFromMemoryTTF(loadFromResources("./fa-solid-900.ttf"), 14, fontConfig, glyphRanges); // font awesome

//        byte[] fontData = loadFromResources("./Alibaba-PuHuiTi-Regular.ttf");
//        ImFont imFont = io.getFonts().addFontFromMemoryTTF(fontData, 14, fontConfig, glyphRanges);

//        fontConfig.setFontDataOwnedByAtlas(false);
//        io.setFontDefault(imFont);

        io.getFonts().build();

        fontConfig.destroy();
    }

    private static final float[] printValue = {1f};
    public static String testFile = "nal/single_step/nal1.3.nal";
    public static void startNARS() throws IOException, ParserConfigurationException, ParseException, ClassNotFoundException, InterruptedException, InvocationTargetException, InstantiationException, NoSuchMethodException, IllegalAccessException, SAXException {
        String[] strParams ={"null","null",testFile,"100"};
        nar = Shell.main(strParams);
    }
    public static void showGUI(){
        //第一个窗口，用来启动 NARS：
        ImGui.begin("NARS Info Window", ImGuiWindowFlags.AlwaysAutoResize);

        if(nar == null){
            if(ImGui.arrowButton("Start NARS", ImGuiDir.Right)){
                try {
                    startNARS();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
            ImGui.sameLine();
            ImGui.text("Start NARS");
        }else{
            if(nar.running){
                if(ImGui.button("Pause NARS")){
                    nar.stop();
                }
                ImGui.sameLine(); ImGui.textColored(0,1f,0,1,"NARS is running "+FontAwesomeIcons.Heart);
            }else{
                if(ImGui.button("Cycle NARS")){
                    nar.cycles(Settings.narsSetting.step[0]);
                }
                ImGui.sameLine(); ImGui.textColored(0.6f,0.6f,0.6f,1,"NARS has stopped "+ FontAwesomeIcons.Snowflake);
                ImGui.sliderInt("Cycle n Step", Settings.narsSetting.step, 1,20);
            }
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

            if(ImGui.arrowButton("Refresh Concepts in 3D View",1)){
                nar.stop();
                refresh3DView(nar.memory.concepts);
            }
            ImGui.sameLine(); ImGui.text("Refresh Concepts in 3D View");
            ImGui.sliderFloat("Show Percentage", printValue,0,1,"%.2f %");
            ImGui.separator();
        }else{
            ImGui.text("Concept Number: 0");
        }
        if(ImGui.isAnyItemHovered()){
            MainGame.imGuiHover = true;
        }else {
            MainGame.imGuiHover = false;
        }
        ImVec2 windowAPos = ImGui.getWindowPos();
        ImVec2 windowASize = ImGui.getWindowSize();
        ImGui.end();

        //第二个窗口，用来设置线宽、颜色等：
        ImGui.setNextWindowPos(windowAPos.x,windowAPos.y+windowASize.y+20);
        ImGui.begin("3D View Setting", ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.sliderFloat("Line3d Width", Settings.lineSetting.lineWidth, 0, 5);
        ImGui.text("Line3d Normal Color:");
        ImGui.colorEdit4("Normal Start Point", Settings.lineSetting.normalStartColor.data);
        ImGui.colorEdit4("Normal End Point", Settings.lineSetting.normalEndColor.data);

        ImGui.separator();

        ImGui.text("Line3d Selected Color:");
        ImGui.colorEdit4("Selected Start Point", Settings.lineSetting.selectStartColor.data);
        ImGui.colorEdit4("Selected End Point", Settings.lineSetting.selectEndColor.data);
        if(ImGui.isAnyItemHovered()){
            MainGame.imGuiHover = true;
        }else {
            MainGame.imGuiHover = false;
        }
        ImGui.end();
    }

    @Override
    public void process() {
        ImGui.text("Hello, World! " + FontAwesomeIcons.Smile);
        ImGui.text("Hello, World! "+FontAwesomeIcons.Heart);
//        if (ImGui.button(FontAwesomeIcons.Save + " Save")) {
        if (ImGui.button(FontAwesomeIcons.Allergies+" Save")) {
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

    private static byte[] loadFromResources(String name) {
//        try {
//            return Files.readAllBytes(Paths.get(GUI.class.getResource(name).toURI()));
//        } catch (IOException | URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
        try {
            Path path = Paths.get(name);
            System.out.println(path.toAbsolutePath());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void refresh3DView(Bag<Concept, Term> entries) {
        int count = 0;
        int printNum = Math.round(printValue[0] * entries.size());
        MainGame.clearInstances();
        for (Concept concept : entries) {
            addConceptTo3DView(concept);
            count++;
            if(count>printNum){
                break;
            }
        }
        for (Item3d entry : MainGame.instances) {
            if(entry instanceof Concept) {
                Concept concept = (Concept) entry;
                // for (TaskLink taskLink : concept.getTaskLinkBag().getNameTable().values()) {
                //     System.out.println(taskLink.getTargetTask());
                // }
                for (TermLink termLink : concept.termLinks) {
                    termLink.toLine();
                    MainGame.add(termLink);
                    Concept targetConcept = nar.memory.concept(termLink.getTarget());
                    if(targetConcept == null) continue;
                    if(targetConcept.isNone()){
                        addConceptTo3DView(targetConcept);
                    }
                    termLink.setLinePos(concept, targetConcept);
                }
            }
        }
    }

    private static void addConceptTo3DView(Concept concept) {
        concept.toPlane();
        concept.calcPos();
        concept.setSize(2);
        MainGame.add(concept);
    }

    public static void main(final String[] args) {
        launch(new GUI());
//        System.exit(0);
    }
}