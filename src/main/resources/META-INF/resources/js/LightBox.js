import { TemplateService } from "./TemplateService.js";

export class LightBox extends HTMLElement {
    connectedCallback() {
        addEventListener(new TemplateService().getEventName(), e => this.onRenderLightboxEvent(e));
        console.log("Subscribed to lightbox event");
    }

    onRenderLightboxEvent(e) {
        setTimeout(this.postponedLightbox, 500);
        console.log("lightbox postponed");
    }

    postponedLightbox() {
        console.log("lightboxing");
        const elements = document.querySelectorAll("li>img");
        this.lightbox = new SimpleLightbox({elements: elements});
        console.log("lightboxed", elements);
    }
}

customElements.define("light-box", LightBox);
