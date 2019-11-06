export class Config {
    constructor() {
        this.config = JSON.parse(window.localStorage.getItem("nasphotos.config"));
    }

    getPort() {
        return this.config["port"];
    }
}
