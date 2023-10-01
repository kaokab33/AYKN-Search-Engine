package com.example;

import java.io.Serializable;

public class PageRank implements Serializable {
    Double score;
    String link;
    public PageRank(Double s,String l)
    {
        score=s;
        link=l;
    }

}
