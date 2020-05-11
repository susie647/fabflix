package main.java;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;


public class StarParser extends DefaultHandler{

    List<Star> myStars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public StarParser() {
        myStars = new ArrayList<Star>();
    }

    public void run() {
        try {
            parseDocument();
            //open report writer
//            myWriter = new FileWriter("report.txt",true);
//            myWriter.write("\nInconsistent data for parsing actor63.xml and adding to database:\n");

            updateDB();


        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<Star> getMyStars() {
        return myStars;
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("stanford-movies/actors63.xml", this);

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

        System.out.println("No of Employees '" + myStars.size() + "'.");

        Iterator<Star> it = myStars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    private void updateDB() throws Exception {
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();

        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        Statement statement = connection.createStatement();
        String maxId = "SELECT max(s.id) as maxStarId from stars as s";
        ResultSet rsMax = statement.executeQuery(maxId);

        //retrieve and increment id
        String maxStarId = "";
        if(rsMax.next()){
            maxStarId = rsMax.getString("maxStarId");
        }
        int newIDNum = Integer.parseInt(maxStarId.substring(2));

        FileWriter myWriter = new FileWriter("newStars.txt");
        String newLine = "";

        FileWriter reportWriter = new FileWriter("report.txt",true);
        reportWriter.write("\nInconsistent data for parsing actor63.xml and adding to database:\n");


        for(int i=0; i < myStars.size(); i++){
            String newStarId = String.format("%s%d", maxStarId.substring(0, 2), ++newIDNum);
            if(myStars.get(i).getName() == null || myStars.get(i).getName().equals("") ){
                System.out.println("Star not added, NO STAGE NAME. " + myStars.get(i).toString());
                try {
                    reportWriter.write("\nStar not added, NO STAGE NAME. " + myStars.get(i).toString());
                }catch (IOException e) { e.printStackTrace(); }
                continue;
            }

            newLine += newStarId;
            newLine += ",";
            newLine += myStars.get(i).getName();
            newLine += ",";

            if(myStars.get(i).getDob() == -1){
                newLine += "\\N\n";
            }
            else {
                newLine += myStars.get(i).getDob();
                newLine += "\n";
            }
            myWriter.write(newLine);
            newLine = "";
        }

        myWriter.close();

        String load = "LOAD DATA LOCAL INFILE 'newStars.txt' INTO TABLE stars FIELDS TERMINATED BY ',';";
        Statement statement2 = connection.createStatement();
        ResultSet rs = statement2.executeQuery(load);

        reportWriter.close();
        System.out.println("Successfully save inconsistent data into report.");

        statement.close();
        rsMax.close();
        statement2.close();
        rs.close();
//        update.close();
        connection.close();

    }



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            tempStar = new Star();
            //tempEmp.setType(attributes.getValue("type"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            myStars.add(tempStar);

        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            if(tempVal.equals("")){
                tempStar.setDob(-1);
            }
            else {
                try {
                    tempStar.setDob(Integer.parseInt(tempVal));
                }
                catch (Exception e){
                    tempStar.setDob(-1);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        StarParser spe = new StarParser();
        spe.run();
    }


}
