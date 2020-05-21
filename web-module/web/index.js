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
            '<a href="movie-list.html?movieTitle=' + alphanumeric[i] + '&page=1&moviesPerPage=10&sort=tara" >'
            + alphanumeric[i] +     // display star_name for the link text
            '</a>' +
            "</i></th>";
        rowHTML += "</tr>";
        movieTitlesTableBody.append(rowHTML);
    }

    // show the session information
    $("#welcomeInfo").text(resultDataJson["welcomeInfo"]);

    if(resultDataJson["admin"] == "true"){
        let dashboard = $("#adminDashboard");
        let HTML = "";
        HTML += '<a href = \"dashboard.html\"><button>Go to Dashboard</button></a>';
        dashboard.append(HTML);
    }
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
            '<a href="movie-list.html?genreId=' + resultData[i]['genre_id'] + '&page=1&moviesPerPage=10&sort=tara" >'
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


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"


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


/*
 * CS 122B Project 4. Autocomplete Example.
 *
 * This Javascript code uses this library: https://github.com/devbridge/jQuery-Autocomplete
 *
 * This example implements the basic features of the autocomplete search, features that are
 *   not implemented are mostly marked as "TODO" in the codebase as a suggestion of how to implement them.
 *
 * To read this code, start from the line "$('#autocomplete').autocomplete" and follow the callback functions.
 *
 */


/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "cs122b/title-suggestion?query=" + escape(query),
        "success": function(data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function(errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["heroID"])
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button


