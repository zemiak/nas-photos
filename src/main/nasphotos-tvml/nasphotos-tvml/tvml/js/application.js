/* global App, ResourceLoaderLocal, JavascriptLogger, Presenter, navigationDocument */

var resourceLoader;
var resourceLoaderLocal;
var LOG;

App.onLaunch = function(options) {
    resourceLoaderLocal = ResourceLoaderLocal.create();
    LOG = JavascriptLogger.create();

    var javascriptFiles = [
        resourceLoaderLocal.scriptUrl("PhotoData.js"),
        resourceLoaderLocal.scriptUrl("service/Presenter.js"),
        resourceLoaderLocal.scriptUrl("service/ApplicationStorage.js"),
        resourceLoaderLocal.scriptUrl("service/DataReader.js"),
        resourceLoaderLocal.scriptUrl("lib/mustache.min.js")
    ];

    evaluateScripts(javascriptFiles, function(success) {
        if (success) {
            DataReader.clearCache();

            Presenter.options = options;
            Presenter.loader = resourceLoaderLocal;
            DataReader.init();
            DataReader.read("/"); // navigates to Folders template after reading the data
        } else {
            var errorDoc = createAlert("Evaluate Scripts Error", "Error attempting to evaluate external JavaScript files.");
            navigationDocument.presentModal(errorDoc);
        }
    });
};

var createAlert = function(title, description) {
    var alertString = '<?xml version="1.0" encoding="UTF-8" ?>'
        + '<document><alertTemplate><title>' + title + '</title>'
        + '<description>' + description + '</description>'
        + '</alertTemplate></document>';

    var parser = new DOMParser();

    var alertDoc = parser.parseFromString(alertString, "application/xml");

    return alertDoc;
};
