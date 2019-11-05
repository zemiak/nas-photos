export class Cache {
    constructor() {
        const cache = window.localStorage.getItem("nasphotos.cache");
        if (! cache) {
            window.localStorage.setItem("nasphotos.cache", JSON.stringify({}));
        }
    }

    get(key) {
        const cache = JSON.parse(window.localStorage.getItem("nasphotos.cache"));
        if (key in cache) {
            return cache[key];
        }

        return undefined;
    }

    set(key, value) {
        const cache = JSON.parse(window.localStorage.getItem("nasphotos.cache"));
        cache[key] = value;
        window.localStorage.setItem("nasphotos.cache", JSON.stringify(cache));
    }

    contains(key) {
        const cache = JSON.parse(window.localStorage.getItem("nasphotos.cache"));
        return (key in cache);
    }

    remove(key) {
        const cache = JSON.parse(window.localStorage.getItem("nasphotos.cache"));
        if (key in cache) {
            delete cache[key];
        }
        window.localStorage.setItem("nasphotos.cache", JSON.stringify(cache));
    }
}
