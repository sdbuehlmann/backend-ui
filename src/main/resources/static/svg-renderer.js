async function init() {
    console.log("Init...");

    // Create WebSocket connection.
    const socket = new WebSocket("ws://localhost:8081");

    // Connection opened
    socket.addEventListener("open", (event) => {
        console.log("WS open");
        socket.send("Hello Server!");
        console.log("Hello Server!");
    });

    socket.addEventListener("message", event => {
        console.log("Event!");
        console.log('Nachricht vom Server erhalten:', event.data);
    })

    // Eventlistener für den Fehlerfall hinzufügen
    socket.addEventListener('error', function (event) {
        console.error('WebSocket-Fehler:', event);
    });
}
