function handleItemsData(resultData) {
    console.log("handleGenresData: populating genres table from resultData");

    // Genres body table
    let ItemsTableBody = $("#items_table_body");

    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td id='title'>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td id='quantity'>" + resultData[i]["quantity"] + "</td>";
        rowHTML += "<td id='price'>" + resultData[i]["price"] + "</td>";
        rowHTML += "<td><button id='add'>Increase Quantity</button><button id='remove'>Decrease Quantity</button></td>";
        // rowHTML += "<td></td>";
        rowHTML += "<td><button id='delete'>Delete</button></td>";
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


$(document).on('click', '#delete', function() {
    let title = $(this).parents("tr").find("td").eq(0).text().toString();

    $.ajax("cs122b/shopping-cart?title="+title+"&behavior="+"delete", {
        method: "POST",
        success: function () {
            window.location.reload();
        }
    });
});

$(document).on('click', '#add', function() {
    let title = $(this).parents("tr").find("td").eq(0).text().toString();

    $.ajax("cs122b/shopping-cart?title="+title+"&behavior="+"add", {
        method: "POST",
        success: function () {
            window.location.reload();
        }
    });
});

$(document).on('click', '#remove', function() {
    let title = $(this).parents("tr").find("td").eq(0).text().toString();

    $.ajax("cs122b/shopping-cart?title="+title+"&behavior="+"remove", {
        method: "POST",
        success: function () {
            window.location.reload();
        }
    });
});
