$(document).ready(function() {
	$(".ratingstars > .glyphicon-star-empty").hover(function() {
		var _this = this;
		var arvo = parseInt($(this).attr('data-number'));
		for (var i = 1; i <= arvo; i++) {
			console.log("lol");
			$(_this).siblings('*[data-number=' + i + ']').css('color', 'yellow');
		}
		console.log(arvo);
	}, function() {
		$(".ratingstars .glyphicon-star-empty").css("color","black");
		console.log("hohohdasdo");
	});

	$(".ratingstars > .glyphicon-star-empty").click(function() {
		var foodId = $(this).attr('data-id')
		$.post("/foods/rate/" + foodId, { rating: parseInt($(this).attr('data-number')) }, function(data) {
			console.log(data);
		})
	});
});	