
const SOCKET = new WebSocket("ws://localhost:8080/websocket");

async function init() {
    SOCKET.addEventListener("message", event => {
        console.log('Nachricht vom Server erhalten:', event.data);

        const jsonObject = JSON.parse(event.data);
        handleSetInnerHtmlDto(jsonObject);
    })

    SOCKET.addEventListener('error', function (event) {
        console.error('WebSocket-Fehler:', event);
    });
}

/**
 * @param {SetInnerHtmlDto} dto
 */
function handleSetInnerHtmlDto(dto) {
    const containerElement = document.getElementById(dto.containerId);

    if (containerElement === null) {
        console.error("Element with id " + dto.containerId + " not found");
        return;
    }

    const tempWrappingElement = document.createElement('div');
    tempWrappingElement.innerHTML = dto.html;

    containerElement.replaceChildren(tempWrappingElement.firstChild);
}

/**
 * @param {string} id
 * @param {any} data
 */
function sendResponse(id, data) {
    const responseDto = new ResponseDto(id, data);
    SOCKET.send(JSON.stringify(responseDto));
}

// ===================== FUNCTIONS ========================================================================
/**
 *
 * @param {string | undefined} containerId
 * @returns {ElementValuesDto}
 */
function collectElementValues(containerId) {
    const element = document
        .getElementById(containerId);

    if (element !== null) {
        const allIds = element.querySelectorAll('[id]');

        return new ElementValuesDto(Array
            .from(allIds)
            .map(element => new ElementValueDto(element.id, element.value)));
    }

    return new ElementValuesDto([]);
}

// ===================== DTO'S ========================================================================
class SetInnerHtmlDto {
    /**
     * @type {string}
     */
    containerId;

    /**
     * @type {string}
     */
    html;

    /**
     * @param {string} containerId
     * @param {string} html
     */
    constructor(containerId, html) {
        this.containerId = containerId;
        this.html = html;
    }
}

class ResponseDto {
    /**
     * @type {string}
     */
    id;

    /**
     * @type {any | null}
     */
    data;

    /**
     * @param {string} id
     * @param {any | null} data
     */
    constructor(id, data) {
        this.id = id;
        this.data = data;
    }
}

class ElementValuesDto {
    /**
     * @type {Array<ElementValueDto>}
     */
    values;

    /**
     * @param {Array<ElementValueDto>} values
     */
    constructor(values) {
        this.values = values;
    }
}

class ElementValueDto {
    /**
     * @type {string}
     */
    elementId;

    /**
     * @type {string}
     */
    value;

    /**
     * @param {string} elementId
     * @param {string} value
     */
    constructor(elementId, value) {
        this.elementId = elementId;
        this.value = value;
    }
}