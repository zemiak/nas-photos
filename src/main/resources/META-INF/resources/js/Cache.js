export class Cache {
    constructor() {
        const cache = window.localStorage.getItem("nasphotos.cache");
        if (! cache) {
            window.localStorage.setItem("nasphotos.cache", {});
        }
    }

    get(key) {
        const cache = window.localStorage.getItem("nasphotos.cache");
        if (key in cache) {
            return cache[key];
        }

        return undefined;
    }

    set(key, value) {
        const cache = window.localStorage.getItem("nasphotos.cache");
        cache[key] = value;
    }

    contains(key) {
        const cache = window.localStorage.getItem("nasphotos.cache");
        return (key in cache);
    }

    remove(key) {
        const cache = window.localStorage.getItem("nasphotos.cache");
        if (key in cache) {
            delete cache[key];
        }
    }
}
