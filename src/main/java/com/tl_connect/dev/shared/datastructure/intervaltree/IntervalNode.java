package com.tl_connect.dev.shared.datastructure.intervaltree;

import lombok.Data;

@Data
public class IntervalNode {
    private ScheduleInterval interval;
    private int maxEnd;
    private IntervalNode left;
    private IntervalNode right;

    public IntervalNode(ScheduleInterval interval) {
        this.interval = interval;
        this.maxEnd = interval.getEndPeriod();
    }

    public boolean intersects(int start, int end) {
        System.out.println("interval: " + this.interval.getStartPeriod() + " to " + this.interval.getEndPeriod());
        System.out.println("start: " + start + " to " + end);
        return this.interval.getStartPeriod() <= end
                && this.interval.getEndPeriod() >= start;
    }
}
