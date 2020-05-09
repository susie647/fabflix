// let saleId = 13561;
let add_star_form = $("#add_star_form");

function handleDashboardData(resultData) {
    console.log("handleGenresData: populating genres table from resultData");

    // Genres body table
    let DashboardTableBody = $("#dashboard_table_body");

    /*
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["table"]+ "</th>";
        rowHTML += "<th>" + resultData[i]["attribute"] + "</th>";
        rowHTML += "<th>" + resultData[i]["type"] + "</th>";
        rowHTML += "</tr>";
        DashboardTableBody.append(rowHTML);
    }

     */

    let dashboard_dict = {};
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        let table = resultData[i]["table"];

        if (dashboard_dict.hasOwnProperty(table) === false) {
            dashboard_dict[table] = {"attribute": [resultData[i]["attribute"]], "type": [resultData[i]["type"]]};
        }
        else{
            dashboard_dict[table]["attribute"].push(resultData[i]["attribute"]);
            dashboard_dict[table]["type"].push(resultData[i]["type"]);
        }
    }

    for (let table in dashboard_dict) {
        for(let j=0; j<dashboard_dict[table]["attribute"].length; j++){
            let rowHTML = "";
            rowHTML += "<tr>";
            //first occurence of table, show table
            if(j===0){
                rowHTML += "<th>" + table+ "</th>";
            }
            //print empty for table
            else{
                rowHTML += "<th>" + " "+ "</th>";
            }

            rowHTML += "<td>" + dashboard_dict[table]["attribute"][j] + "</td>";
            rowHTML += "<td>" + dashboard_dict[table]["type"][j] + "</td>";
            rowHTML += "</tr>";
            DashboardTableBody.append(rowHTML);
        }

    }




}

function handleResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle Result");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        alert("success adding star");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        alert("failure adding star");
    }
}
function submitAddStarForm(formSubmitEvent) {
    console.log("submit add star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "cs122b/add-star", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: add_star_form.serialize(),
            success: (resultData) => handleResult(resultData)
        }
    );
}

$.ajax({
    dataType: "json", // Setting return data type
    method: "get", // Setting request method
    url: "cs122b/dashboard",
    success: (resultData) => handleDashboardData(resultData)
});

add_star_form.submit(submitAddStarForm);
