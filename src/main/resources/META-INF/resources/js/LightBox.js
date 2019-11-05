import { TemplateService } from "./TemplateService.js";

/**
 * glightbox/glightbox.js: https://github.com/biati-digital/glightbox
 */

export class LightBox extends HTMLElement {
    connectedCallback() {
        addEventListener(new TemplateService().getEventName(), e => this.onRenderLightboxEvent(e));
        console.log("Subscribed to lightbox event");
    }

    onRenderLightboxEvent(e) {
        setTimeout(this.glightbox, 500);
        console.log("lightbox postponed");
    }

    glightbox() {
        var lightbox = GLightbox();
    }
}

customElements.define("light-box", LightBox);
