package com.company;// Java program to illustrate reading data from file
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class log_processing
{
    public static void main(String[] args) throws Exception
    {
//        System.out.println("The Average TS is: " + args);
//        System.out.println("The Average TJ is: " + args[0]);


        String filename = args[0]; //
//                "/usr/local/Cellar/tomcat@8/8.5.53/libexec/webapps/cs122b-spring20-team125/log1.txt";
        File file = new File(filename);

        Scanner sc = new Scanner(file);

        ArrayList<Integer> TSs = new ArrayList<Integer>();
        ArrayList<Integer> TJs = new ArrayList<Integer>();

        String st;
        while (sc.hasNextLine())
        {
            st = sc.nextLine();
            String[] arrOfStr = st.split(" | ");
            TSs.add(Integer.parseInt(arrOfStr[0]));
//            arrOfStr[1] = " | "
            TJs.add(Integer.parseInt(arrOfStr[2]));
        }

        long totalTS = 0;
        long totalTJ = 0;

        for(int i = 0; i < TSs.size(); i++){
            totalTS += (TSs.get(i)/1000);
            totalTJ += (TJs.get(i)/1000);
        }

        double avgTS = totalTS / TSs.size()*1000;
        double avgTJ = totalTJ / TJs.size()*1000;
        System.out.println("The Average TS is: " + avgTS);
        System.out.println("The Average TJ is: " + avgTJ);
    }
} 