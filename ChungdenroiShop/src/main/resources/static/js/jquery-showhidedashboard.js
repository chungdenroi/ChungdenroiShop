
    $(".dashboard").hide();
    $(".content").css("width","100%");
    $("#btndashboard").show();
    $("#btnclose").hide();

    /*hide dashboard*/
    $("#btnclose").click(function () {
    $(this).visibility = "hidden";
    $(".dashboard").hide();
    $(".content").css("width","100%");
    $("#btndashboard").show();
});
    /*show dashboard*/
    $("#btndashboard").click(function () {
    $(this).hide();
    $(".dashboard").show();
    $(".content").css("width","89.85%");
    $("#btnclose").show();

});

    /*category sort by*/
    $("#sortby").change(function () {
        if($(this).val() == "asc") {
            window.location = "asc";
        } else if ($(this).val() == "desc") {
                window.location = "desc";
        }
    });




    /*check user pass at ordersucess*/
    if($("#username_ord").val().length==0 ||  $("#password_ord").val().length==0) {
            $(".order-success-acc").hide();
        }

