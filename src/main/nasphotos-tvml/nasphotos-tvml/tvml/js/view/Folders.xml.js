/* global Presenter, Mustache */

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
            src: Presenter.options.BaseUrl + "files/folderThumbnail?path=" + encodeURIComponent(itemData.path),
            action: "DataReader.read('" + itemData.path + "');"};

        FoldersTemplate_setSize(itemData, item);
        data.folders.push(item);
    }

    for (var i in serverData.files) {
        var itemData = serverData.files[i];

        var item = {title: itemData.title,
            src: Presenter.options.BaseUrl + "files/thumbnail?path=" + encodeURIComponent(itemData.path),
            action: "Presenter.navigate('Photos')"};

        FoldersTemplate_setSize(itemData, item);
        data.photos.push(item);
    }

    var template = resourceLoaderLocal.loadBundleResource("templates/Folders.mustache");
    var html = Mustache.render(template, data);
    LOG.log(html);
    return html;
}
