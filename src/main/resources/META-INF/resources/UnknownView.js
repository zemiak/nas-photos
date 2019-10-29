class UnknownView extends HTMLElement {
    connectedCallback() {
        this.innerText = "Unknown";
    }
}

customElements.define("unknown-view", UnknownView);
