export class Spinner {
    constructor() {
        this.spinner = document.querySelector("#spinner");
    }

    show() {
        this.spinner.classList.remove("spinner-hidden");
        this.spinner.classList.add("spinner-visible");
    }

    hide() {
        this.spinner.classList.remove("spinner-visible");
        this.spinner.classList.add("spinner-hidden");
    }
}
