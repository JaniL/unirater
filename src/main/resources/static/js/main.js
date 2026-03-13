

var haeRavintolanRuokalistat = function(uri,callback) {
    $.getJSON( "/json/" + uri, function( data ) {
        callback(data);
    });
}

var haeRavintolanUri = function(nimi,callback) {
    $.get( "/json/etsiUri/" + encodeURIComponent(nimi), function( data ) {
            callback(data);
    });
}

var ohjaaFallbackiin = function() {
    location.href = "/exactum";
}

var paikanna = function() {
    if ("geolocation" in navigator) {
        /* geolocation is available */
        $("#sisalto").html("<p>Etsitään lähintä ravintolaa..</p>");
        navigator.geolocation.getCurrentPosition(function(position) {
            $.getJSON( "/json/closest/" + position.coords.longitude + "/" + position.coords.latitude, function( data ) {
                if (data && data.uri) {
                    $("#sisalto").html("<p>Ravintola löytyi!</p>");
                    location.href = "/" + data.uri;
                    return;
                }

                ohjaaFallbackiin();
            }).fail(function() {
                ohjaaFallbackiin();
            });
        }, function() {
            ohjaaFallbackiin();
        });
    } else {
        ohjaaFallbackiin();
    }
}

//$(document).ready(function() {

//});	
