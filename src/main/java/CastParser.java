package main.java;

import com.sun.javafx.collections.MappingChange;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.plaf.nimbus.State;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class CastParser extends DefaultHandler {

    List<Movie> myMovies;
    List<Star> myStars;
    Map<String, String> FidMidDict;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;

    private String tempDirector;
    private String tempFid;
    //private String tempTitle;
    //private int tempYear;

    private FileWriter myWriter;

    public CastParser( Map<String, String> fmd ) {

        myMovies = new ArrayList<Movie>();
        myStars = new ArrayList<Star>();
        FidMidDict = fmd;
//        FidMidDict = new HashMap<String, String>();
//        for (Map.Entry<String, String> entry : fmd.entrySet()) {
//            FidMidDict.put(entry.getKey(), entry.getValue());
//        }
    }

    public void run() {
        try {
            parseDocument();
            //open report writer
            myWriter = new FileWriter("report.txt",true);
            myWriter.write("\nInconsistent data for parsing cast124.xml and adding to database:\n");

            updateDB();

            myWriter.close();
            System.out.println("Successfully save inconsistent data into report.");

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("stanford-movies/casts124.xml", this);

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

        System.out.println("No of Stars '" + myStars.size() + "'.");

        Iterator<Star> it2 = myStars.iterator();
        while (it2.hasNext()) {
            System.out.println(it2.next().toString());
        }
    }

    private void updateDB() throws Exception {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();

        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

//        String find_movie_s = "select * from movies as m where m.id = ?";
        String find_star_s = "select * from stars as s where s.name = ?";
//        String add_s_in_m_s = "insert into stars_in_movies values (?, ?)";

//        PreparedStatement find_movie = connection.prepareStatement(find_movie_s);
        PreparedStatement find_star = connection.prepareStatement(find_star_s);
//        PreparedStatement add_s_in_m = connection.prepareStatement(add_s_in_m_s);
        FileWriter mySMWriter = new FileWriter("newStarsInMovies.txt");
        String newSMLine = "";


        for(int i=0; i < myMovies.size(); i++){
            // check whether the space is empty
            if(myMovies.get(i).getId() == null || myMovies.get(i).getId().equals("") ){
                //System.out.println("movie id is empty; skip");

                System.out.println("Star in Movie not added, NO MOVIE ID. " + myMovies.get(i).toString());
                try {
                    myWriter.write("\nStar in Movie not added, NO MOVIE ID. " + myMovies.get(i).toString());
                }catch (IOException e) { e.printStackTrace(); }

                continue;
            }

            // check whether the movie exists
//            find_movie.setString(1, myMovies.get(i).getId());
//            ResultSet fmrs = find_movie.executeQuery();
            // if exists
            if(FidMidDict.containsKey(myMovies.get(i).getId())){
                String movieId = FidMidDict.get(myMovies.get(i).getId());
                ArrayList<String> movieStars = myMovies.get(i).getStars();
                for(int j=0; j<movieStars.size(); j++){
                    if(movieStars.get(j) == null || movieStars.get(j).equals("")){
                        //System.out.println("star stage name is empty; skip");

                        System.out.println("Star in Movie not added, NO STAGE NAME. " + myMovies.get(i).toString());
                        try {
                            myWriter.write("\nStar in Movie not added, NO STAGE NAME. " + myMovies.get(i).toString());
                        }catch (IOException e) { e.printStackTrace(); }

                        continue;
                    }
                    find_star.setString(1, movieStars.get(j));
                    ResultSet fsrs = find_star.executeQuery();
                    if(fsrs.next()){
                        // both movie and star exists
                        newSMLine += fsrs.getString("id");
                        newSMLine += ",";
                        newSMLine += movieId;
                        newSMLine += "\n";

                        mySMWriter.write(newSMLine);
                        newSMLine = "";
//                        add_s_in_m.setString(1, fsrs.getString("id"));
//                        add_s_in_m.setString(2, movieId);
//                        add_s_in_m.executeUpdate();
//                        System.out.println(movieStars.get(j) + " in " + myMovies.get(i).getTitle());
                    }
                    else{
                        //System.out.println(movieStars.get(j) + "does not exist in stars table");
                        System.out.println("Star in Movie not added, " + movieStars.get(j) + "does not exist in stars table. "+ myMovies.get(i).toString());
                        try {
                            myWriter.write("\nStar in Movie not added, " + movieStars.get(j) + "does not exist in stars table. " + myMovies.get(i).toString());
                        }catch (IOException e) { e.printStackTrace(); }
                    }
                }
            }
            else{
                //System.out.println(myMovies.get(i).getTitle() + "does not exist in movies table");
                System.out.println("Star in Movie not added, " + myMovies.get(i).getTitle() + "does not exist in movies table."+ myMovies.get(i).toString());
                try {
                    myWriter.write("\nStar in Movie not added, " + myMovies.get(i).getTitle() + "does not exist in movies table." + myMovies.get(i).toString());
                }catch (IOException e) { e.printStackTrace(); }
            }
        }
        mySMWriter.close();

        String updateSM_s = "LOAD DATA LOCAL INFILE 'newStarsInMovies.txt' INTO TABLE stars_in_movies FIELDS TERMINATED BY ',';";
        Statement updateSM = connection.createStatement();
        ResultSet rs = updateSM.executeQuery(updateSM_s);

        updateSM.close();
        rs.close();
//        find_movie.close();
        find_star.close();
//        add_s_in_m.close();
        connection.close();
    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (qName.equalsIgnoreCase("dirfilms")) {
            tempDirector = "";
        }

        if (qName.equalsIgnoreCase("filmc")) {
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

        if (qName.equalsIgnoreCase("filmc")) {
            //add it to the list
            tempMovie.setYear(-1);
            myMovies.add(tempMovie);
        }

        //store information into current movie
        else if (qName.equalsIgnoreCase("is")) {
            tempDirector = tempVal;
        }
        else if (qName.equalsIgnoreCase("f")) {
            tempMovie.setId(tempVal);
            tempFid = tempVal;
        }
        else if (qName.equalsIgnoreCase("t")) {
            tempMovie.setTitle(tempVal);
        }
        else if (qName.equalsIgnoreCase("a")) {
            //add to database star
            myStars.add(new Star(tempVal,-1));
            //add to database stars in movies
            tempMovie.addStars(tempVal);
        }
        else{

        }

    }

//    public static void main(String[] args) throws Exception{
//
//        CastParser spe = new CastParser( fmd );
//        spe.run();
//    }

}
