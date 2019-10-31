export class TemplateService {
    constructor(url) {
        this.imageDownloadBaseUrl = url;
    }

    render(name, data) {
        const folderTitle = this.getFolderTitle(name);
        var html = "";

        html = html + this.header(folderTitle) + "\n";

        this.folders = false;
        data.items.forEach(item => {html = html + this.element(item)});

        html = html + this.footer() + "\n";

        if (! this.folders) {
            console.log("TemplateService.render: turning images into gallery");
        }

        return html;
    }

    header(title) {


        return `
    <h1>${title}</h1>
    <ul class="auto-grid">
        `;
    }

    getFolderTitle(name) {
        if ("" === name) {
            return "Roky";
        }

        const decoded = decodeURIComponent(name).replace("+", " ");
        const pieces = decoded.split(/[\/]+/);
        const lastPart = pieces[pieces.length - 1];
        return lastPart;
    }

    footer() {
        return `    </ul>
`;
    }

    element(item) {
        console.log(item);
        if ("folder" === item.type) {
            return this.renderFolder(item);
        }

        return this.renderPicture(item);
    }

    renderPicture(item) {
        const imageUrl = this.imageDownloadBaseUrl + item.path;

        return `
        <li>
            <img src="${imageUrl}" alt="${item.title}">
        </li>
`;
    }

    renderFolder(item) {
        this.folders = true;
        // <img onclick="window._gallery.render('${item.path}')" src="/img/folder.png" width="128px" height="128px" alt="${item.title}">

        return `
        <li>
            <img onclick="window._gallery.render('${item.path}')" src="/img/folder.png" width="90%" height="90%" alt="${item.title}">
            <span>${item.title}</span>
        </li>
`;
    }
}
