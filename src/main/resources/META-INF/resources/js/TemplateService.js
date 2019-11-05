export class TemplateService {
    constructor(url, thumbnailUrl) {
        this.imageDownloadBaseUrl = url;
        this.imageDownloadThumbnailBaseUrl = thumbnailUrl;
    }

    render(data) {
        var html = "";

        html = html + this.header() + "\n";

        this.folders = false;
        data.items.forEach(item => {html = html + this.element(item)});

        html = html + this.footer() + "\n";

        if (! this.folders) {
            this.dispatchGalleryEvent();
        }

        return html;
    }

    header() {


        return `
    <bread-crumbs></bread-crumbs>
    <ul class="auto-grid">
        `;
    }

    footer() {
        return `    </ul>
`;
    }

    element(item) {
        if ("folder" === item.type) {
            return this.renderFolder(item);
        }

        return this.renderPicture(item);
    }

    renderPicture(item) {
        const imageUrl = this.imageDownloadBaseUrl + item.path;
        const imageThumbnailUrl = this.imageDownloadThumbnailBaseUrl + item.path;

        return `
        <li>
            <a href="${imageUrl}" data-glightbox="gallery1" class="glightbox">
                <img src="${imageThumbnailUrl}" alt="${item.title}" width="90%">
            </a>
        </li>
`;
    }

    renderFolder(item) {
        this.folders = true;

        return `
        <li>
            <a href="#${item.path}">
                <img src="/img/folder.png" width="90%" height="90%" alt="${item.title}">
            </a>
            <span>${item.title}</span>
        </li>
`;
    }

    getEventName() {
        return "render-lightbox-event";
    }

    dispatchGalleryEvent() {
        const folderDataEvent = new CustomEvent(this.getEventName(), {detail: {}, bubbles: true});
        dispatchEvent(folderDataEvent);
        console.log("dispatchGalleryEvent ran");
    }
}
