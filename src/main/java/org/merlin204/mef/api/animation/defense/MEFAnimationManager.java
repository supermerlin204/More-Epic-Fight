package org.merlin204.mef.api.animation.defense;


import java.util.ArrayList;
import java.util.List;


/**
 * MEF动画的核心,动画类要实现MEF的功能需自行实现
 */
public class MEFAnimationManager {
    private final List<DefenseTimePair> defenseTimePairs = new ArrayList<>();


    public List<DefenseTimePair> getDefenseTimePairsByTime(float time){
        List<DefenseTimePair> list = new ArrayList<>();
        for (DefenseTimePair defenseTimePair : this.defenseTimePairs) {
            if (defenseTimePair.isTimeIn(time)) {
                list.add(defenseTimePair);
            }
        }
        return list;
    }

    public void addDefenseTimePair(DefenseTimePair defenseTimePair){
        this.defenseTimePairs.add(defenseTimePair);
    }





}
