package main.java;

import java.util.*;

public class Parser {

    public static void main(String[] args) throws Exception{
        //StarParser sp = new StarParser();
        //sp.run();

        MainParser mp = new MainParser();
        mp.run();
        Map<String, String> fmd = mp.getFidMidDict();

        //CastParser cp = new CastParser( fmd );
        //cp.run();

    }
}
