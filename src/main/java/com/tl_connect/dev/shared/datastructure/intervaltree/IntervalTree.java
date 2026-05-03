package com.tl_connect.dev.shared.datastructure.intervaltree;

public class IntervalTree {

    private IntervalNode root;

    public void insert(ScheduleInterval interval) {
        root = insert(root, interval);
    }

    private IntervalNode insert(IntervalNode node, ScheduleInterval interval) {
        if (node == null) return new IntervalNode(interval);

        if (interval.getStartPeriod() < node.getInterval().getStartPeriod()) {
            node.setLeft(insert(node.getLeft(), interval));
        } else {
            node.setRight(insert(node.getRight(), interval));
        }

        node.setMaxEnd(Math.max(node.getMaxEnd(), interval.getEndPeriod()));
        return node;
    }

    public ScheduleInterval findOverlaps(int start, int end) {
        IntervalNode current = root;
        while(current != null) {
            if(current.intersects(start, end)) {
                return current.getInterval();
            } 
            else if(current.getLeft() == null) {
                current = current.getRight();
            } else if(current.getLeft().getMaxEnd() < start) {
                current = current.getRight();
            } else {
                current = current.getLeft();
            }
        }
        return null;
    }
}
