/* global Presenter, Mustache, DataReader, LOG, resourceLoaderLocal */

function FoldersTemplate_setSize(itemData, item) {
    if (itemData.width != -1 && itemData.height != -1) {
        if (itemData.width > itemData.height) {
            item.width = Presenter.width;
            item.height = Presenter.height;
        } else {
            item.width = Presenter.height;
            item.height = Presenter.width;
        }
    } else {
        item.width = Presenter.width;
        item.height = Presenter.height;
    }
}

function FoldersTemplate_fillFolders(data, folders) {
    for (var i in folders) {
        var itemData = folders[i];
        var item = {title: itemData.title,
            src: Presenter.options.BaseUrl + "thumbnails/folders?path=" + encodeURIComponent(itemData.path),
            action: "DataReader.read('" + itemData.path + "');"};

        FoldersTemplate_setSize(itemData, item);
        data.folders.push(item);
    }
}

function FoldersTemplate_fillPictures(data, pictures) {
    for (var i in pictures) {
        var itemData = pictures[i];

        var item = {title: itemData.title,
            src: Presenter.options.BaseUrl + "thumbnails?path=" + encodeURIComponent(itemData.path),
            action: "Presenter.navigate('Photos')"};

        FoldersTemplate_setSize(itemData, item);
        data.photos.push(item);
    }
}

function FoldersTemplate_fillMovies(data, movies) {
    for (var i in movies) {
        var itemData = movies[i];

        var item = {title: itemData.title,
            src: Presenter.options.BaseUrl + "thumbnails/movies?path=" + encodeURIComponent(itemData.path),
            action: "MoviePlayer.play('" + Presenter.options.MovieUrl + encodeURIComponent(itemData.path) + "')"};

        FoldersTemplate_setSize(itemData, item);
        data.photos.push(item);
    }
}

function FoldersTemplate_fillLivePhotos(data, livePhotos) {
    for (var i in livePhotos) {
        var itemData = livePhotos[i];

        var item = {title: itemData.title,
            src: Presenter.options.BaseUrl + "thumbnails/movies?path=" + encodeURIComponent(itemData.path),
            action: "MoviePlayer.play('" + Presenter.options.MovieUrl + encodeURIComponent(itemData.path) + "')"};

        FoldersTemplate_setSize(itemData, item);
        data.photos.push(item);
    }
}

var Template = function() {
    var folder = DataReader.currentFolder;
    LOG.log("Preparing template for folder " + folder);

    var serverData = DataReader.getFolderData(folder);
    var data = {title: folder, folders: [], photos: [], mainFolder: folder};

    if ("/" === folder) {
        data.title = "Roky";
    }

    FoldersTemplate_fillFolders(data, serverData.folders);
    FoldersTemplate_fillPictures(data, serverData.files);
    FoldersTemplate_fillMovies(data, serverData.movies);
    FoldersTemplate_fillLivePhotos(data, serverData.livePhotos);

    var template = resourceLoaderLocal.loadBundleResource("templates/Folders.mustache");
    var html = Mustache.render(template, data);
    LOG.log(html);
    return html;
}
