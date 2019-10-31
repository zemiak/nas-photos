import { TemplateService } from "./TemplateService.js";

/**
 * lightbox/simpleLightbox.js: https://dbrekalo.github.io/simpleLightbox/
 * glightbox/glightbox.js: https://github.com/biati-digital/glightbox
 */

export class LightBox extends HTMLElement {
    connectedCallback() {
        addEventListener(new TemplateService().getEventName(), e => this.onRenderLightboxEvent(e));
        console.log("Subscribed to lightbox event");
    }

    onRenderLightboxEvent(e) {
        //setTimeout(this.simpleLightbox, 500);
        setTimeout(this.glightbox, 500);
        console.log("lightbox postponed");
    }

    simpleLightbox() {
        console.log("lightboxing");
        const elements = document.querySelectorAll("li>a");
        this.lightbox = new SimpleLightbox({elements: elements});
        console.log("lightboxed", elements);
    }

    glightbox() {
        var lightbox = GLightbox();
    }
}

customElements.define("light-box", LightBox);
