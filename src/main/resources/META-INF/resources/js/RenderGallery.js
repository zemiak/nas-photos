import { FolderService } from "./FolderService.js"
import { SpinnerService } from "./SpinnerService.js";
import { TemplateService } from "./TemplateService.js";

export class RenderGallery extends HTMLElement {
    constructor() {
        super();
        this.gallery = document.querySelector("#gallery");

        this.service = new FolderService();
        this.spinner = new SpinnerService();
        this.template = new TemplateService(this.service.getBaseDownloadUri());
        this.currentFolder = "";

        window._gallery = this;
    }

    connectedCallback() {
        this.addEventListener(this.service.getEventName(), e => this.onFolderData(e));
        this.render();
    }

    render(folder) {
        folder = this.saveFolderToStorage(folder);
        console.log("render: Folder " + folder);
        if (! this.service.contains(folder)) {
            console.log("render: Going to fetch " + folder);
            this.spinner.show();
            this.service.fetchFolder(folder);
        } else {
            console.log("render: Data in cache, going to render directly " + folder);
            this.onFolderData({detail: folder});
        }
    }

    saveFolderToStorage(folder) {
        if (! folder) {
            folder = this.currentFolder;
            if (! folder) {
                folder = "";
            }
        }

        this.currentFolder = folder;
        return folder;
    }

    onFolderData(e) {
        this.spinner.hide();

        const folder = e.detail;
        const data = this.service.getFolder(folder);

        this.gallery.innerHTML = this.template.render(folder, data);
    }
}

customElements.define("render-gallery", RenderGallery);
