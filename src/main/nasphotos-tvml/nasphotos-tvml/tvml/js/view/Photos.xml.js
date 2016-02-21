/* global Presenter, Mustache */

var Template = function() {
    var folder = DataReader.currentFolder;
    LOG.log("Preparing template for photo folder " + folder);

    var serverData = DataReader.getFolderData(folder);
    var data = {title: folder, folders: [], photos: [], mainFolder: folder};

    for (var i in serverData.files) {
        var itemData = serverData.files[i];

        var item = {title: itemData.title,
        src: Presenter.options.BaseUrl + "files/download?path=" + encodeURIComponent(itemData.path),
            action: "Presenter.navigate('Photos')", width: itemData.width, height: itemData.height,
            index: i, count: serverData.files.length};

        data.photos.push(item);
    }

    var template = resourceLoaderLocal.loadBundleResource("templates/Photos.mustache");
    var html = Mustache.render(template, data);
    LOG.log(html);
    return html;
}
