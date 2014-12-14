//$(document).ready(function() {
//	$(".ratingstars > .glyphicon-star-empty").hover(function() {
//		if ($( this ).parent().hasClass( "hasvoted" ) == false) {
//
//			var _this = this;
//			var arvo = parseInt($(this).attr('data-number'));
//			$(this).css('color', '#C7BD2E');
//			for (var i = 1; i <= arvo; i++) {
//				console.log("lol");
//				console.log(i);
//				$(_this).siblings('*[data-number=' + i + ']').css('color', '#C7BD2E');
//			}
//			// console.log(arvo);
//		}
//	}, function() {
//		if ($( this ).parent().hasClass( "hasvoted" ) == false) {
//			$(".ratingstars .glyphicon-star-empty").css("color","black");
//			// console.log("hohohdasdo");
//		}
//	});
//
//	$(".ratingstars > .glyphicon-star-empty").click(function() {
//		if ($( this ).parent().hasClass( "hasvoted" ) == false) {
//			var foodId = $(this).attr('data-id')
//			var _this = this;
//			var votenro = parseInt($(this).attr('data-number'));
//			
//			console.log($(_this).parent());
//			$(_this).parent().addClass("hasvoted");
//			$(_this).css('color', '#C7BD2E');
//			for (var i = 1; i <= votenro; i++) {
//				console.log("lol");
//				console.log(i);
//				$(_this).siblings('*[data-number=' + i + ']').css('color', '#C7BD2E');
//			}	
//			$.post("/rate/" + foodId, { rating: parseInt($(this).attr('data-number')) }, function(data) {
//				console.log(data);
//				var tulos = data.split("/");
//				
//				if (tulos.length == 2) {
//					var ka = parseFloat(tulos[0]);
//					
//					var aanimaara = parseInt(tulos[1]);
//	
//				}
//			})
//		}
//	});
//});	