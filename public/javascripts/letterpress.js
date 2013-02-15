var $selected;
var $board;

$(document).ready(function() {
    init();
    $selected = $("#selected");
    $selected.sortable();
});

var deselect = function() {
    var $this = $(this);
    var id = $this.data("id");
    var $tile = $board.find("[data-id='" + id +"']");
    $tile.toggle();
    $this.remove();
};

var select = function() {
    var $this = $(this);
    var $li = $("<li>");
    $li.text($this.text());
    $li.data("id", $this.data("id"));
    $li.addClass($this.attr('class'));
    $selected.append($li);
    $li.click(deselect);
    $this.toggle();
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
		data: {
			word: word,
			id: $("input[name='gameid']").val(),
			tiles: tiles.substring(0, tiles.length-1)
		}
	}).done(function(data) {
    	if (data == "OK") {
    		location.reload();
    	} else if (data == "ENDED") {
    		alert("The game has ended.");
    	} else if (data == "PLAYED") {
    		alert(word + " has already been played.");
    	} else {
    		alert(word + " is not a valid word.");
    	}
    }).fail(function() {
    	alert("Submitting " + word + " failed.");
    });
};

var init = function() {
    $board = $('#board');
    $board.find("span").click(select);
    $('.submit').click(submit);
    $('.clear').click(clear);
};
