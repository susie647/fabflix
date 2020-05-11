package main.java;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class Parser {

    public static void main(String[] args) throws Exception{
        long startTime = System.nanoTime();

        StarParser sp = new StarParser();
        sp.run();

        MainParser mp = new MainParser();
        mp.run();
        Map<String, String> fmd = mp.getFidMidDict();

//        CastParser cp = new CastParser( fmd );
//        cp.run();

//        try {
//            FileWriter myWriter = new FileWriter("report.txt",true);
//            myWriter.write("Inconsistent data");
//            myWriter.close();
//            System.out.println("Successfully save inconsistent data into report.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }


        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        double totalTimeSec = (double) totalTime / 1_000_000_000;
        System.out.println("Total runtime: " + totalTimeSec);

    }
}
