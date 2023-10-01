package com.example;

import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class Ranker {
    HashMap<Map.Entry<String, String>, Link> links; //key->link , value->word
    HashMap<String, Double> scores;
    ArrayList<Results> results = new ArrayList<>();


    String[] words;
    private static final double k1 = 1.2;
    private static final double b = 0.75;
    private static final double k3 = 7.0;
    private static HashMap<String, org.jsoup.nodes.Document> docs;
    static HashMap<String, Double> pagerank = new HashMap<>();

    public String query;
    public Ranker(String[] w, HashMap<Map.Entry<String, String>, Link> l, HashMap<String, org.jsoup.nodes.Document> doc, HashMap<String, Double> s,String query) {
        links = l;
        words = w;
        docs = doc;
        scores = s;
        this.query=query;
    }

    public ArrayList<Results> rank() {
        HashMap<String, Double> idfMap = new HashMap<>();
        int numDocs = docs.size(); // total no. of docs

        for (String word : words) {
            int docsWithWord = 0;
            for (Map.Entry<String, String> temp : links.keySet()) {
                if (Objects.equals(temp.getValue(), word))
                    docsWithWord++;
            }
            System.out.println(docsWithWord);
            double idf = Math.log((numDocs - docsWithWord + 0.5) / (docsWithWord + 0.5));
            idfMap.put(word, idf);
        }
        for (Map.Entry<String, String> temp : links.keySet()) {
            int tf = links.get(temp).TF;
            double idf = idfMap.get(temp.getValue()); //link
            double words_num = docs.get(temp.getKey()).body().getAllElements().text().split("\\s+").length;
            double tags = (links.get(temp).pCount + links.get(temp).boldCount * 2 + links.get(temp).descriptionCount * 4 +
                    links.get(temp).highHeaderCount * 8 + links.get(temp).lowHeaderCount * 8 + links.get(temp).titleCount * 16) / words_num;
            if(!pagerank.containsKey(temp.getKey()))
                pagerank.put(temp.getKey(), 0.5*tf * idf + 0.2*tags);
            else {
                double x = pagerank.get(temp.getKey());
                pagerank.put(temp.getKey(), 0.5 * tf * idf + 0.3*tags + x+1);
            }
        }
        for(String link : pagerank.keySet())
        {
            double x = pagerank.get(link);
            pagerank.put(link,0.3*scores.get(link)+x);
        }
        System.out.println("Before sorting"+pagerank);
        reverseSortByValue(pagerank);
        System.out.println("After sorting"+pagerank);
        return document();

    }
    public static void reverseSortByValue(HashMap<String, Double> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list = new LinkedList<>(hm.entrySet());

        list.sort(new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Create a new LinkedHashMap to store the sorted entries
        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        pagerank=sortedMap;

    }
    public  ArrayList<Results>  phraserank(HashMap <String ,Integer> links) {
        if(links==null)
            return null;
        for(String link:links.keySet())
        {
            double newscore =1/links.get(link)+scores.get(link);
            pagerank.put(link, newscore);

        }

        System.out.println("Before sorting phrase"+pagerank);
        reverseSortByValue(pagerank);
        System.out.println("After sorting phrase"+pagerank);
        return document();
    }

    public ArrayList<Results> document() {
        query=query.replaceAll("[^a-zA-Z0-9\\s]", "");
        for(String link:pagerank.keySet())
        {
            Document document = docs.get(link);
            Elements elements = document.body().select("*");
            List<String> queryWords = Arrays.asList(query.split("\\s+"));
            List<Highlight> highlights = new ArrayList<>();
         //   PorterStemmer stemmer=new PorterStemmer();
          //   query=stemmer.stemming(query);
            for (Element element : elements) {
                String text = element.text();
                int count = countQueryWordsInParagraph(text, queryWords);
                if (count > 0) {
                    highlights.add(new Highlight(element, count));
                }
            }

            highlights.sort(Comparator.comparingInt(Highlight::getQueryWordCount).reversed());

            if (highlights.isEmpty()) {
                System.out.println("No results found.");
            } else {
                Highlight bestMatch = highlights.get(0);
                Element element = bestMatch.getElement();
                String text = element.text();
                String highlightedText = highlightQueryWordsInText(text, queryWords);
                //System.out.println(highlightedText);
                results.add(new Results(link,document.title(),highlightedText));
            }
        }
        return results;
    }

    private static int countQueryWordsInParagraph(String paragraph, List<String> queryWords) {
        int count = 0;

        for (String queryWord : queryWords) {


            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(queryWord) + "\\b", Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(paragraph);
            if (matcher.find()) {
                count++;
                System.out.println(count);
            }
            else
                System.out.println(count);
        }
        return count;
    }

    private static String highlightQueryWordsInText(String text, List<String> queryWords) {
        String[] words = text.split("\\s+");

        List<Integer> matchIndices = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            for (String queryWord : queryWords) {
                if (words[i].toLowerCase().contains(queryWord.toLowerCase())) {
                    matchIndices.add(i);
                    break;
                }
            }
        }
        if (matchIndices.isEmpty()) {
            return "";
        }
        matchIndices.sort(Comparator.naturalOrder());
        List<String> highlightedWords = new ArrayList<>();
        int start = matchIndices.get(0);
        int end = matchIndices.get(0);
        for (int i = 1; i < matchIndices.size(); i++) {
            int index = matchIndices.get(i);
            if (index - end <= 5) {
                end = index;
            } else {
                String highlightedWord = highlightWords(words, start, end,queryWords);
                highlightedWords.add(highlightedWord);
                start = index;
                end = index;
            }
        }
        String highlightedWord = highlightWords(words, start, end,queryWords);
        highlightedWords.add(highlightedWord);
        String result = String.join(" ... ", highlightedWords);

        // get the first 50 words
        String[] resultWords = result.split("\\s+");
        if (resultWords.length > 20) {
            resultWords = Arrays.copyOfRange(resultWords, 0, 20);
            resultWords[19] = resultWords[19].concat("...");
            result = String.join(" ", resultWords);
        }
        else {
            String [] rmeppp=words;

//            int number=20- resultWords.length;
//            rmeppp = Arrays.copyOfRange(words, start, number);
//            rmeppp[number-1-start] = rmeppp[number-1-start].concat("...");
//            result = result+ Arrays.toString(rmeppp);
        }

        return result;
    }
    private static String highlightWords(String[] words, int start, int end, List<String> words2) {
        StringBuilder sb = new StringBuilder();
        int matchCount = 0;

        for (int i = Math.max(0, start - 15); i <= Math.min(words.length - 1, end + 15); i++) {
            boolean isQueryWord = false;
            for (String queryWord : words2) {
                if (words[i].toLowerCase().contains(queryWord.toLowerCase())) {
                    isQueryWord = true;
                 //   sb.append("<b>").append(words[i]).append("</b>").append(" ");
                    matchCount++;
                    break;
                }
            }
            if (!isQueryWord) {
                sb.append(words[i]).append(" ");
            }
        }
        String highlightedWord = sb.toString().trim();
        if (matchCount > 1) {
            highlightedWord = "... " + highlightedWord + " ...";
        }
        return highlightedWord;
    }

    private static class Highlight {
        private final Element element;
        private final int queryWordCount;

        public Highlight(Element element, int queryWordCount) {
            this.element = element;
            this.queryWordCount = queryWordCount;
        }

        public Element getElement() {
            return element;
        }

        public int getQueryWordCount() {
            return queryWordCount;
        }
    }

}

