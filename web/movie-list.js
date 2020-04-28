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

function sort2DimentionalArray(a, b) {
    if (a[1] === b[1]) {
        if (a[0] === b[0]) {
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

function handleAddToCart(resultData) {
    console.log("handleAddToCart");
    if (resultData["status"] === "success") {
        alert(resultData["message"]);
        alert(resultData["itemsadded"])
    } else {
        console.log("show error message");
        alert(resultData["message"]);
    }
}

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
            "<th><i>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?movieId=' + resultData[i]['movie_id'] + '" >'
            + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "</i></th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let movie_id = resultData[i]["movie_id"];

        let genres = "";
        let stars = "";

        let genres_dict = {};
        let stars_dict = {};


        while(i<resultData.length && resultData[i]["movie_id"] === movie_id) {
            let genre = resultData[i]["movie_genre"];
            if (genres_dict.hasOwnProperty(genre) === false){
                genres_dict[genre] = resultData[i]['movie_genre_id'];
            }

            let star = resultData[i]["star_name"];
            if(stars_dict.hasOwnProperty(star) === false) {
                stars_dict[star] = {"id": resultData[i]['star_id'], "played_count": resultData[i]["star_played_count"]};
            }
            i++;
        }
        i--;


        // sorting genres dict
        let sorted_genre = [];
        for(let key in genres_dict) {
            sorted_genre[sorted_genre.length] = key;
        }
        sorted_genre.sort();
        for (let i = 0; i < Math.min(3, sorted_genre.length); i++){
            genres += ", " + '<a href="movie-list.html?genreId=' + genres_dict[sorted_genre[i]]+ "&page=1&moviesPerPage=10&sort=tara"
                + '">' + sorted_genre[i] + '</a>';
        }

        // sorting stars dict
        let sorted_star = [];
        for(let key in stars_dict) {
            sorted_star[sorted_star.length] = [key, stars_dict[key]["played_count"]];
        }
        sorted_star.sort(sort2DimentionalArray);
        for (let i = 0; i < Math.min(3, sorted_genre.length); i++){
            stars += ", " + '<a href="single-star.html?id=' + stars_dict[sorted_star[i][0]]["id"] + '">'
                + sorted_star[i][0] + '</a>';
        }

        rowHTML += "<th>" + genres.substring(1) + "</th>";
        rowHTML += "<th>" + stars.substring(1) + "</th>";


        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        //add button
        rowHTML += "<th><button id='add'>+</button></th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * get parameters
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult

let page=parseInt(getParameterByName('page'));
let moviesPerPage =getParameterByName('moviesPerPage');
let sort=getParameterByName('sort');
let previous= document.getElementById('previous');
let next = document.getElementById('next');

let genre = "";
let movieTitle = "";
let title = "";
let year = "";
let director = "";
let star = "";

if (getParameterByName('genreId')) {
    genre = getParameterByName('genreId');
}
else if (getParameterByName('movieTitle')) {
    movieTitle = getParameterByName('movieTitle');
}
else {
    title = getParameterByName('title');
    year = getParameterByName('year');
    director = getParameterByName('director');
    star = getParameterByName('star');
}

/**
 * call java to receive json result
 */

if (getParameterByName('genreId')) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "cs122b/movie-list?genreId="+genre+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
else if (getParameterByName('movieTitle')) {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "cs122b/movie-list?movieTitle="+movieTitle+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
else {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: encodeURI("cs122b/movie-list?title="+ title +"&year="+ year + "&director="+ director + "&star="+star+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort), // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

/**
 * pagination
 */



previous.addEventListener('click',()=>{
    page -=1;
    if (page<=0){
        page = 1;
    }
    else if (getParameterByName('genreId')) {
        window.location.href= "movie-list.html?genreId="+genre+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else if (getParameterByName('movieTitle')) {
        window.location.href="movie-list.html?movieTitle="+movieTitle+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else{
        window.location.href=encodeURI("movie-list.html?title="+ title +"&year="+ year + "&director="+ director + "&star="+star+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort);
    }
});

next.addEventListener('click',()=>{
    page +=1;
    if (page===10000){
    }
    else if (getParameterByName('genreId')) {
        window.location.href= "movie-list.html?genreId="+genre+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else if (getParameterByName('movieTitle')) {
        window.location.href="movie-list.html?movieTitle="+movieTitle+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else{
        window.location.href=encodeURI("movie-list.html?title="+ title +"&year="+ year + "&director="+ director + "&star="+star+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort);
    }
});

/**
 * select sort and listings
 */

let sortings= document.getElementById('sort');
let listings = document.getElementById('listings');
sortings.addEventListener('change', ()=>{
    sort = sortings.options[sortings.selectedIndex].value;
    if (getParameterByName('genreId')) {
        window.location.href= "movie-list.html?genreId="+genre+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else if (getParameterByName('movieTitle')) {
        window.location.href="movie-list.html?movieTitle="+movieTitle+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else {
        window.location.href = encodeURI("movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&page=" + page + "&moviesPerPage=" + moviesPerPage+"&sort="+sort);
    }

});

listings.addEventListener('change', ()=>{
    moviesPerPage = listings.options[listings.selectedIndex].value;
    if (getParameterByName('genreId')) {
        window.location.href= "movie-list.html?genreId="+genre+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else if (getParameterByName('movieTitle')) {
        window.location.href="movie-list.html?movieTitle="+movieTitle+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else {
        window.location.href = encodeURI("movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&page=" + page + "&moviesPerPage=" + moviesPerPage+"&sort="+sort);
    }
});

$(document).on('click', '#add', function() {
    let title = $(this).parents("tr").find("th").eq(0).text().toString();
    let id = $(this).parents("tr").find("th").eq(0).find("a").attr('href').toString();


    $.ajax( {
        dataType: "json",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        url: "cs122b/add-cart?itemID="+id+"&itemTitle=" + title,
        //data: {"item": "abc"}.serialize(),
        success: (resultData) => handleAddToCart(resultData)
    });
});