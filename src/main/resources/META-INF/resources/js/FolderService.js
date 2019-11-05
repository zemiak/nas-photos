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

    getBaseUri() {
        const changedPort = window.location.href.replace(":8000/", ":8080/");
        return changedPort.split('#')[0];
    }

    getBaseDownloadUri() {
        return this.getBaseUri() + "backend/download/?path=";
    }

    getBaseDownloadThumbnailUri() {
        return this.getBaseUri() + "backend/thumbnail/?path=";
    }

    getFolderFetchUri(name) {
        return this.getBaseUri() + "backend/browse/?path=" + name;
    }

    async fetchFolder(name) {
        if (!(name in this.cache)) {
            var uri = this.getFolderFetchUri(name);

            const response = await fetch(uri);
            const payload = await response.json();

            this.cache[name] = payload;
        }

        this.dispatchDataEvent(name);
    }

    dispatchDataEvent(name) {
        const folderDataEvent = new CustomEvent(this.getEventName(), {detail: name, bubbles: true});
        dispatchEvent(folderDataEvent);
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
