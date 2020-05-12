package main.java;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "AddToCartServlet", urlPatterns = "/cs122b/add-cart")
public class AddToCartServlet extends HttpServlet {

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        try {
            String title = request.getParameter("itemTitle");
            String id = request.getParameter("itemID");

            //System.out.println(item);
            HttpSession session = request.getSession();

            // get the previous items in a ArrayList

            ArrayList<Item> previousItems = (ArrayList<Item>) session.getAttribute("items");
            if (previousItems == null) {
                previousItems = new ArrayList<Item>();
                //session.setAttribute("items", previousItems);
            }

            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                boolean found = false;

                for(int i = 0; i < previousItems.size(); i++) {
                    //movie already exists
                    if(previousItems.get(i).getMovieId().equals(id)){
                        int quality = previousItems.get(i).getQuantity();
                        quality++;
                        previousItems.get(i).setQuantity(quality);
                        previousItems.get(i).updatePrice(true);
                        found = true;
                    }
                }
                if(!found) {
                    Item newItem = new Item(id,title);
                    previousItems.add(newItem);
                }

            }
            session.setAttribute("items", previousItems);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Successfully Added Movie!");
            responseJsonObject.addProperty("itemsadded", previousItems.toString());
            //response.getWriter().write("Success adding to cart!");

        }catch (Exception ex) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Error in adding movie.");
            //return;
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
