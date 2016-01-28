var NasphotosSwipe = {
    show: function(pictureIndex) {
        var pswpElement = document.querySelectorAll('.pswp')[0];
        var options = {
            index: pictureIndex
        };

        window.gallery = new PhotoSwipe( pswpElement, PhotoSwipeUI_Default, window.photoswipe_items, options);
        window.gallery.init();
    }
};