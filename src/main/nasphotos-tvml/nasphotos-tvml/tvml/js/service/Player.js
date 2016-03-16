/* global LOG */

var Player = {
    player: null,

    play: function(url) {
        if (!Player.player) {
            Player.player = new Player();
            Player.player.playlist = new Playlist();
        }

        Player.player.stop();
        Player.player.playlist.pop();

        var mediaItem = new MediaItem("video", url);
        Player.player.playlist.push(mediaItem);

        Player.player.addEventListener("stateDidChange", function(e) {
            LOG.log("Player.player.stateDidChange: " + Player.player.playbackState);
        }, false);
    }
};
