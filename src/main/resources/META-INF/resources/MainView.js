class MainView extends HTMLElement {
    connectedCallback() {
        this.innerText = "Main";
    }
}

customElements.define("main-view", MainView);
