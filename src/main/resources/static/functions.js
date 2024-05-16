
const SOCKET = new WebSocket("ws://localhost:8080/websocket");

async function init() {
    SOCKET.addEventListener("message", event => {
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

    const removedElementIds = Array.from(containerElement.querySelectorAll('[id]'))
        .filter(element => element.id)
        .map(element => element.id);

    if (removedElementIds.length > 0) {
        console.log("Ids removed elements: ", removedElementIds);

        sendResponse("65284f2b-8516-4d10-83b9-119444e274d4", new RemovedElementsDto(removedElementIds));
    }

    containerElement.replaceChildren(tempWrappingElement.firstChild);
}

/**
 * @param {string} id
 * @param {any} data
 */
function sendResponse(id, data) {
    const responseDto = new ResponseDto(id, data);

    console.log('Send response:', responseDto);
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

class RemovedElementsDto {

    /**
     * @param {Array<String>} ids
     */
    ids;

    /**
     * @param {Array<String>} ids
     */
    constructor(ids) {
        this.ids = ids;
    }
}