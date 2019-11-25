import { html, render } from "./lib/lit-html.js";

export class TemplateService {
    static EVENT_NAME = "render-lightbox-event"
    
    constructor(url, thumbnailUrl, videoUrl) {
        this.imageDownloadBaseUrl = url;
        this.imageDownloadThumbnailBaseUrl = thumbnailUrl;
        this.videoStreamingUrl = videoUrl;
    }

    renderGallery(data, element) {
        var plainHtml = "";

        plainHtml = plainHtml + this.header() + "\n";

        this.folders = false;
        data.items.forEach(item => {plainHtml = plainHtml + this.element(item)});

        plainHtml = plainHtml + this.footer() + "\n";

        if (! this.folders) {
            this.dispatchGalleryEvent();
        }

        // const template = html(plainHtml);
        // console.log(template);
        // render(template, element);

        element.innerHTML = plainHtml;
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

        if ("video" === item.type) {
            return this.renderVideo(item);
        }

        return this.renderPicture(item);
    }

    renderPicture(item) {
        const imageUrl = this.imageDownloadBaseUrl + item.path;
        const imageThumbnailUrl = this.imageDownloadThumbnailBaseUrl + item.path;

        return `
        <li>
            <a href="${imageUrl}" data-glightbox="type: image" class="glightbox" data-gallery="gallery1">
                <img src="${imageThumbnailUrl}" width="192px" alt="${item.title}">
            </a>
        </li>
`;
    }

    renderVideo(item) {
        const imageUrl = this.videoStreamingUrl + item.path;
        const imageThumbnailUrl = this.imageDownloadThumbnailBaseUrl + item.path;

        return `
        <li>
            <a href="${imageUrl}" data-glightbox="type: video" class="glightbox" data-gallery="gallery1">
                <img src="${imageThumbnailUrl}" width="192px" alt="${item.title}">
            </a>
        </li>
`;
    }

    renderFolder(item) {
        const imageThumbnailUrl = this.imageDownloadThumbnailBaseUrl + item.path;

        this.folders = true;

        return `
        <li>
            <a href="#${item.path}">
                <img src="${imageThumbnailUrl}" width="192px" alt="${item.title}">
            </a>
            <span>${item.title}</span>
        </li>
`;
    }

    dispatchGalleryEvent() {
        const folderDataEvent = new CustomEvent(TemplateService.EVENT_NAME, {detail: {}, bubbles: true});
        dispatchEvent(folderDataEvent);
        console.log("dispatchGalleryEvent ran");
    }
}
