import { TemplateService } from "./TemplateService.js";

/**
 * glightbox/glightbox.js: https://github.com/biati-digital/glightbox
 */

export class LightBox extends HTMLElement {
    connectedCallback() {
        addEventListener(TemplateService.eventName(), e => this.onRenderLightboxEvent(e));
        console.log("Subscribed to lightbox event " + TemplateService.eventName());
    }

    onRenderLightboxEvent(e) {
        console.log("Got the event " + TemplateService.eventName())
        setTimeout(this.glightbox, 500);
        console.log("lightbox postponed");
    }

    glightbox() {
        window.lightbox = GLightbox();
        console.log("GLightbox instantiated");
    }
}

customElements.define("light-box", LightBox);
