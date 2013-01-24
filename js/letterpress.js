var $selected;
var $board;

$(document).ready(function() {
    init();
    $selected = $("#selected");
    $selected.sortable();
});

var deselect = function() {
    var $this = $(this);
    var coordinates = $this.data("coordinates");
    var $tile = $board.find("[data-coordinates='" + coordinates +"']");
    $tile.toggle();
    $this.remove();
};

var select = function() {
    var $this = $(this);
    var $li = $("<li>");
    $li.text($this.text());
    $li.data("coordinates", $this.data("coordinates"));
    $selected.append($li);
    $li.click(deselect);
    $this.toggle();
};

var init = function() {
    $board = $('#board');
    $board.find("span").click(select);
    /*
    var alphabet = "ABCDEFGHIJKLMNOPRSTUVYÄÖ";
    for (var i = 0; i < 5; i += 1) {
	var $row = $("<div/>");
	$board.append($row);
	for (var j = 0; j < 5; j += 1) {
	    var letter = alphabet.substr(Math.random() * alphabet.length, 1);
	    $letter = $("<span>" + letter + "</span>");
	    $letter.data("coordinates", i + "," + j);
	    $letter.css("left", j*80);
	    $row.append($letter);
	    $letter.click(select);
	}
    }
    */
};
