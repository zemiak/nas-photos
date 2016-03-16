/* global LOG */

var MoviePlayer = {
    player: null,

    play: function(url) {
        LOG.log("MoviePlayer.play(): begin");

        if (!MoviePlayer.player) {
            LOG.log("MoviePlayer.play(): creating");
            MoviePlayer.player = new Player();
            MoviePlayer.player.playlist = new Playlist();
        }

        LOG.log("MoviePlayer.play(): stopping");
        MoviePlayer.player.stop();
        MoviePlayer.player.playlist.pop();

        LOG.log("MoviePlayer.play(): setting up video");
        var mediaItem = new MediaItem("video", url);
        MoviePlayer.player.playlist.push(mediaItem);

        MoviePlayer.player.addEventListener("stateDidChange", function(e) {
            LOG.log("Player.player.stateDidChange: " + MoviePlayer.player.playbackState);
        }, false);

        LOG.log("MoviePlayer.play(): playing");
        MoviePlayer.player.play();
    }
};
