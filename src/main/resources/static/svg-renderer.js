
const SOCKET = new WebSocket("ws://localhost:8080/websocket");

async function init() {
    console.log("Init...");

    const loadingElement = document.getElementById("loading");
    console.log(loadingElement.innerHTML);
    console.log(loadingElement.innerText);

    // Create WebSocket connection.
    // const socket = new WebSocket("ws://localhost:8080/websocket");
    const socket = SOCKET;

    // Connection opened
    socket.addEventListener("open", (event) => {
        console.log("WS open");
        socket.send("Hello Server!");
        console.log("Hello Server!");
    });

    socket.addEventListener("message", event => {
        console.log("Event!");
        console.log('Nachricht vom Server erhalten:', event.data);

        const jsonObject = JSON.parse(event.data);
        replace(jsonObject);
    })

    // Eventlistener für den Fehlerfall hinzufügen
    socket.addEventListener('error', function (event) {
        console.error('WebSocket-Fehler:', event);
    });
}

/**
 * @param {HtmlElementUpdateDto} dto
 */
function replace(dto) {
    const oldElement = document.getElementById(dto.elementId);
    const parentElement = oldElement.parentElement;

    const temp = document.createElement('div');
    temp.innerHTML = dto.elementHtml;

    console.log("Replaced element with id " + dto.elementId + "replaced with " + dto.elementHtml)

    parentElement.replaceChild(temp.firstChild, oldElement);
}

/**
 *
 * @param {string} actionId
 * @param {string} parentElementId
 */
function collectAllValues(actionId, parentElementId) {
    const allIds = document
        .getElementById(parentElementId)
        .querySelectorAll('[id]');

    const elementValues = Array
        .from(allIds)
        .map(element => new ElementValueDto(element.id, element.value));

    SOCKET.send(JSON.stringify(new ChildElementValuesDto(actionId, parentElementId, elementValues)));
}

class ChildElementValuesDto {

    /** @type {string} */
    triggeringActionId;

    /** @type {string} */
    parentElementId;

    /** @type {ElementValueDto[]} */
    elementValues;

    /**
     *
     * @param {string} triggeringActionId
     * @param {string} parentElementId
     * @param {ElementValueDto[]} elementValues
     */
    constructor(triggeringActionId, parentElementId, elementValues) {
        this.triggeringActionId = triggeringActionId;
        this.parentElementId = parentElementId;
        this.elementValues = elementValues;
    }
}

class ElementValueDto {

    /** @type {string} */
    elementId;

    /** @type {string} */
    value;

    /**
     *
     * @param {string} elementId
     * @param {string} elementHtml
     */
    constructor(elementId, value) {
        this.elementId = elementId;
        this.value = value;
    }
}

class HtmlElementUpdateDto {

    /** @type {string} */
    elementId;

    /** @type {string} */
    elementHtml;

    /**
     *
     * @param {string} elementId
     * @param {string} elementHtml
     */
    constructor(elementId, elementHtml) {
        this.elementId = elementId;
        this.elementHtml = elementHtml;
    }
}

class GraphEntry {
    /** @type {Date} */
    time;

    /** @type {number} */
    position;

    /** @type {string | undefined} */
    tooltip;

    /** @type {string | undefined} */
    label;

    /**
     *
     * @param {Date} time
     * @param {number} position
     * @param {string | undefined} tooltip
     * @param {string | undefined} label
     */
    constructor(time, position, tooltip, label) {
        this.time = time;
        this.position = position;
        this.tooltip = tooltip;
        this.label = label;
    }
}
