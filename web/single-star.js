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
    let starInfoElement = jQuery("#star_info");

    //change year of birth to N/A if null
    let year = resultData[0]["star_dob"];
    if(year === null){
        year = "N/A"
    }

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p style=\"text-align:center;\">Star Name: <b>" + resultData[0]["star_name"] + "</b></p>" +
        "<p style=\"text-align:center;\">Year Of Birth: " + year + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    let movies_dict = {};
    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let movie = resultData[i]["movie_title"];
        if (movies_dict.hasOwnProperty(movie) == false) {
            movies_dict[movie] = {"id": resultData[i]["movie_id"],"year": resultData[i]["movie_year"]};
        }
    }
    let sorted_movie = [];
    for(let key in movies_dict) {
        sorted_movie[sorted_movie.length] = [key, movies_dict[key]["year"]];
    }
    sorted_movie.sort(sort2DimentionalArray);
    for (let i = 0; i < sorted_movie.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th><i>" + '<a href="single-movie.html?movieId=' + movies_dict[sorted_movie[i][0]]['id'] + '">'
            + sorted_movie[i][0] + "(" + sorted_movie[i][1] + ")" +      // display star_name for the link text
            '</a>' + "</i></th>";
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

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

function getMovieListStatus(formClickEvent) {
    console.log("get movie list status");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formClickEvent.preventDefault();

    $.ajax(
        "cs122b/single-star", {
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
let starId = getParameterByName('id');

let back = document.getElementById('back');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "cs122b/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

back.addEventListener('click', getMovieListStatus);