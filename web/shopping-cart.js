function handleItemsData(resultData) {
    console.log("handleGenresData: populating genres table from resultData");

    // Genres body table
    let ItemsTableBody = $("#items_table_body");

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML += "<th>" + resultData[i]["price"] + "</th>";
        rowHTML += "<th>" + "Later" + "</th>";
        rowHTML += "</tr>";

        ItemsTableBody.append(rowHTML);
    }
}

$.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "cs122b/shopping-cart",
    success: (resultData) => handleItemsData(resultData)
});