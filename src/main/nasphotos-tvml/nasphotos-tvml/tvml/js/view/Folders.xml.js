/* global Presenter, Mustache */

function FoldersTemplate_setSize(itemData, item) {
    if (itemData.coverWidth != -1 && itemData.coverHeight != -1) {
        if (itemData.coverWidth > itemData.coverHeight) {
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

var Template = function() {
    var folder = DataReader.currentFolder;
    LOG.log("Preparing template for folder " + folder);

    var serverData = DataReader.getFolderData(folder);
    var data = {title: folder, folders: [], photos: [], mainFolder: folder};

    if ("/" === folder) {
        data.title = "Roky";
    }

    for (var i in serverData.folders) {
        var itemData = serverData.folders[i];
        var item = {title: itemData.title,
            src: Presenter.options.BaseUrl + "files/folderThumbnails?path=" + encodeURIComponent(itemData.path),
            action: "DataReader.read('" + itemData.path + "');"};

        FoldersTemplate_setSize(itemData, item);
        data.folders.push(item);
    }

    for (var i in serverData.files) {
        var itemData = serverData.files[i];

        var item = {title: itemData.title,
            src: Presenter.options.BaseUrl + "files/thumbnails?path=" + encodeURIComponent(itemData.path),
            action: "Presenter.navigate('Photos')"};

        FoldersTemplate_setSize(itemData, item);
        data.photos.push(item);
    }

    var template = resourceLoaderLocal.loadBundleResource("templates/Folders.mustache");
    var html = Mustache.render(template, data);
    LOG.log(html);
    return html;
}
