import { TemplateService } from "./TemplateService.js";

/**
 * glightbox/glightbox.js: https://github.com/biati-digital/glightbox
 */

export class LightBox extends HTMLElement {
    connectedCallback() {
        addEventListener(TemplateService.EVENT_NAME, e => this.onRenderLightboxEvent(e));
        console.log("Subscribed to lightbox event " + TemplateService.EVENT_NAME);
    }

    onRenderLightboxEvent(e) {
        console.log("Got the event " + TemplateService.EVENT_NAME)
        setTimeout(this.glightbox, 500);
        console.log("lightbox postponed");
    }

    glightbox() {
        var lightbox = GLightbox();
        console.log("GLightbox instantiated", lightbox);
    }
}

customElements.define("light-box", LightBox);
