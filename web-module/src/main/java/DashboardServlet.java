package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


@WebServlet(name = "DashboardServlet", urlPatterns = "/cs122b/dashboard")
public class DashboardServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
    //private DatabaseMetaData databaseMetaData;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(true);
        response.setContentType("application/json"); // Response mime type
        JsonObject responseJsonObject = new JsonObject();

        PrintWriter out = response.getWriter();

        try {

            // the following few lines are for connection pooling
            // Obtain our environment naming context

            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null) {
//                out.println("envCtx is NULL");
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "envCtx is NULL");
                response.getWriter().write(responseJsonObject.toString());
            }

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            // the following commented lines are direct connections without pooling
            //Class.forName("org.gjt.mm.mysql.Driver");
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
            //Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            if (ds == null){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "ds is null.");
                response.getWriter().write(responseJsonObject.toString());
            }
//                out.println("ds is null.");

            Connection dbCon = ds.getConnection();

//            Connection dbCon = dataSource.getConnection();
            //databaseMetaData = dbCon.getMetaData();
            //ResultSet rs = databaseMetaData.getTables(null,null,null,new String[]{"TABLE"});

            //get all tables

            Statement statement = dbCon.createStatement();
            String query = "show tables";

            ResultSet rs = statement.executeQuery(query);



            JsonArray jsonArray = new JsonArray();
            while(rs.next()){
                //get metadata for each table
                //String table = rs.getString("TABLE_NAME");

                //ResultSet rsTable = databaseMetaData.getColumns(null,null, table, null);
                String table = rs.getString("Tables_in_moviedb");
                Statement statementTable = dbCon.createStatement();
                String queryTable = String.format("describe %s", table);
                //String queryTable = "describe creditcards";
                ResultSet rsTable = statementTable.executeQuery(queryTable);


                while(rsTable.next()){
                    JsonObject jsonObject = new JsonObject();

                    //String attribute = rsTable.getString("COLUMN_NAME");
                    //String type = rsTable.getString("DATA_TYPE");
                    String attribute = rsTable.getString("FIELD");
                    String type = rsTable.getString("TYPE");

                    jsonObject.addProperty("attribute", attribute);
                    jsonObject.addProperty("type", type);
                    jsonObject.addProperty("table",table);
                    jsonArray.add(jsonObject);
                }



                rsTable.close();
                statementTable.close();
            }
            // write JSON string to output
            out.write(jsonArray.toString());


            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbCon.close();


        } catch (Exception e) {
            e.printStackTrace();
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }

        out.close();
        //response.getWriter().write("success");
    }
}
