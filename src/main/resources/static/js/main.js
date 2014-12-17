

var haeRavintolanRuokalistat = function(uri,callback) {
    $.getJSON( "/json/" + uri, function( data ) {
        callback(data);
    });
}

var haeRavintolanUri = function(nimi,callback) {
    $.get( "/json/etsiUri/" + nimi, function( data ) {
            callback(data);
    });
}

var paikanna = function() {
    if ("geolocation" in navigator) {
        /* geolocation is available */
        $("#sisalto").html("<p>Etsitään lähintä ravintolaa..</p>");
        navigator.geolocation.getCurrentPosition(function(position) {
            $.getJSON( "http://messi.hyyravintolat.fi/publicapi/restaurant/locate/" + position.coords.longitude + "/" + position.coords.latitude, function( data ) {
                $("#sisalto").html("<p>Ravintola löytyi!</p>");
                haeRavintolanUri(data.data.name, function(res) {
                    location.href = "http://unirater.herokuapp.com/" + res;
                });
            });
        });
    } else {
        location.href = "http://unirater.herokuapp.com/ylioppilasaukio";
    }
}

//$(document).ready(function() {

//});	