let logout_form = $("#logout_form");

// function handleMovieResult(resultData) {
//     console.log("handleMovieResult: populating star table from resultData");
//
//     // Populate the star table
//     // Find the empty table body by id "star_table_body"
//     let movieTableBodyElement = jQuery("#movie_table_body");
//
//     // Iterate through resultData, no more than 10 entries
//     for (let i = 0; i < resultData.length; i++) {
//
//         // Concatenate the html tags with resultData jsonObject
//         let rowHTML = "";
//         rowHTML += "<tr>";
//         rowHTML +=
//             "<th><i>" +
//             // Add a link to single-star.html with id passed with GET url parameter
//             '<a href="single-movie.html?movieId=' + resultData[i]['movie_id'] + '" >'
//             + resultData[i]["movie_title"] +     // display star_name for the link text
//             '</a>' +
//             "</i></th>";
//         rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
//         rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
//
//         let movie_id = resultData[i]["movie_id"];
//
//         let temp_genre = resultData[i]["movie_genre"];
//         let genres = temp_genre;
//         let count_genre = 1;
//
//         let stars = "";
//         let count_star = 0;
//
//
//         while(resultData[i]["movie_id"] === movie_id) {
//             if(count_star <2){
//                 stars += '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
//                     + resultData[i]["star_name"] + '</a>' + ", ";
//                 count_star++;
//             }
//             else if(count_star ===2){
//                 stars += '<a href="single-star.html?id=' + resultData[i]['star_id'] + '">'
//                     + resultData[i]["star_name"] + '</a>';
//                 count_star++;
//             }
//             if(count_genre<3 && resultData[i]["movie_genre"]!==temp_genre){
//                 genres += ", " + resultData[i]["movie_genre"];
//                 temp_genre = resultData[i]["movie_genre"];
//                 count_genre++;
//             }
//             i++;
//         }
//         i--;
//
//
//
//         rowHTML += "<th>" + genres + "</th>";
//         rowHTML += "<th>" + stars + "</th>";
//
//
//         rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
//         rowHTML += "</tr>";
//
//         // Append the row created to the table body, which will refresh the page
//         movieTableBodyElement.append(rowHTML);
//     }
// }

function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#welcomeInfo").text(resultDataJson["welcomeInfo"]);
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);
}

function handleLogoutResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle logout response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("login.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

function submitLogoutForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "cs122b/main", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: logout_form.serialize(),
            success: handleLogoutResult
        }
    );
}



/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
// jQuery.ajax({
//     dataType: "json", // Setting return data type
//     method: "GET", // Setting request method
//     url: "cs122b/main", // Setting request url, which is mapped by StarsServlet in Stars.java
//     success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
// })

$.ajax("cs122b/main", {
    method: "GET",
    success: handleSessionData
});

// Bind the submit action of the form to a handler function
logout_form.submit(submitLogoutForm);