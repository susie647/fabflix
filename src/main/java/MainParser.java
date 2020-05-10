package main.java;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainParser extends DefaultHandler {

    List<Movie> myMovies;
    List<Genre> myGenres;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;
    private Genre tempGenre;

    private String tempDirector;
    private String tempFid;
    //private String tempTitle;
    //private int tempYear;


    //@Resource(name = "jdbc/moviedb")
    //private Datasource datasource;

    public MainParser() {

        myMovies = new ArrayList<Movie>();
        myGenres = new ArrayList<Genre>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("XMLs/main.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies '" + myMovies.size() + "'.");

        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }

        System.out.println("No of Genres '" + myGenres.size() + "'.");

        Iterator<Genre> it2 = myGenres.iterator();
        while (it2.hasNext()) {
            System.out.println(it2.next().toString());
        }
    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (qName.equalsIgnoreCase("directorfilms")) {
            tempDirector = "";
        }

        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            tempMovie = new Movie();
            tempMovie.setDirector(tempDirector);
            //tempEmp.setType(attributes.getValue("type"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            myMovies.add(tempMovie);
        }

        //store information into current movie
        else if (qName.equalsIgnoreCase("dirname")) {
            tempDirector = tempVal;
        }
        else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
            tempFid = tempVal;
        }
        else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        }
        else if (qName.equalsIgnoreCase("year")) {
            if(tempVal.equals("")){
                tempMovie.setYear(-1);
            }
            else {
                tempMovie.setYear(Integer.parseInt(tempVal));
            }
        }
        else if (qName.equalsIgnoreCase("cat")) {
            //add to database genre with genreid increment
            myGenres.add(new Genre(tempVal,1));

            //add to database fid and genre

        }
        else{

        }

    }

    public static void main(String[] args) throws Exception{
//        Class.forName("com.mysql.jdbc.Driver").newInstance();
//
//        // Connect to the test database
//        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb" + "?autoReconnect=true&useSSL=false",
//                "mytestuser", "mypassword");
//
//        if (connection != null) {
//            System.out.println("Connection established!!");
//            System.out.println();
//        }



        MainParser spe = new MainParser();
        spe.runExample();
    }

}
