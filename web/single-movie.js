/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */


function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function sort2DimentionalArray(a, b) {
    if (a[1] == b[1]) {
        if (a[0] == b[0]) {
            return 0;
        }
        else {
            return (a[0] < b[0]) ? -1 : 1;
        }
    }
    else{
        return (a[1] > b[1]) ? -1 : 1;
    }
}

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p style=\"text-align:center;\">Movie Name: <b>" + resultData[0]["movie_title"] + "</b></p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows

    let rowHTML = "";
    let stars = "";
    let genres = "";

    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movie_year"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movie_director"] + "</th>";

    let genres_dict = {};
    let stars_dict = {};

    for (let i = 0; i < resultData.length; i++) {
        //
        let genre = resultData[i]["movie_genre"];
        if (genres_dict.hasOwnProperty(genre) == false){
            genres_dict[genre] = resultData[i]['movie_genre_id'];
        }

        let star = resultData[i]["star_name"];
        if(stars_dict.hasOwnProperty(star) == false) {
            stars_dict[star] = {"id": resultData[i]['star_id'], "played_count": resultData[i]["star_played_count"]};
        }
    }
    // sorting genres dict
    let sorted_genre = [];
    for(let key in genres_dict) {
        sorted_genre[sorted_genre.length] = key;
    }
    sorted_genre.sort();
    for (let i = 0; i < sorted_genre.length; i++){
        genres += ", " + '<a href="movie-list.html?genreId=' + genres_dict[sorted_genre[i]]+ "&page=1&moviesPerPage=10&sort=tara"
            + '">' + sorted_genre[i] + '</a>';
    }

    // sorting stars dict
    let sorted_star = [];
    for(let key in stars_dict) {
        sorted_star[sorted_star.length] = [key, stars_dict[key]["played_count"]];
    }
    sorted_star.sort(sort2DimentionalArray);
    for (let i = 0; i < sorted_star.length; i++){
        stars += ", " + '<a href="single-star.html?id=' + stars_dict[sorted_star[i][0]]["id"] + '">'
            + sorted_star[i][0] + "(" + sorted_star[i][1] + ")" + '</a>';
    }

    rowHTML += "<th>" + genres.substring(1) + "</th>";
    rowHTML += "<th>" + stars.substring(1) + "</th>";

    rowHTML += "<th>" + resultData[0]["movie_rating"] + "</th>";
    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);


}

////////////////////////////////////////////////////
function handleGoBackButton(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle movie list status response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        let ML_status = resultDataJson["ML_status"];
        let ML_page = resultDataJson["ML_page"];
        let ML_moviesPerPage = resultDataJson["ML_moviesPerPage"];
        let ML_sort = resultDataJson["ML_sort"];

        window.location.href= "movie-list.html?"+ML_status+"&page="+ML_page+"&moviesPerPage="+ML_moviesPerPage+"&sort="+ML_sort;

    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}
////////////////////////////////////////////////////////////

function getMovieListStatus(formClickEvent) {
    console.log("get movie list status");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formClickEvent.preventDefault();

    $.ajax(
        "cs122b/single-movie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            // data: logout_form.serialize(),
            success: handleGoBackButton
        }
    );
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('movieId');

let back = document.getElementById('back');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "cs122b/single-movie?movieId=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

back.addEventListener('click', getMovieListStatus);