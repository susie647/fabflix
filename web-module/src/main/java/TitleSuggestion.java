package main.java;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet(name = "TitleSuggestion", urlPatterns = "/cs122b/title-suggestion")

public class TitleSuggestion extends HttpServlet {
//    private static final long serialVersionUID = 3L;

    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
//    public static HashMap<Integer, String> superHeroMap = new HashMap<>();
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public TitleSuggestion() {
        super();
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();
            HttpSession session = request.getSession();

            //get suggestion list from session
            HashMap<String,ArrayList<String>> suggestion_title = (HashMap<String,ArrayList<String>> ) session.getAttribute("suggestion_title");
            HashMap<String,ArrayList<String>> suggestion_id = (HashMap<String,ArrayList<String>> ) session.getAttribute("suggestion_id");

            // get the query string from parameter
            String title = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (title == null || title.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            title = title.trim();

            if (title.compareTo("") > 0) {
                ArrayList<String> title_list = suggestion_title.get(title);
                ArrayList<String> id_list = suggestion_id.get(title);
                //title is searched before and suggestion is cached
                if(title_list!=null && id_list!=null){

                    for (int i=0; i<title_list.size(); i++){
                        jsonArray.add(generateJsonObject(id_list.get(i), title_list.get(i)));
                    }
                    response.getWriter().write(jsonArray.toString());
                    return;

                }

            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

            String query = "SELECT * FROM movies WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) limit 10";
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            if (title.compareTo("") > 0) {
                //split search into array "good a" -> [good,a] -> '+good* +a*'
                String [] titleArr = title.split(" ");

                String ftTitle = "";
                for (String word: titleArr){
                    ftTitle += "+"+word+"* ";
                }
                statement.setString(1, ftTitle);
            }

            ResultSet resultSet = statement.executeQuery();

            ArrayList<String> titleList = new ArrayList<String>();
            ArrayList<String> idList = new ArrayList<String>();
            while(resultSet.next()){
                String movieTitle = resultSet.getString("title");
                String id = resultSet.getString("id");
                jsonArray.add(generateJsonObject(id, movieTitle));
                titleList.add(movieTitle);
                idList.add(id);
            }
            suggestion_title.put(title, titleList);
            suggestion_id.put(title, idList);

            session.setAttribute("suggestion_title", suggestion_title);
            session.setAttribute("suggestion_id", suggestion_id);


//            for (Integer id : superHeroMap.keySet()) {
//                String heroName = superHeroMap.get(id);
//                if (heroName.toLowerCase().contains(query.toLowerCase())) {
//                    jsonArray.add(generateJsonObject(id, heroName));
//                }
//            }
            connection.close();
            statement.close();
            resultSet.close();

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieId, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
