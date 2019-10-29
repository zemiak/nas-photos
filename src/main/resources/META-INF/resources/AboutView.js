class AboutView extends HTMLElement {
    connectedCallback() {
        this.innerText = "About";
    }
}

customElements.define("about-view", AboutView);
