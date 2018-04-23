$(document).ready(function () {

    $(".delete-note-link").click(function (e) {
        e.preventDefault();
        var noteId = $(this).attr("href");
        deleteNote(noteId);
    });

    function deleteNote(noteId) {
        var confirmed = confirm("Are you sure you want to delete it?");
        if (confirmed == true) {
            $("#delete-note-form-id-" + noteId).submit();
        }
    }

});

