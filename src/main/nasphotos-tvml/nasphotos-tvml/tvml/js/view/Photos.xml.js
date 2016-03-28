/* global Presenter, Mustache */

function PhotosTemplate_process(itemData, data, i, count) {
    var item = {title: itemData.title,
    src: Presenter.options.BaseUrl + "files?path=" + encodeURIComponent(itemData.path),
    action: "Presenter.navigate('Photos')", width: itemData.width, height: itemData.height,
        index: i, count: count};

    data.photos.push(item);
}

var Template = function() {
    var folder = DataReader.currentFolder;
    var startFrom = PhotoViewer.currentIndex;
    LOG.log("Preparing template for photo folder " + folder + " and index " + startFrom);

    var serverData = DataReader.getFolderData(folder);
    var data = {title: folder, folders: [], photos: [], mainFolder: folder};
    var count = serverData.files.length, i;

    for (i = startFrom; i < count; i++) {
        PhotosTemplate_process(serverData.files[i], data, i, count);
    }

    for (i = 0; i < startFrom; i++) {
        PhotosTemplate_process(serverData.files[i], data, i, count);
    }

    var template = resourceLoaderLocal.loadBundleResource("templates/Photos.mustache");
    var html = Mustache.render(template, data);
    LOG.log(html);
    return html;
}
