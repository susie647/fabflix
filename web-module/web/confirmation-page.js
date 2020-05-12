// let saleId = 13561;

function handleItemsData(resultData) {
    console.log("handleGenresData: populating genres table from resultData");

    // Genres body table
    let ItemsTableBody = $("#items_table_body");

    let i = 0;
    for (i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td id='saleID'>" + resultData[i]["saleID"]+ "</td>";
        // saleId++;
        rowHTML += "<td id='title'>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td id='quantity'>" + resultData[i]["quantity"] + "</td>";
        rowHTML += "</tr>";
        ItemsTableBody.append(rowHTML);
    }

    $("#totalPrice").text("Total Price: " + resultData[--i]["total"]);
}



$.ajax({
    dataType: "json", // Setting return data type
    method: "POST", // Setting request method
    url: "cs122b/update",
    success: (resultData) => handleItemsData(resultData)
});