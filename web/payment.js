let payment_form = $("#payment_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("index.html");
    } else {
        // If login fails, the web page will display 
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "cs122b/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handleLoginResult
        }
    );
}

function handleTotalPriceData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("calculating total price");
    console.log(resultDataJson);

    // show the session information
    $("#total_price_info").text("Total Price: " + resultDataJson["total_price"]);
}

$.ajax({
    method: "GET",
    url: "cs122b/payment",
    success: (resultData) => handleTotalPriceData(resultData)
});

// Bind the submit action of the form to a handler function
payment_form.submit(submitLoginForm);

