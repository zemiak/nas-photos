var ApplicationStorage = {
    get: function(key, defaultValue) {
        var value = localStorage.getItem("com.zemiak.nasphotos." + key);

        return (!value) ? defaultValue : JSON.parse(value);
    },

    set: function(key, value) {
        localStorage.setItem("com.zemiak.nasphotos." + key, JSON.stringify(value));
    }
};
