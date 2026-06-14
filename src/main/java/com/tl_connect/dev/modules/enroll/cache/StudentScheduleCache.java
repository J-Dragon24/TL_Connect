package com.tl_connect.dev.modules.enroll.cache;

import java.util.HashMap;
import java.util.Map;

import com.tl_connect.dev.shared.datastructure.intervaltree.IntervalTree;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.Data;

@Data
public class StudentScheduleCache {
    private Map<Integer, IntervalTree> treeByDay = new HashMap<>();

    public void addSchedule(int dayOfWeek, ScheduleInterval interval) {
        treeByDay.computeIfAbsent(dayOfWeek, d -> new IntervalTree())
                .insert(interval);
    }

    public ScheduleInterval findConflict(int dayOfWeek, int start, int end) {
        IntervalTree tree = treeByDay.get(dayOfWeek);
        if (tree == null) return null;
        return tree.findOverlaps(start, end);
    }
}
