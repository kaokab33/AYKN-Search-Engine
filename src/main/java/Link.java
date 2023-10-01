package com.example;

import org.jsoup.nodes.Document;

public class Link {
    Document URL;
    int totalScore = 0;
    int titleCount = 0;
    int pCount = 0;
    int descriptionCount = 0;
    int boldCount = 0;
    int highHeaderCount = 0;
    int lowHeaderCount = 0;
    int TF = 0;

    Link addLink(Link another) {
        this.totalScore += another.totalScore;
        this.titleCount += another.titleCount;
        this.pCount += another.pCount;
        this.descriptionCount += another.descriptionCount;
        this.boldCount += another.boldCount;
        this.highHeaderCount += another.highHeaderCount;
        this.lowHeaderCount += another.lowHeaderCount;
        return this;
    }

    Link addVals(int totalScore, int titleCount, int pCount, int descriptionCount, int boldCount, int highHeaderCount,
            int lowHeaderCount) {
        this.totalScore += totalScore;
        this.titleCount += titleCount;
        this.pCount += pCount;
        this.descriptionCount += descriptionCount;
        this.boldCount += boldCount;
        this.highHeaderCount += highHeaderCount;
        this.lowHeaderCount += lowHeaderCount;
        return this;
    }
}
