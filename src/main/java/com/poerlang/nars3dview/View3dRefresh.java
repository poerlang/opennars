package com.poerlang.nars3dview;

import com.badlogic.gdx.utils.Array;
import com.poerlang.nars3dview.items.Item3d;
import com.poerlang.nars3dview.setting.Settings;
import org.opennars.entity.Concept;
import org.opennars.entity.TermLink;
import org.opennars.language.Term;
import org.opennars.storage.Bag;

import java.util.Map;
import java.util.Objects;

import static com.poerlang.nars3dview.MainGame.*;

public class View3dRefresh {

    public static int refreshCountDown = 0;
    public static Long lastCycleNum = 0L;

    public static void refresh3DView() {
        if (View3dRefresh.refreshCountDown % 2 == 0) { //refresh once in 2 frame
            if (Objects.equals(View3dRefresh.lastCycleNum, nar.cycle)) {
                return;
            }
        }
        refresh3DView(true);
    }
    static Array<Concept> level_above_instances = new Array<>();
    static Array<Concept> priority_above_instances = new Array<>();
    public static void refresh3DView(Boolean now) {
        level_above_instances.clear();
        priority_above_instances.clear();

        lastCycleNum = nar.cycle;

        Bag<Concept, Term> memoryBag = nar.memory.concepts;
        int maxConceptCount = 0;
        int maxConcept = Math.round(Settings.renderSetting.refreshPercentage.get() * memoryBag.size());
        int max3dObject = Settings.renderSetting.maxConceptIn3dView.get();
        int max3dObjectCount = 0;

        MainGame.clearInstances();

        memoryBag.getLevelAbove(Settings.renderSetting.levelThreshold.get(),level_above_instances);

        for (Concept concept : level_above_instances) {
            if(concept.getPriority() > Settings.renderSetting.priorityThreshold.get()) {
                priority_above_instances.add(concept);
            }
        }
        for (Concept a_instance : priority_above_instances) {
            if (max3dObjectCount++ > max3dObject) break;
            if (maxConceptCount++ > maxConcept) break;
            addConceptTo3DView(a_instance);
            // for (TaskLink taskLink : concept.getTaskLinkBag().getNameTable().values()) {
            for (TermLink termLink : a_instance.termLinks) {
                termLink.toLine();
                MainGame.add(termLink);
                check_for_remove.remove(termLink.uid);
                if (max3dObjectCount++ > max3dObject) {
                    break;
                }
                Concept targetConcept = nar.memory.concept(termLink.getTarget());
                if (targetConcept == null) continue;
                // if(targetConcept.isNone()){
                //    addConceptTo3DView(targetConcept);
                //    if(max3dObjectCount++ > max3dObject){
                //        return;
                //    }
                // }
                termLink.setLinePos(a_instance, targetConcept);
            }
        }
        for (Map.Entry<Long, Item3d> entry_will_remove : check_for_remove.entrySet()) {
            entry_will_remove.getValue().toNone(); // 回收那些等级或优先度被降低且不在列表中的 item 的资源
            log(entry_will_remove.getValue().toString() + "资源已经回收");
        }
        check_for_remove.clear();
    }

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
