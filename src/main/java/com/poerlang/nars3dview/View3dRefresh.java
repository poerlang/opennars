package com.poerlang.nars3dview;

import com.poerlang.nars3dview.setting.Settings;
import org.opennars.entity.Concept;
import org.opennars.entity.TermLink;
import org.opennars.language.Term;
import org.opennars.storage.Bag;

import static com.poerlang.nars3dview.MainGame.nar;

public class View3dRefresh {

    public static int refreshCountDown = 0;
    public static Long lastCycleNum = 0L;
    public static void refresh3DView() {
        Bag<Concept, Term> entries = nar.memory.concepts;
        lastCycleNum = nar.cycle;
        int count = 0;
        int printNum = Math.round(Settings.renderSetting.refreshPercentage.get() * entries.size());
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
    public static void addConceptTo3DView(Concept concept) {
        concept.toPlane();
        concept.calcPos();
        concept.setSize(2);
        MainGame.add(concept);
    }
}
