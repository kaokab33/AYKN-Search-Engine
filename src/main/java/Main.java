package com.example;
//package com.example.person;
//
//import com.mongodb.*;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import java.io.EOFException;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.BrokenBarrierException;
//import org.bson.Document;
//import org.jsoup.Jsoup;
//
//public class Main {
//    static HashMap<String, org.jsoup.nodes.Document> docs;
//    static HashMap<String,Double> scores=new HashMap<>();
//    static MongoCollection<Document> myCollection;
//
//    public static void main(String[] args) throws BrokenBarrierException, IOException, InterruptedException {
//        HashMap<Map.Entry<String, String>, Link> testMap = null;
//        String connectionString = "mongodb+srv://ahmedmohamed202:HacKeR2233@cluster0.lfmmw9l.mongodb.net/test";
//        ServerApi serverApi = ServerApi.builder()
//                .version(ServerApiVersion.V1)
//                .build();
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .applyConnectionString(new ConnectionString(connectionString))
//                .serverApi(serverApi)
//                .build();
//        // Create a new client and connect to the server
//        try (MongoClient mongoClient = MongoClients.create(settings)) {
//            try {
//                MongoDatabase database = mongoClient.getDatabase("Indexer");
//               myCollection = database.getCollection("Data");
//                //WebCrawler.crawl((short) Runtime.getRuntime().availableProcessors());
//                //docs = WebCrawler.getDocs();
//                //Indexer.index(myCollection,docs);
//                System.out.println("Connected...");
//                docs = new HashMap<>();
//                getFromFile();
//
////                 Indexer.index(myCollection,docs);
//                //QueryProcessor.process("beginner", myCollection, docs,scores);
//                // System.out.println(testMap.size());
//                while(true);
//               // System.out.println("Finished...");
//
//            } catch (MongoException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    static void getFromFile() {
//        try {
//            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("links.txt"));
//            while (true) {
//                try {
//                    LinkDocument currLink = (LinkDocument) objectInputStream.readObject();
//                    docs.put(currLink.link, Jsoup.parse(currLink.document));
//                } catch (EOFException e) {
//                    break;
//                }
//            }
//            objectInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("scores.txt"));
//            while (true) {
//                try {
//                    PageRank currLink = (PageRank) objectInputStream.readObject();
//                    System.out.println(currLink.link);
//                    System.out.println(currLink.score);
//                    scores.put(currLink.link, currLink.score);
//                } catch (EOFException e) {
//                    break;
//                }
//            }
//            objectInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    public static void sendQuery(String query) throws InterruptedException {
//		QueryProcessor.process(query,myCollection, docs,scores);
//	}
//
//}
//
//
