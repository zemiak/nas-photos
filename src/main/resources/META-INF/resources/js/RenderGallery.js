import { FolderService } from "./FolderService.js"
import { Spinner } from "./Spinner.js";
import { TemplateService } from "./TemplateService.js";

export class RenderGallery extends HTMLElement {
    constructor() {
        super();
        this.gallery = document.querySelector("#gallery");

        this.service = new FolderService();
        this.spinner = new Spinner();
        this.template = new TemplateService(this.service.getBaseDownloadUri(), this.service.getBaseDownloadThumbnailUri(), this.service.getBaseVideoStreamingUrl());
        this.currentFolder = "";
    }

    connectedCallback() {
        addEventListener(FolderService.EVENT_NAME, e => this.onFolderData(e));
        addEventListener("hashchange", e => this.onHashChange(e));
        this.render();
    }

    render() {
        const folder = this.getFolder();
        if (! this.service.contains(folder)) {
            this.spinner.show();
            this.service.fetchFolder(folder);
        } else {
            this.onFolderData({detail: folder});
        }
    }

    getFolder() {
        var folder = window.location.hash;
        if ("" == folder || "#" == folder) {
            folder = "";
        }

        if (folder.startsWith("#")) {
            folder = folder.substr(1);
        }

        return folder;
    }

    onFolderData(e) {
        this.spinner.hide();

        const folder = e.detail;
        const data = this.service.getFolder(folder);

        this.template.renderGallery(data, this.gallery);
    }

    onHashChange(e) {
        this.render();
    }
}

customElements.define("render-gallery", RenderGallery);
