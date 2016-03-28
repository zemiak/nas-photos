var PhotoViewer = {
    currentIndex: 0,

    showPhoto: function(index) {
        PhotoViewer.currentIndex = index;
        Presenter.navigate('Photos');
    }
};
