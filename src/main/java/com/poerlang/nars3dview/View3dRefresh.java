package com.poerlang.nars3dview;

import com.poerlang.nars3dview.setting.Settings;
import org.opennars.entity.Concept;
import org.opennars.entity.TermLink;
import org.opennars.language.Term;
import org.opennars.storage.Bag;

import java.util.Objects;

import static com.poerlang.nars3dview.MainGame.nar;

public class View3dRefresh {

    public static int refreshCountDown = 0;
    public static Long lastCycleNum = 0L;
    public static void refresh3DView() {
        if(View3dRefresh.refreshCountDown%2==0){ //refresh once in 2 frame
            if( Objects.equals(View3dRefresh.lastCycleNum, nar.cycle) ) {
                return;
            }
        }
        refresh3DView(true);
    }
    public static void refresh3DView(Boolean now) {

        lastCycleNum = nar.cycle;

        Bag<Concept, Term> memoryBag = nar.memory.concepts;
        int count = 0;
        int maxConcept = Math.round(Settings.renderSetting.refreshPercentage.get() * memoryBag.size());
        int max3dObject = Settings.renderSetting.maxConceptIn3dView.get();
        int max3dObjectCount = 0;

        MainGame.clearInstances();

        for (Concept concept : memoryBag) {
            addConceptTo3DView(concept);
            if(max3dObjectCount++ > max3dObject){
                return;
            }
            count++;
            if(count>maxConcept){
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
                    max3dObjectCount++;
                    if(max3dObjectCount > max3dObject){
                        return;
                    }
                    Concept targetConcept = nar.memory.concept(termLink.getTarget());
                    if(targetConcept == null) continue;
                    if(targetConcept.isNone()){
                        addConceptTo3DView(targetConcept);
                        if(max3dObjectCount++ > max3dObject){
                            return;
                        }
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
