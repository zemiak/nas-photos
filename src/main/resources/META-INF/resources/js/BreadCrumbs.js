export class BreadCrumbs extends HTMLElement {
    connectedCallback() {
        addEventListener("hashchange", e => this.onHashChange(e));
        this.render();
    }

    onHashChange(e) {
        const newUrl = e.newURL;
        var folder;

        if (!newUrl.includes("#")) {
            folder = "";
        } else {
            folder = window.location.href.split('#')[1];
        }

        this.render();
    }

    render() {
        var loc = window.location.href;
        if (!loc.includes("#")) {
            loc = "";
        } else {
            loc = loc.split('#')[1];
        }

        const decoded = decodeURIComponent(loc).replace("+", " ");
        const pieces = decoded.split("/");

        var html = this.renderItem("/#", "Roky");
        if (pieces.length >= 1 && pieces[0] != "") {
            html = html + "&nbsp;|&nbsp;" + this.renderItem("/#" + pieces[0], pieces[0]);
        }

        if (pieces.length >= 2 && pieces[1] != "") {
            html = html + "&nbsp;|&nbsp;" + this.renderItem("/#" + pieces[0] + encodeURI("/" + pieces[1]), pieces[1]);
        }

        this.innerHTML = html;
    }

    renderItem(url, title) {
        return `<a href="${url}">${title}</a>`;
    }
}

customElements.define("bread-crumbs", BreadCrumbs);
