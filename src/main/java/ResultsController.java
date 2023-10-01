package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;

@Controller

public class ResultsController {
    ArrayList<Results> results = new ArrayList<>();
    @GetMapping("/main") //Main screen
    public static String showMain() {
        return "main";
    }
    @RequestMapping("/search")
    public String search(@RequestParam("q") String query, Model model) throws InterruptedException { //Remain in the same page after another search
        // Do something with the search query
        if(query == null ||query.equals(""))
        {
            return "empty";
        }
        model.addAttribute("query", query);
        System.out.println(query);
        //results.clear();
        long startTime = System.currentTimeMillis();
        results=ResultsApplication.sendQuery(query);
        model.addAttribute("persons", results);
        //System.out.println(results);
//        for(Results res : results)
//        {
//            System.out.println("l:"+res.l);
//            System.out.println("h:"+res.h);
//            System.out.println("p:"+res.p);
//        }
        long end = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("\nTime taken = " + end + " s");

        return "results";
    }
//    @RequestMapping("/main")
//    public String firstSearch(@RequestParam("q") String query, Model model) throws InterruptedException {
//        // Do something with the search query
//        model.addAttribute("query", query);
//        System.out.println(query);
//        results=ResultsApplication.sendQuery(query);
//        model.addAttribute("persons", results);
//        return "persons";
//    }
}