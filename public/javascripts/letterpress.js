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
	$selected.find("li").each(function() {
		word += $(this).text();
	});

	$.ajax("/submit/" + word)
    .done(function(data) {
    	if (data == "OK") {
    		location.reload();
    	} else {
    		alert(data + " is not a valid word");
    	}
    });
};

var init = function() {
    $board = $('#board');
    $board.find("span").click(select);
    $('.submit').click(submit);
    $('.clear').click(clear);
};
