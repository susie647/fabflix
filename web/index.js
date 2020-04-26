let logout_form = $("#logout_form");


function genCharArray(startChar, endChar) {
    let a = [], i = startChar.charCodeAt(0), j = endChar.charCodeAt(0);
    for (; i <= j; ++i) {
        a.push(String.fromCharCode(i));
    }
    return a;
}

function handleMainPageData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    console.log("populating movie titles table");

    // Genres body table
    let movieTitlesTableBody = $("#movie_titles_table_body");

    let numbers = genCharArray('0', '9');
    let alphanumeric = numbers.concat(genCharArray('A', 'Z'));
    alphanumeric.push('*');

    for (let i = 0; i < alphanumeric.length; i++){
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th><i>" +
            // Add a link to movie-list.html with id passed with GET url parameter
            '<a href="movie-list.html?movieTitle=' + alphanumeric[i] + '" >'
            + alphanumeric[i] +     // display star_name for the link text
            '</a>' +
            "</i></th>";
        rowHTML += "</tr>";
        movieTitlesTableBody.append(rowHTML);
    }

    // show the session information
    $("#welcomeInfo").text(resultDataJson["welcomeInfo"]);
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);
}

function handleGenresData(resultData) {
    console.log("handleGenresData: populating genres table from resultData");

    // Genres body table
    let genresTableBody = $("#genres_table_body");
    // //
    // genresTableBody.append("<tr><th>Test</th></tr>");
    for (let i = 0; i < resultData.length; i++){
        let rowHTML = "";
        rowHTML += "<tr>";
        // rowHTML += "<th>" + resultData[i]["genre_name"] + "</th>";
        rowHTML +=
            "<th><i>" +
            // Add a link to movie-list.html with id passed with GET url parameter
            '<a href="movie-list.html?genreId=' + resultData[i]['genre_id'] + '" >'
            + resultData[i]["genre_name"] +     // display star_name for the link text
            '</a>' +
            "</i></th>";
        rowHTML += "</tr>";
        genresTableBody.append(rowHTML);
    }
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
    success: handleMainPageData
});

$.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "cs122b/browse",
    success: (resultData) => handleGenresData(resultData)
});

// Bind the submit action of the form to a handler function
logout_form.submit(submitLogoutForm);