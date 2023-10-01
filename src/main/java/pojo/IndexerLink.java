package com.example;

public class IndexerLink {
    int priority;
    int count;
    String link;
    int tf;

    public IndexerLink(int priority,int count,String link,int tf) {
        this.count = count;
        this.link = link;
        this.tf = tf;
        this.priority = priority;
    }
}
