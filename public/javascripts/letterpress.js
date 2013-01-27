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

var init = function() {
    $board = $('#board');
    $board.find("span").click(select);
};
