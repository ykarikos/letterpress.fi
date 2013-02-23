var $selected;
var $board;

$(document).ready(function() {
    init();
    $selected = $("#selected");
    $selected.sortable();
});

var deselect = function() {
    var $this = $(this);
    var tileClass = $this.attr('class');
    var id = $this.data("id");
    var $tile = $board.find("[data-id='" + id +"']");
    $tile.toggle();
    $this.remove();
    
    updateScore(tileClass, -1);
};


var updateScore = function(tileClass, diff) {
	var updateScoreNum = function(player, diff) {
    	var $score = $('.player' + player + ' .score');
    	$score.text(parseInt($score.text()) + diff);
	};
	var otherPlayer = function(player) {
		return (player % 2) + 1;
	};
	
	var turn = function() {
		return parseInt($(".arrow").parent().attr('class').substr(6));
	};

    var player = turn();
    
    if (!tileClass || otherPlayer(player) == parseInt(tileClass.substr(6))) {
    	updateScoreNum(player, diff);
    	if (tileClass) {
    		updateScoreNum(otherPlayer(player), diff * -1);
    	}
    }
};

var select = function() {
    var $this = $(this);
    var $li = $("<li>");
    $li.text($this.text());
    $li.data("id", $this.data("id"));
    var tileClass = $this.attr('class');
    
    $li.addClass(tileClass);
    $selected.append($li);
    $li.click(deselect);
    $this.toggle();
    
    updateScore(tileClass, +1);
};


var clear = function() {
	$selected.find("li").click();
};

var submit = function() {
	var word = "";
	var tiles = "";
	$selected.find("li").each(function() {
		var $this = $(this);
		word += $this.text();
		tiles += $this.data("id") + ",";
	});

	$.ajax({
		url: "/submit",
		type: "POST",
		data: {
			word: word,
			id: $("input[name='gameid']").val(),
			tiles: tiles.substring(0, tiles.length-1)
		}
	}).done(function(data) {
    	if (data == "OK") {
    		location.reload();
    	} else {
    		alert(data);
    	}
    }).fail(function() {
    	alert("Submitting " + word + " failed.");
    });
};

var namesubmit = function() {
	var playerTwo = $("input[name='playerTwoName']").val();
	var playerOne = $("span.player1").attr("title");
	if (playerTwo == playerOne) {
		alert("Can not join the game with same name as player one");
		return;
	}
	
	$.ajax({
		url: "/joingame",
		type: "POST",
		data: {
			name: playerTwo,
			id: $("input[name='gameid']").val()
		}
	}).done(function(data) {
    	if (data != "OK") {
    		alert("Could not join the game");
    	}
		location.reload();
    }).fail(function() {
    	alert("Joining to game failed.");
    });
};

var init = function() {
    $board = $('#board');
    $board.find("span").click(select);
    $('.submit').click(submit);
    $('.clear').click(clear);
    $('.namesubmit').click(namesubmit);
};
