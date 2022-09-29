package com.poerlang.nars3dview;

import com.poerlang.nars3dview.setting.Settings;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiButtonFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.opennars.main.Debug;
import org.opennars.main.Nar;

import java.util.HashMap;
import java.util.Map;

public class InputTextEdit {
    private static final TextEditor EDITOR = new TextEditor();

    static {
        TextEditorLanguageDefinition lang = TextEditorLanguageDefinition.cPlusPlus();

        String[] ppnames = {
                "#",
        };
        String[] ppvalues = {
                "word",
        };

        // Adding custom preproc identifiers
        Map<String, String> preprocIdentifierMap = new HashMap<>();
        for (int i = 0; i < ppnames.length; ++i) {
            preprocIdentifierMap.put(ppnames[i], ppvalues[i]);
        }
        lang.setPreprocIdentifiers(preprocIdentifierMap);

        String[] identifiers = {
            "-->",
            "==>",
        };
        String[] idecls = {
            "inheritance",
            "implication",
        };

        // Adding custom identifiers
        Map<String, String> identifierMap = new HashMap<>();
        for (int i = 0; i < identifiers.length; ++i) {
            identifierMap.put(identifiers[i], idecls[i]);
        }
        lang.setIdentifiers(identifierMap);

        EDITOR.setLanguageDefinition(lang);

        // Adding error markers
        Map<Integer, String> errorMarkers = new HashMap<>();
//        errorMarkers.put(1, "Expected '>'");
        EDITOR.setErrorMarkers(errorMarkers);

        EDITOR.setPalette(new int[]{
            0xff7f7f7f,	// Default
            0xffd69c56,	// Keyword
            0xff00ff00,	// Number
            0xff7070e0,	// String
            0xff70a0e0, // Char literal
            0xffffffff, // Punctuation
            0xff408080,	// Preprocessor
            0xffaaaaaa, // Identifier
            0xff9bc64d, // Known identifier
            0xffc040a0, // Preproc identifier
            0xffffffff, // Comment (single line)
            0xffffffff, // Comment (multi line)
            0xff101010, // Background
            0xffe0e0e0, // Cursor
            0x80a06020, // Selection
            0x800020ff, // ErrorMarker
            0x40f08000, // Breakpoint
            0xff707000, // Line number
            0x40000000, // Current line fill
            0x40808080, // Current line fill (inactive)
            0x40a0a0a0, // Current line edge
        });
        EDITOR.setTextLines(new String[]{
            "",
            "",
            "<swan --> swimmer>. %0.90%",
            "<swan --> bird>.",
            "<小特 --> 鸟>.",
            "<鸟 --> 动物>.",
            "",
            "",
        });
    }

    public static void show() {
        if(MainGame.nar == null)
            return;

        int inputEditorWidth = 300;
        imgui.internal.ImGui.setNextWindowSize(inputEditorWidth, 276, ImGuiCond.Once);
        ImGuiViewport imGuiViewport = imgui.internal.ImGui.getMainViewport();
        imgui.internal.ImGui.setNextWindowPos(imGuiViewport.getPosX()+imGuiViewport.getSizeX()  - inputEditorWidth - 10, imGuiViewport.getPosY() + 10, ImGuiCond.FirstUseEver);
        if (ImGui.begin("Input Editor",
                ImGuiWindowFlags.HorizontalScrollbar | ImGuiWindowFlags.MenuBar)) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("Edit")) {

                    if (ImGui.menuItem("Undo", "ALT-Backspace", EDITOR.canUndo())) {
                        EDITOR.undo(1);
                    }
                    if (ImGui.menuItem("Redo", "Ctrl-Y", EDITOR.canRedo())) {
                        EDITOR.redo(1);
                    }

                    ImGui.separator();

                    if (ImGui.menuItem("Copy", "Ctrl-C", EDITOR.hasSelection())) {
                        EDITOR.copy();
                    }
                    if (ImGui.menuItem("Cut", "Ctrl-X", EDITOR.hasSelection())) {
                        EDITOR.cut();
                    }
                    if (ImGui.menuItem("Delete", "Del", EDITOR.hasSelection())) {
                        EDITOR.delete();
                    }
                    if (ImGui.menuItem("Paste", "Ctrl-V", ImGui.getClipboardText() != null)) {
                        EDITOR.paste();
                    }

                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("Run")) {
                    if (ImGui.menuItem("Add Text To NARS Input")) {
                        play();
                    }
                    ImGui.endMenu();
                }

                ImGui.endMenuBar();
            }

            if(ImGui.arrowButton("Play", ImGuiMouseButton.Right)){
                play();
            }
            ImGui.sameLine(); ImGui.text("Add Text To NARS Input");

            EDITOR.render("TextEditor");
            MainGame.imGuiHover = ImGui.isAnyItemHovered() || ImGui.isWindowHovered();
            ImGui.end();
        }
    }

    private static void play() {
        String textToSave = EDITOR.getText();
        String[] lines = textToSave.split("\n");
        for (String line : lines) {
            if (line != null && !line.equals("")) {
                try {
                    MainGame.nar.addInput(line);
                } catch (Exception ex) {
                    if (Debug.DETAILED) {
                        throw new IllegalStateException("error parsing:" + line, ex);
                    }
                    System.out.println("parsing error");
                }
                if(!Settings.renderSetting.AutoRender.get()) GUI.refresh3DView();
            }
        }
    }
}
