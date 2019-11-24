var request = new XMLHttpRequest();
request.open('GET', '/backend/config', false);  // `false` makes the request synchronous
request.send(null);

if (request.status !== 200) {
    alert("Configuration cannot be read from the server.")
    throw new Error("Could not read config file /backend/config");
}

window._nasphotos_config = request.responseText;
