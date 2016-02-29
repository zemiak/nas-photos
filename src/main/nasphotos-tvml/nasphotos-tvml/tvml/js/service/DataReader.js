var DataReader = {
    VERSION_URL: null,
    DATA_URL: null,
    currentFolder: null,

    init: function() {
        DataReader.VERSION_URL = Presenter.options.BaseUrl + "files/data";
        DataReader.DATA_URL = Presenter.options.BaseUrl + "files/list";
    },

    clearCache: function() {
        PhotoData.cache = {};
        LOG.log("DataReader.clearCache: cleared");
    },

    check: function() {
        PhotoData = ApplicationStorage.get("PhotoData", PhotoData);

        LOG.log("lastCheckedDay " + PhotoData.version.lastCheckedDay + " version " + PhotoData.version.version);

        var currentDayOfMonth = new Date().getDate();
        if (PhotoData.version.lastCheckedDay === currentDayOfMonth) {
            return;
        }

        PhotoData.version.lastCheckedDay = currentDayOfMonth;
        DataReader.save();

        DataReader.requestNewVersion();
    },

    getFolderData: function(folderName) {
        return PhotoData.cache[folderName];
    },

    getVersion: function() {
        return PhotoData.version;
    },

    requestNewVersion: function() {
        LOG.log("DataReader.requestNewVersion: Refreshing photo data");

        var request = new XMLHttpRequest();
        request.responseType = "text";
        request.addEventListener("load", function(){DataReader.newVersionLoaded(request);});
        request.open("GET", DataReader.VERSION_URL);
        request.send();
    },

    newVersionLoaded: function(that) {
        var data = JSON.parse(that.responseText);
        var newVersion = data.version.version;
        var ourVersion = PhotoData.version.version;

        if (newVersion == ourVersion) {
            LOG.log("newVersionLoaded: We have the most recent version");
            return;
        }

        LOG.log("newVersionLoaded: Upgrading data to version " + newVersion + " from version " + ourVersion);

        var currentDayOfMonth = new Date().getDate();
        data.version.lastCheckedDay = currentDayOfMonth;
        if (! data.cache) {
            data.cache = {};
        }

        PhotoData = data;
        DataReader.save();

        if (data.version.motd) {
            LOG.log("newVersionLoaded: Update message: " + PhotoData.version.motd);
            DataReader.showMessage();
        }
    },

    save: function() {
        ApplicationStorage.set("PhotoData", PhotoData);
    },

    showMessage: function() {
//        var errorDoc = createAlert("Dáta o fotkách aktualizované", PhotoData.version.version + ": " + PhotoData.version.motd);
//        navigationDocument.presentModal(errorDoc);

        if (null === DataReader.currentFolder) {
            DataReader.currentFolder = "/";
        }

        Presenter.navigateReplace("Folders");
    },

    read: function(folder) {
        DataReader.currentFolder = folder;

        if (PhotoData.cache[folder]) {
            LOG.log("DataReader.read: Cache hit for folder " + folder);
            Presenter.navigateReplace("Folders");
            return;
        }

        LOG.log("DataReader.read: Reading data for folder " + folder);

        var request = new XMLHttpRequest();
        request._folder = folder;
        request.responseType = "text";
        request.timeout = 5000; // 5 seconds
        request.ontimeout = function(){DataReader.timeout(request);};
        request.onerror = function(){DataReader.timeout(request);};
        request.addEventListener("load", function(){DataReader.folderDataLoaded(request);});
        request.open("GET", DataReader.DATA_URL + "?path=" + encodeURIComponent(folder));
        request.send();
    },

    timeout: function(request) {
        var errorDoc = createAlert("Data Loading Error", "Error reading the data files.");
        navigationDocument.presentModal(errorDoc);
    },

    folderDataLoaded: function(that) {
        PhotoData.cache[that._folder] = JSON.parse(that.responseText);
        LOG.log("folderDataLoaded: Got data for folder " + that._folder);

        Presenter.navigate("Folders");
    },
};
