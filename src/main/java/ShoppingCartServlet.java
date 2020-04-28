package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/cs122b/shopping-cart")
public class ShoppingCartServlet extends HttpServlet{
    /**
     * handles POST requests to store session information
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        HttpSession session = request.getSession(true);
        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        if(session.getAttribute("items") == null) {
            ArrayList<Item> temp = new ArrayList<Item>();
            session.setAttribute("items", temp);
        }

        ArrayList<Item> items = (ArrayList<Item>) session.getAttribute("items");
        for (int i = 0; i < items.size(); i++) {
            JsonObject jsonObject = new JsonObject();

            String title = items.get(i).getMovieTitle();
            String id = items.get(i).getMovieId();
            int quantity = items.get(i).getQuantity();
            int price = items.get(i).getPrice();

            jsonObject.addProperty("title", title);
            jsonObject.addProperty("quantity", quantity);
            jsonObject.addProperty("id", id);
            jsonObject.addProperty("price", price);

            jsonArray.add(jsonObject);
        }

        out.write(jsonArray.toString());
        out.close();
        response.setStatus(200);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(true);

        String title = request.getParameter("title");
        String t = "";
        String behavior = request.getParameter("behavior");

        if(session.getAttribute("items") != null) {
            ArrayList<Item> items = (ArrayList<Item>) session.getAttribute("items");
            int i;
            for( i = 0; i < items.size(); i++) {
                t = items.get(i).getMovieTitle();
                if(t.equals(title)){ break; }
            }
            if (i != items.size()) {
                if (behavior.equals("delete")){
                    items.remove(i);
                }
                if (behavior.equals("add")){
                    int quality = items.get(i).getQuantity();
                    quality++;
                    items.get(i).setQuantity(quality);
                    items.get(i).updatePrice(true);
                }
                if (behavior.equals("remove")){
                    int quality = items.get(i).getQuantity();
                    if (quality > 0){
                        quality--;
                        if(quality == 0){
                            items.remove(i);
                        }
                        else{
                            items.get(i).setQuantity(quality);
                            items.get(i).updatePrice(false);
                        }
                    }
                }
                session.setAttribute("items", items);
            }
        }

        response.getWriter().write("success");
    }
}
