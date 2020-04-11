/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?movieId=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let movie_id = resultData[i]["movie_id"];

        let temp_genre = resultData[i]["movie_genre"];
        let genres = temp_genre;
        let count_genre = 1;

        let stars = "";
        let count_star = 0;


        while(resultData[i]["movie_id"] === movie_id) {
            if(count_star <2){
                stars += '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
                    + resultData[i]["star_name"] + '</a>' + ", ";
                count_star++;
            }
            else if(count_star ===2){
                stars += '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
                    + resultData[i]["star_name"] + '</a>';
                count_star++;
            }
            if(count_genre<3 && resultData[i]["movie_genre"]!==temp_genre){
                genres += ", " + resultData[i]["movie_genre"];
                temp_genre = resultData[i]["movie_genre"];
                count_genre++;
            }
            i++;
        }
        i--;



        rowHTML += "<th>" + genres + "</th>";
        rowHTML += "<th>" + stars + "</th>";


        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "cs122b/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});