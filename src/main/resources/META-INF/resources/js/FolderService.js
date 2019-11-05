import { Cache } from "./Cache.js";

export class FolderService {
    constructor() {
        this.cache = new Cache();
    }

    contains(name) {
        return (undefined !== this.cache.get(name));
    }

    getBaseUri() {
        const changedPort = window.location.href.replace(":8000/", ":8081/");
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
        if (! this.cache.contains(name)) {
            var uri = this.getFolderFetchUri(name);

            const response = await fetch(uri);
            const payload = await response.json();

            this.cache.set(name, payload);
        }

        this.dispatchDataEvent(name);
    }

    dispatchDataEvent(name) {
        const folderDataEvent = new CustomEvent(this.getEventName(), {detail: name, bubbles: true});
        dispatchEvent(folderDataEvent);
    }

    getFolder(name) {
        if (this.cache.contains(name)) {
            return this.cache.get(name);
        }

        throw new Error("FolderService.getFolder: Cache data for " + name + " does not exist!");
    }

    getEventName() {
        return "folder-data-event";
    }
}
