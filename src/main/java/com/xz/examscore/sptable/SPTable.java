package com.xz.sptable;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * (description)
 * created at 16/05/27
 *
 * @author yiding_he
 */
public class SPTable {

    private List<Document> scores = new ArrayList<>();

    private List<Item> quests = new ArrayList<>();

    private List<Item> students = new ArrayList<>();

    public void addScore(Document score) {
        String questId = score.getString("quest");
        String studentId = score.getString("student");

        scores.add(score);
        quests.stream().filter(quest -> quest.getId().equals(questId)).findFirst().ifPresent(Item::incre);
        students.stream().filter(student -> student.getId().equals(studentId)).findFirst().ifPresent(Item::incre);
    }

    //////////////////////////////////////////////////////////////

    private static class Item implements Comparable<Item> {

        private String id;

        private int value;

        public Item(String id, int value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void incre() {
            this.value += 1;
        }

        @Override
        public int compareTo(Item o) {
            return this.value > o.value ? 1 : (this.value < o.value ? -1 : 0);
        }
    }
}
