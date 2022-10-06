package com.poerlang.nars3dview;

import com.badlogic.gdx.utils.Array;
import com.poerlang.nars3dview.items.Item3d;
import com.poerlang.nars3dview.setting.Settings;
import org.opennars.entity.Concept;
import org.opennars.entity.TermLink;
import org.opennars.language.Term;
import org.opennars.storage.Bag;

import java.util.HashMap;
import java.util.Map;

import static com.poerlang.nars3dview.MainGame.*;

public class View3dRefresh {

    public static int refreshCountDownNum = 3;
    public static int refreshCountDown = refreshCountDownNum;
    public static Long lastCycleNum = 0L;

    static Array<Concept> level_above_instances = new Array<>();
    static Array<Concept> priority_above_instances = new Array<>();
    static Array<Item3d> links = new Array<>();
    public static void refresh3DView() {
        if(refreshCountDown>0) return;
        if(nar==null) return;

        refreshCountDown = refreshCountDownNum;

        level_above_instances.clear();
        priority_above_instances.clear();

        lastCycleNum = nar.cycle;

        Bag<Concept, Term> memoryBag = nar.memory.concepts;
        int maxConceptCount = 0;
        int maxConcept = (int) Math.min(Math.round(Settings.renderSetting.refreshPercentage.get() * memoryBag.size()) , Settings.renderSetting.maxConceptIn3dView.get() * Settings.renderSetting.ConceptVsLink.get());
        int max3dObject = Settings.renderSetting.maxConceptIn3dView.get();
        int max3dObjectCount = 0;

        MainGame.clearInstances();

        memoryBag.getLevelAbove(Settings.renderSetting.levelThreshold.get(),level_above_instances);

        for (int i = 0; i < level_above_instances.size; i++) {
            Concept concept = level_above_instances.get(i);
            if(concept.getPriority() > Settings.renderSetting.priorityThreshold.get()) {
                priority_above_instances.add(concept);
            }
        }
        int conceptLen = priority_above_instances.size;
        for (int i = 0; i < priority_above_instances.size; i++) {
            Concept a_instance = priority_above_instances.get(i);
            if (max3dObjectCount >= max3dObject || maxConceptCount >= maxConcept) {
                conceptLen = i;
                break;
            }
            addConceptTo3DView(a_instance);
            max3dObjectCount++;
            maxConceptCount++;
        }
        for (int i = 0; i < conceptLen; i++) {
            Concept a_instance = priority_above_instances.get(i);
            links.clear();
            int maxLink = Math.round(Settings.renderSetting.refreshPercentage.get() * a_instance.termLinks.size());
            a_instance.termLinks.getArray(maxLink, links);
            for (int i1 = 0; i1 < links.size; i1++) {
                TermLink termLink = (TermLink) links.get(i1);
                if (max3dObjectCount >= max3dObject) {
                    break;
                }
                Concept targetConcept = nar.memory.concept(termLink.getTarget());
                if(targetConcept == null)
                    continue;
                if(targetConcept.isNone())
                    continue;

                termLink.toLine();
                check_for_remove.remove(termLink.uid);
                max3dObjectCount++;
                MainGame.add(termLink);
                termLink.setLinePos(a_instance, targetConcept);
            }
        }
        for (Map.Entry<Long, Item3d> entry_will_remove : check_for_remove.entrySet()) {
            entry_will_remove.getValue().toNone(); // 回收那些等级或优先度被降低且不在列表中的 item 的资源
        }
        check_for_remove.clear();
    }
    public static final Map<Long, Item3d> check_for_remove = new HashMap<>();
    public static void addConceptTo3DView(Item3d item3d) {
        check_for_remove.remove(item3d.uid);
        if(!item3d.isPlane()){
            item3d.toPlane();
            item3d.setSize(2);
        }
        item3d.calcPos();
        MainGame.add(item3d);
    }
}
