package com.example;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;

import org.bson.Document;
import org.jsoup.Jsoup;
import org.springframework.boot.SpringApplication;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResultsApplication {
    static HashMap<String, org.jsoup.nodes.Document> docs;
    static HashMap<String, Double> scores = new HashMap<>();
    static MongoCollection<Document> myCollection;
    static ArrayList<Results> results = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ResultsApplication.class, args);

        //Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);

        String connectionString = "mongodb+srv://ahmedmohamed202:HacKeR2233@cluster0.lfmmw9l.mongodb.net/test";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                MongoDatabase database = mongoClient.getDatabase("Indexer");
                myCollection = database.getCollection("Data");
               // long startTime = System.currentTimeMillis();
                //WebCrawler.crawl((short) Runtime.getRuntime().availableProcessors());
                //long end = (System.currentTimeMillis() - startTime) / 1000;
                //System.out.println("\nTime taken in crawler = " + end + "s");
                //docs = WebCrawler.getDocs();
                //Indexer.index(myCollection, docs);
                docs = new HashMap<>();
                getFromFile();
               // results = QueryProcessor.phraseSearch1("pretend", myCollection, docs, scores);
                //System.out.println("results");

                //for(Results res : results)
//                {
//                    System.out.println(res.l);
//                    System.out.println(res.h);
//                }
//                System.out.println("Connected...");


                while (true) {
                }


            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }

    static void getFromFile() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("links.txt"));
            while (true) {
                try {
                    LinkDocument currLink = (LinkDocument) objectInputStream.readObject();
                    docs.put(currLink.link, Jsoup.parse(currLink.document));
                } catch (EOFException e) {
                    break;
                }
            }
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        docs.clear();
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("scores.txt"));
            while (true) {
                try {
                    PageRank currLink = (PageRank) objectInputStream.readObject();
                    // PageRank currLink = (PageRank) objectInputStream.readObject();
                    System.out.println(currLink.link);
                    System.out.println(currLink.score);
                    scores.put(currLink.link, currLink.score);
                } catch (EOFException e) {
                    break;
                }
            }
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Results> sendQuery(String query) throws InterruptedException {
        results = QueryProcessor.phraseSearch1(query, myCollection, docs, scores);
        return results;
    }
}