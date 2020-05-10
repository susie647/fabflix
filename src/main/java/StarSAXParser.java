package main.java;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class StarSAXParser extends DefaultHandler{

    List<Star> myStars;

    private String tempVal;

    //to maintain context
    private Star tempStar;

    public StarSAXParser() {
        myStars = new ArrayList<Star>();
    }

    public void runExample() {
        try {
            parseDocument();
            printData();
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
            sp.parse("XMLs/actor.xml", this);

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

        String add_star = "insert into stars values (?, ?, ?)";
        PreparedStatement update = connection.prepareStatement(add_star);

        for(int i=0; i < myStars.size(); i++){
//            newIDNum++;
            String newStarId = String.format("%s%d", maxStarId.substring(0, 2), ++newIDNum);
            update.setString(1, newStarId);
            update.setString(2, myStars.get(i).getName());
            if(myStars.get(i).getDob() == -1){
                update.setString(3, null);
            }
            else {
                update.setInt(3, myStars.get(i).getDob());
            }
            update.executeUpdate();
        }

        statement.close();
        rsMax.close();
        update.close();
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
                tempStar.setDob(Integer.parseInt(tempVal));
            }
        }else{

        }

    }

    public static void main(String[] args) throws Exception {

        StarSAXParser spe = new StarSAXParser();
        spe.runExample();
    }


}
