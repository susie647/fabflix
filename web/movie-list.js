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
        // alert(resultData["itemsadded"])
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
    let movie_info_dict = {};


    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        let movie_id = resultData[i]["movie_id"];

        if (movie_info_dict.hasOwnProperty(movie_id) === false) {
            movie_info_dict[movie_id] = {"movie_title": resultData[i]["movie_title"],
                "movie_year": resultData[i]["movie_year"], "movie_director": resultData[i]["movie_director"],
                "movie_rating": resultData[i]["movie_rating"], "genres_name": [resultData[i]["movie_genre"]],
                "genres_id": [resultData[i]["movie_genre_id"]], "stars_name": [resultData[i]["star_name"]],
                "stars_id": [resultData[i]["star_id"]], "stars_played": [resultData[i]["star_played_count"]]};
        }
        else{
            movie_info_dict[movie_id]["genres_name"].push(resultData[i]["movie_genre"]);
            movie_info_dict[movie_id]["genres_id"].push(resultData[i]["movie_genre_id"]);
            movie_info_dict[movie_id]["stars_name"].push(resultData[i]["star_name"]);
            movie_info_dict[movie_id]["stars_id"].push(resultData[i]["star_id"]);
            movie_info_dict[movie_id]["stars_played"].push(resultData[i]["star_played_count"]);
        }
    }

    for (let key in movie_info_dict) {
        let genres_dict = {};
        let stars_dict = {};

        let genres_array = movie_info_dict[key]["genres_name"];
        for (let j = 0; j < genres_array.length; j++) {
            let genre = genres_array[j];
            if (genres_dict.hasOwnProperty(genre) === false) {
                genres_dict[genre] = movie_info_dict[key]["genres_id"][j];
            }
        }

        let stars_array = movie_info_dict[key]["stars_name"];
        for (let j = 0; j < stars_array.length; j++) {
            let star = movie_info_dict[key]["stars_name"][j];
            if (stars_dict.hasOwnProperty(star) === false) {
                stars_dict[star] = {"id": movie_info_dict[key]['stars_id'][j],
                    "played_count": movie_info_dict[key]['stars_played'][j]};
            }
        }

        let genres = "";
        let sorted_genre = [];
        for(let g in genres_dict) {
            sorted_genre[sorted_genre.length] = g;
        }
        sorted_genre.sort();
        for (let j = 0; j < Math.min(3, sorted_genre.length); j++){
            genres += ", " + '<a href="movie-list.html?genreId=' + genres_dict[sorted_genre[j]]+ "&page=1&moviesPerPage=10&sort=tara"
                + '">' + sorted_genre[j] + '</a>';
        }

        let stars = "";
        let sorted_star = [];
        for(let s in stars_dict) {
            sorted_star[sorted_star.length] = [s, stars_dict[s]["played_count"]];
        }
        sorted_star.sort(sort2DimentionalArray);
        for (let j = 0; j < Math.min(3, sorted_genre.length); j++){
            stars += ", " + '<a href="single-star.html?id=' + stars_dict[sorted_star[j][0]]["id"] + '">'
                + sorted_star[j][0] + '</a>';
        }

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th><i>" +
            '<a href="single-movie.html?movieId=' + key + '" >'
            + movie_info_dict[key]["movie_title"] +
            '</a>' +
            "</i></th>";
        rowHTML += "<th>" + movie_info_dict[key]["movie_year"] + "</th>";
        rowHTML += "<th>" + movie_info_dict[key]["movie_director"] + "</th>";
        rowHTML += "<th>" + genres.substring(1) + "</th>";
        rowHTML += "<th>" + stars.substring(1) + "</th>";
        rowHTML += "<th>" + movie_info_dict[key]["movie_rating"] + "</th>";
        //add button
        rowHTML += "<th><button id='add'>+</button></th>";
        rowHTML += "</tr>";

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
//let next = document.getElementById('next');

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

/*next.addEventListener('click',()=>{
    page +=1;
    if (getParameterByName('genreId')) {
        window.location.href= "movie-list.html?genreId="+genre+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else if (getParameterByName('movieTitle')) {
        window.location.href="movie-list.html?movieTitle="+movieTitle+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort;
    }
    else{
        window.location.href=encodeURI("movie-list.html?title="+ title +"&year="+ year + "&director="+ director + "&star="+star+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort);
    }
});

 */

function checkPage(resultData){
    if(resultData.length===0){
        page-=1;
    }
    else {
        if (getParameterByName('genreId')) {
            window.location.href = "movie-list.html?genreId=" + genre + "&page=" + page + "&moviesPerPage=" + moviesPerPage + "&sort=" + sort;
        } else if (getParameterByName('movieTitle')) {
            window.location.href = "movie-list.html?movieTitle=" + movieTitle + "&page=" + page + "&moviesPerPage=" + moviesPerPage + "&sort=" + sort;
        } else {
            window.location.href = encodeURI("movie-list.html?title=" + title + "&year=" + year + "&director=" + director + "&star=" + star + "&page=" + page + "&moviesPerPage=" + moviesPerPage + "&sort=" + sort);
        }
    }
}

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
    let id = $(this).parents("tr").find("th").eq(0).find("a").attr('href').toString().substring(26);


    $.ajax( {
        dataType: "json",
        method: "POST",
        // Serialize the login form to the data sent by POST request
        url: "cs122b/add-cart?itemID="+id+"&itemTitle=" + title,
        //data: {"item": "abc"}.serialize(),
        success: (resultData) => handleAddToCart(resultData)
    });
});


$(document).on('click', '#next', function() {
    page+=1;
    if (getParameterByName('genreId')) {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "cs122b/movie-list?genreId="+genre+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => checkPage(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
    else if (getParameterByName('movieTitle')) {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "cs122b/movie-list?movieTitle="+movieTitle+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => checkPage(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
    else {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: encodeURI("cs122b/movie-list?title="+ title +"&year="+ year + "&director="+ director + "&star="+star+"&page="+page+"&moviesPerPage="+moviesPerPage+"&sort="+sort), // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => checkPage(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
});