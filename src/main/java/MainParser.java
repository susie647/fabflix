package main.java;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;


public class MainParser extends DefaultHandler {

    List<Movie> myMovies;
    HashSet<String> myGenres;

    Map<String, Integer> existingGenres;
    Map<String, String> genreTable;

    private String tempVal;

    //to maintain context
    private Movie tempMovie;
    private Genre tempGenre;

    private String tempDirector;
    private String tempFid;




    public MainParser() {

        myMovies = new ArrayList<Movie>();
        myGenres = new HashSet<String>();
        existingGenres = new HashMap<String, Integer>();
        genreTable = new HashMap<String, String>(){{
            put("susp", "thriller");
            put("cnr", "cops and robbers");
            put("dram", "drama");
            put("west", "western");
            put("myst", "mystery");
            put("s.f.", "science fiction");
            put("advt", "adventure");
            put("horr", "horror");
            put("romt", "romantic");
            put("comd", "comedy");
            put("musc", "musical");
            put("docu", "documentary");
            put("porn", "pornography, including soft");
            put("noir", "black");
            put("biop", "biographical Picture");
            put("tv", "TV show");
            put("tvs", "TV series");
            put("tvm", "TV miniseries");
        }};
    }

    public void run() {
        try {
            parseDocument();
//            printData();
            updateDB();
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
            sp.parse("stanford-movies/mains243.xml", this);

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

        Iterator<String> it2 = myGenres.iterator();
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


        /**
         * retrieve all genres and store in map, find max genreid
         */

        Statement retrieve = connection.createStatement();
        String query = "SELECT * from genres as g";
        ResultSet rsGenre = retrieve.executeQuery(query);

        int maxGenreId = 0;
        while(rsGenre.next()){
            int genreId = rsGenre.getInt("id");
            String genreName = rsGenre.getString("name").toLowerCase();
            existingGenres.put(genreName, genreId);
            if(genreId>maxGenreId){
                maxGenreId = genreId;
            }
        }
        System.out.println("Maxgenreid:"+maxGenreId);
        System.out.println(existingGenres.toString());

        retrieve.close();
        rsGenre.close();


        /**
         * add to genres table
         */
        String add_genre = "insert into genres values (?, ?)";
        PreparedStatement updateGenre = connection.prepareStatement(add_genre);

        Iterator<String> itG = myGenres.iterator();
        //iterate through mygenres set
        while (itG.hasNext()) {
            //convert abbreviation to actual genre name
            String abbrev = itG.next().toString();
            //System.out.println(abbrev);
            String genreName = genreTable.get(abbrev);
            //System.out.println(genreName);

            //check if genre name already exists, add to database if key does not exist
            if(genreName!=null && !existingGenres.containsKey(genreName)) {
                maxGenreId++;
                updateGenre.setInt(1, maxGenreId);
                updateGenre.setString(2, genreName);
                updateGenre.executeUpdate();
                existingGenres.put(genreName, maxGenreId);
            }
        }
        updateGenre.close();


        /**
         * add to movies table and genres_in_movies table
         */
        String find_movie_s = "select * from movies as m where m.title = ? and m.year = ? and m.director = ?";
        PreparedStatement find_movie = connection.prepareStatement(find_movie_s);

        String add_movie = "insert into movies values (?, ?, ?, ?)";
        PreparedStatement update = connection.prepareStatement(add_movie);

        String add_genres_in_movies = "insert into genres_in_movies values(?,?)";
        PreparedStatement updateGIM = connection.prepareStatement(add_genres_in_movies);

        for(int i=0; i < myMovies.size(); i++){

            if(myMovies.get(i).getId().equals("") || myMovies.get(i).getId() == null){
                System.out.println("NO FID" + myMovies.get(i).toString());
                continue;
            }
            if(myMovies.get(i).getYear() == -1){
                System.out.println("NO YEAR/WRONG TYPE" + myMovies.get(i).toString());
                continue;
            }
            if(myMovies.get(i).getDirector().equals("") || myMovies.get(i).getDirector() == null){
                System.out.println("NO DIRECTOR" + myMovies.get(i).toString());
                continue;
            }
            if(myMovies.get(i).getTitle().equals("") || myMovies.get(i).getTitle() == null){
                System.out.println("NO TITLE" + myMovies.get(i).toString());
                continue;
            }

            // CHECK DUPLICATE MOVIES
            find_movie.setString(1, myMovies.get(i).getTitle());
            find_movie.setInt(2, myMovies.get(i).getYear());
            find_movie.setString(3, myMovies.get(i).getDirector());
            ResultSet fmrs = find_movie.executeQuery();
            String movieId = myMovies.get(i).getId();
            if(fmrs.next()){
                movieId = fmrs.getString("id");
            }
            else {
                update.setString(1, movieId);
                update.setString(2, myMovies.get(i).getTitle());
                update.setInt(3, myMovies.get(i).getYear());
                update.setString(4, myMovies.get(i).getDirector());
                update.executeUpdate();
            }

            //add each genre in this movie into genres_in_movies
            ArrayList<String> tempGenres = myMovies.get(i).getGenres();

            if(tempGenres!=null && !tempGenres.isEmpty()){
                for(int j=0; j<tempGenres.size(); j++){
                    String genre = genreTable.get(tempGenres.get(j));
                    if(genre.equals("") || genre == null){
                        System.out.println("genre is empty; skip");
                        continue;
                    }
                    updateGIM.setInt(1, existingGenres.get(genre));
                    updateGIM.setString(2, movieId);
                    updateGIM.executeUpdate();
                }
            }

        }
        updateGIM.close();
        update.close();
        find_movie.close();
        connection.close();

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
                try {
                    tempMovie.setYear(Integer.parseInt(tempVal));
                }
                catch (Exception e){
                    tempMovie.setYear(-1);
                }
            }
        }
        else if (qName.equalsIgnoreCase("cat")) {
            //add to database genre with genreid increment
            myGenres.add(tempVal.toLowerCase());
            tempMovie.addGenres(tempVal.toLowerCase());
            //add to database fid and genre

        }
        else{

        }

    }

    public static void main(String[] args) throws Exception{


        MainParser spe = new MainParser();
        spe.run();
    }

}
