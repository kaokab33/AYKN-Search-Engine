package com.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WebCrawler extends Thread {
    Addition addition = new Addition();
    static CyclicBarrier cyclicBarrier;
    static int size = 0;
    static int threadsFinish = 0;
    static HashMap<String, Integer>[] m = new HashMap[6];

    public static HashMap<String, Document> getDocs() {
        return docs;
    }

    static HashMap<String, Document> docs;
    static File file = new File("SeedLinks.txt");
    static File testStream = new File("TestLink.txt");
    static File testStream2 = new File("ScoreLinks.txt");
    static FileWriter fileWriter = null;
    static FileWriter fileWriter2 = null;
    static short num = -1;
    static Map<String, Integer>[] splittedMaps;
    static short noOfThreads;
    static int parting;
    static int[] arr;
    static int[] arrEnd;
    static int openedSize = 0;
    static Scanner scanner;
    static ArrayList<String> ls = new ArrayList<>();
    static int count = 0;
    static int criticalSize = 0;
    static int neededSize = 0;
    static int oversize=0;

    static HashMap<String, Double> scores = new HashMap<>();
    static HashMap<String, HashMap<String, Double>> pagerank = new HashMap<String, HashMap<String, Double>>();

    static void paging(int iterations, double dampingfactor) throws IOException {

        for (int i = 1; i < iterations; i++) {
            for (String onePage : pagerank.keySet()) {
                double sum = 0;

                for (String parents : pagerank.get(onePage).keySet()) {
                    double ranko = scores.get(parents);
                    int grandparents = pagerank.get(parents).size();
                    if (grandparents == 0)
                        sum += ranko; // seedLink it's hashMap size = 0
                    else
                        sum += (ranko / grandparents);
                    /*
                     * if (sum == 0) {
                     * System.out.println();
                     * }
                     */

                }
                if (pagerank.get(onePage).size() != 0)
                    scores.put(onePage, sum);

            }
        }

        Iterator<Map.Entry<String, Double>> iterator = scores.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Double> entry = iterator.next();
            if (!docs.containsKey(entry.getKey())) {
                iterator.remove();
                if (docs.size() == scores.size())
                    break;
            }
        }

        // if (docs.size() < scores.size()) {
        // for (String link : scores.keySet()) {
        // if (!docs.containsKey(link)) {
        // scores.remove(link);
        // // if (docs.size() == scores.size())
        // // break;
        // }
        // }
        // }
//        for (Map.Entry<String, Double> map : scores.entrySet()) {
//            fileWriter2.write(map.getKey());
//            fileWriter2.write("\n");
//        }
        // System.out.println(scores);
        FileOutputStream fos2 = new FileOutputStream("scores.txt");
        ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
        for (String link : scores.keySet()) {
            PageRank tempLink = new PageRank(scores.get(link), link);
            oos2.writeObject(tempLink);
        }
        oos2.close();
        fos2.close();
//        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("E:\\Collage\\APT\\ProjectAhmed\\APT_project\\scores.txt",true));
//
//        outputStream.close();
    }

    public WebCrawler() throws IOException {
    }

    public void run() {
        short currNum = Short.parseShort(getName());
        Document doc;
        Connection con;
        String temp;
        String theURL;
        int start = (currNum - 1) * parting;
        int end = 0;
        if (currNum != noOfThreads)
            end = currNum * parting;
        else
            end = count;
        try {
            for (int i = start; i < end; i++) {
                if (totalSizeOfMaps() >= oversize)
                    break;
                if (Thread.interrupted()) {
                    arr[currNum - 1] = i;
                    arrEnd[currNum - 1] = end;
                }
                try {
                    theURL = ls.get(i);
                    con = Jsoup.connect(theURL);
                    doc = con.timeout(3000).userAgent("Mozilla/5.0").ignoreContentType(true).get();
                    if (con.response().statusCode() != 200) {
                        continue;
                    }
                    if (doc.title() == "" || doc.title() == " ")
                        continue;

                    docs.put(theURL, doc);
                    Elements links = doc.select("a[href]");
                    System.out.printf("The link is :- %s\nThe title is :- %s\n\n", theURL, doc.title());
                    for (Element e : links) {

                        temp = e.attr("href");

                        if ((!temp.contains("https://")
                                && !temp.contains("http://"))) {
                            continue;
                        }
                        if (totalSizeOfMaps() >= oversize)
                            break;
                        if (temp.contains(".com"))
                            addition.increment(temp, 0, theURL);
                        else if (temp.contains(".net"))
                            addition.increment(temp, 1, theURL);
                        else if (temp.contains(".edu"))
                            addition.increment(temp, 2, theURL);
                        else if (temp.contains(".gov"))
                            addition.increment(temp, 3, theURL);
                        else if (temp.contains(".org"))
                            addition.increment(temp, 4, theURL);
                        else
                            addition.increment(temp, 5, theURL);
                        // docs.put(temp, doc);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
        }
        boolean finished = false;

        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        while (num < 6 && docs.size() < criticalSize) {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            if (splittedMaps[currNum - 1] != null)
                for (Map.Entry<String, Integer> entry : splittedMaps[currNum - 1].entrySet()) {
                    try {
                        temp = entry.getKey();
                        if (docs.size() >= criticalSize) {
                            finished = true;
                            break;
                        }

                        if (temp.equals("https://www.pinterest.com/taste_of_home/") ||
                                temp.equals("https://www.pinterest.com/foxbroadcasting/"))
                            continue;
                        con = Jsoup.connect(temp);
                        doc = con.timeout(3000).userAgent("Mozilla/5.0").get();
                        if (con.response().statusCode() != 200) {
                            continue;
                        }
                        docs.put(temp, doc);
                        System.out.println("Size is " + docs.size());
                        if (docs.size() >= criticalSize) {
                            finished = true;
                            break;
                        }
                        if (totalSizeOfMaps() >= criticalSize)
                            continue;
                        String Mylink = temp;

                        Elements links = doc.select("a[href]");
                        for (Element e : links) {
                            if (docs.size() >= criticalSize) {
                                finished = true;
                                break;
                            }
                            temp = e.attr("href");
                            if (temp.contains("/docs")
                                    || temp.contains("/logs")
                                    || (!temp.contains("https://")
                                    && !temp.contains("http://"))) {
                                continue;
                            }
                            addition.incrementLink(temp, num, Mylink);
                        }
                    } catch (Exception ignore) {
                    }
                }

            try {
                cyclicBarrier.await();
                System.out.println("out of barrier" + currNum);
                threadsFinish++;
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
            if (finished)
                break;
        }

    }

    static class Addition {
        public synchronized void increment(String s, int num, String ref) {
            if (totalSizeOfMaps() >= criticalSize)
                return;
            if (m[num].containsKey(s))
                m[num].merge(s, 1, Integer::sum);
            else {
                size++;
                m[num].put(s, 1);
            }
            if (!pagerank.containsKey(s)) {
                HashMap<String, Double> tempo = new HashMap<String, Double>();
                tempo.put(ref, scores.get(ref));
                pagerank.put(s, tempo);
                // pagerank.get(s).put("yara", 1);
            } else {
                pagerank.get(s).put(ref, 1.0);
            }
            scores.put(s, 1.0);
            // if (!scores.containsKey(ref))
            // scores.put(ref, 1.0);//// Ahmed ???????
            if (!ls.contains(s))
                ls.add(s);
        }

        public synchronized void incrementLink(String s, int num, String ref) throws IOException {
            if (docs.size() >= criticalSize)
                return;
            if (!pagerank.containsKey(s)) {
                HashMap<String, Double> tempo = new HashMap<String, Double>();
                tempo.put(ref, 1.0);
                pagerank.put(s, tempo);
                // pagerank.get(s).put("yara", 1);
            } else {
                for (String y : pagerank.get(s).keySet())
                    System.out.println(y);

                System.out.println(pagerank.get(s).size());
                pagerank.get(s).put(ref, 1.0);
                System.out.println(pagerank.get(s).size());
            }
            scores.put(s, 1.0);
            // if (!scores.containsKey(ref))
            // scores.put(ref, 1.0);//// Ahmed ???????
            if (!ls.contains(s))
                ls.add(s);
            if (m[num].containsKey(s))
                m[num].merge(s, 1, Integer::sum);
            else {
                size++;
                m[num].put(s, 1);
                Connection connection = Jsoup.connect(s);
                Document doc = connection.timeout(3000).get();
                if (connection.response().statusCode() != 200)
                    return;
                if (doc.title() == "" || doc.title() == " ")
                    return;
                docs.put(s, doc);

                System.out.printf("The link is :- %s\nThe title is :- %s\n\n", s, doc.title());
            }
        }
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static void crawl(short args) throws IOException, InterruptedException, BrokenBarrierException {
        System.setProperty("http.proxyhost", "127.0.0.1");// to hide ip data to avoid hackers
        System.setProperty("http.proxyport", "3128");
        System.out.println("Starting Web Crawling");
        fileWriter = new FileWriter(testStream);
        fileWriter2 = new FileWriter(testStream2);
        docs = new HashMap<>();
        criticalSize = 1000;
        oversize=criticalSize+2000;
        scanner = new Scanner(file);
        m[0] = new HashMap<>();
        m[1] = new HashMap<>();
        m[2] = new HashMap<>();
        m[3] = new HashMap<>();
        m[4] = new HashMap<>();
        m[5] = new HashMap<>();
        while (scanner.hasNextLine()) {
            ls.add(scanner.nextLine());
        }
        scanner.close();
        for (String seedLink : ls) {
            pagerank.put(seedLink, new HashMap<String, Double>());
            scores.put(seedLink, 4.0);
        }

        System.out.println("Reading Links Finished");
        count = ls.size();
        System.out.println("Size is  " + count);
        noOfThreads = args;

        if (noOfThreads == 0 || noOfThreads > count)
            noOfThreads = (short) count;
        arr = new int[noOfThreads];
        arrEnd = new int[noOfThreads];
        cyclicBarrier = new CyclicBarrier(noOfThreads + 1);
        parting = count / noOfThreads;
        WebCrawler[] th = new WebCrawler[noOfThreads];
        for (int i = 0; i < noOfThreads; i++) {
            th[i] = new WebCrawler();
            th[i].setName(String.valueOf(i + 1));
        }
        for (int i = 0; i < noOfThreads; i++) {
            th[i].start();
        }
        cyclicBarrier.await();
        // openedSize = m[0].size() + m[1].size() + m[2].size() + m[3].size() +
        // m[4].size() + m[5].size();
        System.out.println("Working on fetching");
        splittedMaps = new HashMap[noOfThreads];

        for (int i = 0; i < noOfThreads; i++) {
            splittedMaps[i] = new HashMap<>();
        }
        while (docs.size() < criticalSize && num < 6) {
            num++;
            if (num == 6) {
                num = -1;
                continue;
            }
            // if (num == 0)
            // break;
            if (m[num].size() != 0) {
                splittedMaps = splitMap(m[num], noOfThreads);
            }
            cyclicBarrier.await();
            System.out.println("Size is  " + size);
            cyclicBarrier.await();
        }
        System.out.println("Finished fetching");
        // cyclicBarrier.await();
        for (int i = 0; i < noOfThreads; i++) {
            th[i].join();
        }
        System.out.println("Size is  " + size);
        System.out.println("Crawling finished");

        FileOutputStream fos = new FileOutputStream("links.txt");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (Map.Entry<String, Document> doc : docs.entrySet()) {
            LinkDocument tempLink = new LinkDocument();
            tempLink.link = doc.getKey();
            tempLink.document = doc.getValue().html();
            oos.writeObject(tempLink);
        }
        oos.close();
        fos.close();
//        ObjectOutputStream outputStream = new ObjectOutputStream(
//                new FileOutputStream("E:\\Collage\\APT\\ProjectAhmed\\APT_project\\links.txt", true)); // Appendf
////        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("links.txt"));
//
//        outputStream.close();
//        for (Map.Entry<String, Document> map : docs.entrySet()) {
//            fileWriter.write(map.getKey());
//            fileWriter.write("\n");
//        }

        // String URL;
        // Connection con;
        // Document doc;
        // for (int i = 0; i < 6; i++) {
        // Set<Map.Entry<String, Integer>> set = hm[i].entrySet();
        // for (Map.Entry<String, Integer> element : set) {
        // if (!docs.containsKey(element.getKey())) {
        // try {
        // URL = element.getKey();
        // con = Jsoup.connect(URL).timeout(3000).userAgent("Mozilla/5.0");
        // doc = con.get();
        // if (con.response().statusCode() != 200)
        // continue;
        // docs.put(URL, doc);
        // System.out.printf("The link is :- %s\nThe title is :- %s\n\n", URL,
        // doc.title());
        // } catch (Exception ignore) {
        // }
        //
        // } else {
        // System.out.printf("The link is :- %s\nThe title is :- %s\n\n",
        // element.getKey(), docs.get(element.getKey())) ;
        // }
        //
        // }
        // }
        paging(2, .85);
        fileWriter.close();
        fileWriter2.close();

    }

    static Map<String, Integer>[] splitMap(Map<String, Integer> map, int numMaps) {
        int size = map.size();
        int chunkSize = (int) Math.ceil((double) size / numMaps);
        Map<String, Integer>[] splitMaps = new Map[numMaps];

        int i = 0;
        int j = 0;
        for (String key : map.keySet()) {
            if (splitMaps[i] == null) {
                splitMaps[i] = new HashMap<>();
            }
            splitMaps[i].put(key, map.get(key));
            j++;
            if (j >= chunkSize && i < numMaps - 1) {
                i++;
                j = 0;
            }
        }

        return splitMaps;
    }

    static private int totalSizeOfMaps() {
        return m[0].size() + m[1].size() + m[2].size() + m[3].size() + m[4].size() + m[5].size();
    }
}