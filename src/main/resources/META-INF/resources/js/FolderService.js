export class FolderService {
    constructor() {
        this.clear();
    }

    clear() {
        this.cache = {};
    }

    contains(name) {
        return (name in this.cache);
    }

    getBaseDownloadUri() {
        return window.location.href.replace(":8000/", ":8080/") + "backend/download/?path=";
    }

    getFolderFetchUri(name) {
        return window.location.href.replace(":8000/", ":8080/") + "backend/browse/?path=" + name;
    }

    async fetchFolder(name) {
        if (!(name in this.cache)) {
            var uri = this.getFolderFetchUri(name);
            console.log("Fetching", uri);

            const response = await fetch(uri);
            const payload = await response.json();

            this.cache[name] = payload;
        }

        this.dispatchDataEvent(name);
    }

    dispatchDataEvent(name) {
        // const folderDataEvent = new CustomEvent(this.getEventName(), {detail: name, bubbles: true});
        // dispatchEvent(folderDataEvent);  // @TODO: !!! does NOT work
        window._gallery.onFolderData({detail: name});  // ugly as fuck
    }

    getFolder(name) {
        if (name in this.cache) {
            return this.cache[name];
        }

        throw new Error("FolderService.getFolder: Cache data for " + name + " does not exist!");
    }

    getEventName() {
        return "folder-data-event";
    }
}
