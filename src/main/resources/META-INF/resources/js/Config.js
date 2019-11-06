export class Config {
    constructor() {
        this.config = JSON.parse(window._nasphotos_config);
    }

    getPort() {
        return this.config["port"];
    }
}
