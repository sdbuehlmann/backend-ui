const SVG_NAMESPACE = "http://www.w3.org/2000/svg";
const PX_PER_S_RATIO = 1 / 1;
const PX_PER_MS_RATIO = PX_PER_S_RATIO / 1000;

const SPACE_TIME_MARKER_LABELS = 50;
const SPACE_PATH_MARKER_LABELS = 20;

const GRID_SURPLUS = 5;

async function render() {
    const svgCanvas = document.getElementById("canvas");
    svgCanvas.innerHTML = '';

    const trainRunId = document.getElementById("trainRunIdInput").value;
    console.log("Querring visualisation data for train " + trainRunId);

    let response = await fetch("http://localhost:8080/visualisation/" + trainRunId);
    let body = await response.json();

    console.log(response);
    console.log(body);

    new SvgRenderer(
        svgCanvas,
        body.graphs.map(graph => new Graph(
            graph.name,
            graph.color,
            graph.entries.map(entry => new GraphEntry(new Date(entry.time), entry.pathPosition, entry.tooltip, entry.label)),
            graph.displayType,
        )),
        body.timeMarkers.map(tm => new TimeMarker(new Date(tm.time), tm.label)),
        body.pathMarkers.map(pm => new PathMarker(pm.position, pm.name, pm.color, pm.tooltip))
    ).render();
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

class Position {

    /** @type {number} */
    x;

    /** @type {number} */
    y;

    /** @type {string | undefined} */
    info;

    /**
     * @param {number} x
     * @param {number} y
     * @param {string | null} info
     */
    constructor(x, y, info = null) {
        this.x = x;
        this.y = y;
        this.info = info
    }

    /**
     * @param {number} deltaX
     * @param {number} deltaY
     * @returns {Position}
     */
    move(deltaX, deltaY) {
        return new Position(this.x + deltaX, this.y + deltaY);
    }
}

const DisplayType = {
    solid: 'SOLID',
    dashed: 'DASHED',
    noLine: 'NO_LINE'
};

class Graph {
    /** @type {string} */
    name;

    /** @type {string} */
    color;

    /** @type {GraphEntry[]} */
    entries;

    /** @type {DisplayType} */
    displayType;

    /**
     * @param {string} name
     * @param {string} color
     * @param {GraphEntry[]} entries
     * @param {DisplayType} displayType
     */
    constructor(
        name,
        color,
        entries,
        displayType,
    ) {
        this.name = name;
        this.color = color;
        this.entries = entries;
        this.displayType = displayType;
    }
}

class TimeMarker {
    /** @type {Date} */
    time;

    /** @type {string} */
    label;

    /**
     * @param {Date} time
     * @param {string} label
     */
    constructor(time, label) {
        this.time = time;
        this.label = label;
    }
}

class PathMarker {

    /** @type {number} */
    position;

    /** @type {string} */
    name;

    /** @type {string | null} */
    color;

    /** @type {string | null} */
    tooltip;

    /**
     * @param {number} position
     * @param {string} name
     * @param {string | null} color
     * @param {string | null} tooltip
     */
    constructor(position, name, color, tooltip) {
        this.position = position;
        this.name = name;
        this.color = color;
        this.tooltip = tooltip;
    }
}

class SvgRenderer {

    /** @type {HTMLElement} */
    svgCanvas;

    /** @type {Graph[]} */
    graphs;

    /** @type {TimeMarker[]} */
    timeMarkers;

    /** @type {PathMarker[]} */
    pathMarkers;

    /** @type {number} */
    #width;

    /** @type {number} */
    #height;

    /** @type {Date} */
    #startingTime

    /** @type {number} */
    #highestPosition

    /**
     * @param {HTMLElement} svgCanvas
     * @param {Graph[]} graphs
     * @param {TimeMarker[]} timeMarkers
     * @param {PathMarker[]} pathMarkers
     */
    constructor(
        svgCanvas,
        graphs,
        timeMarkers,
        pathMarkers
    ) {
        this.graphs = graphs;
        this.timeMarkers = timeMarkers;
        this.pathMarkers = pathMarkers;
        this.svgCanvas = svgCanvas;

        this.#highestPosition = pathMarkers[pathMarkers.length - 1].position;
        this.#width = this.#calculatePathPosition(this.#highestPosition) + GRID_SURPLUS + SPACE_TIME_MARKER_LABELS;

        this.#startingTime = this.timeMarkers[0].time;

        const highestTime = timeMarkers[timeMarkers.length - 1].time;
        this.#height = this.#calculateTimePosition(highestTime) + GRID_SURPLUS;
    }

    render() {
        this.svgCanvas.setAttribute("width", this.#width + "");
        this.svgCanvas.setAttribute("height", this.#height + "");

        this.#renderPathMarkers();
        this.#renderTimeMarkers();
        this.graphs.forEach(graph => this.#renderGraph(graph));
    }

    #renderTimeMarkers() {
        console.log("Render time markers", this.timeMarkers);

        const xPosEnd = this.#calculatePathPosition(this.#highestPosition) + GRID_SURPLUS;

        this.timeMarkers.forEach((marker) => {

            const yPos = this.#calculateTimePosition(marker.time);
            const lineElement = createLineElement(
                new Position(SPACE_TIME_MARKER_LABELS, yPos),
                new Position(xPosEnd, yPos),
                "white"
            )

            const textElement = createTextElement(
                "white",
                new Position(SPACE_TIME_MARKER_LABELS - 5, yPos),
                marker.label,
                AlignmentBaseline.middle,
                TextAnchor.end
            );

            this.svgCanvas.appendChild(lineElement);
            this.svgCanvas.appendChild(textElement);
        });
    }

    #renderPathMarkers() {
        console.log("Draw path markers", this.pathMarkers);

        this.pathMarkers.forEach((marker) => {
            /** @type {string} */
            const color = marker.color ?? "white";

            const xPos = this.#calculatePathPosition(marker.position);
            const lineElement = createLineElement(
                new Position(xPos, SPACE_PATH_MARKER_LABELS),
                new Position(xPos, this.#height),
                color,
                marker.tooltip
            );

            const textElement = createTextElement(
                color,
                new Position(xPos, 0),
                marker.name,
                AlignmentBaseline.hanging,
                TextAnchor.middle
            );

            this.svgCanvas.appendChild(lineElement);
            this.svgCanvas.appendChild(textElement);
        })
    }

    /**
     *
     * @param {Graph} graph
     */
    #renderGraph(graph) {
        console.log("Draw graph ", graph);

        const positions = graph.entries
            .map(entry => {
                const yPos = this.#calculateTimePosition(entry.time);
                const xPos = this.#calculatePathPosition(entry.position);

                return [new Position(xPos, yPos, entry.tooltip), entry];
            });

        // draw line
        if (graph.displayType !== DisplayType.noLine) {
            const dashed = graph.displayType === DisplayType.dashed;
            const pathElement = createPathElement(
                positions.map(([position, graphEntry]) => position),
                graph.color,
                dashed,
                graph.name
            );

            this.svgCanvas.appendChild(pathElement);
        }

        // draw circles
        positions.forEach(([position, graphEntry]) => {
            const circleElement = createCircleElement(position, graph.color, position.info);
            this.svgCanvas.appendChild(circleElement);

            if (graphEntry.label !== null) {
                if (position.x < this.#highestPosition / 2) {
                    this.svgCanvas.appendChild(createLabelElement(position, graphEntry.label, graph.color, LabelOrientation.up));
                } else {
                    this.svgCanvas.appendChild(createLabelElement(position, graphEntry.label, graph.color, LabelOrientation.down));
                }
            }
        })
    }

    /**
     * @param {Date} time
     */
    #calculateTimePosition(time) {
        const deltaInMs = time.getTime() - this.#startingTime.getTime();
        return SPACE_PATH_MARKER_LABELS + GRID_SURPLUS + deltaInMs * PX_PER_MS_RATIO;
    }

    /**
     * @param {number} distance
     */
    #calculatePathPosition(distance) {
        return SPACE_TIME_MARKER_LABELS + GRID_SURPLUS + distance;
    }
}

const TextAnchor = {
    start: 'start',
    middle: 'middle',
    end: 'end'
};

const AlignmentBaseline = {
    hanging: 'hanging',
    baseline: 'baseline',
    middle: 'middle'
};

/**
 * @param {string} color
 * @param {Position} pos
 * @param {string} text
 * @param {AlignmentBaseline | null} alignmentBaseline
 * @param {TextAnchor | null} textAnchor
 * @returns {SVGTextElement}
 */
function createTextElement(
    color,
    pos,
    text,
    alignmentBaseline,
    textAnchor
) {
    /** @type {SVGTextElement} */
    const textElement = document.createElementNS(SVG_NAMESPACE, "text");

    textElement.setAttribute("x", pos.x + "");
    textElement.setAttribute("y", pos.y + "");
    textElement.textContent = text;

    const textAnchorStyle = textAnchor != null ? "text-anchor:" + textAnchor : null;
    const fillStyle = "fill:" + color;

    textElement.style = [textAnchorStyle, fillStyle]
        .filter(s => s != null)
        .join(";");

    if (alignmentBaseline != null) {
        textElement.setAttribute("alignment-baseline", alignmentBaseline + "");
    }

    return textElement;
}

/**
 * @param {Position} fromPos
 * @param {Position} toPos
 * @param {string} color
 * @param {string | null} tooltip
 * @returns {SVGLineElement}
 */
function createLineElement(
    fromPos,
    toPos,
    color,
    tooltip = null
) {
    const lineElement = document.createElementNS(SVG_NAMESPACE, "line");

    lineElement.setAttribute("x1", fromPos.x + "");
    lineElement.setAttribute("y1", fromPos.y + "");
    lineElement.setAttribute("x2", toPos.x + "");
    lineElement.setAttribute("y2", toPos.y + "");
    lineElement.setAttribute("stroke", color);

    if (tooltip !== null) {
        lineElement.setAttribute("class", "highlightable");
        const tooltipElement = createTitleElement(tooltip);
        lineElement.appendChild(tooltipElement);
    }

    return lineElement;
}

/**
 * @param {string} text
 * @returns {SVGTitleElement}
 */
function createTitleElement(text) {
    const titleElement = document.createElementNS(SVG_NAMESPACE, "title");
    titleElement.textContent = text;

    return titleElement;
}

/**
 * @param {Position} position
 * @param {string} color
 * @param {string | null} tooltip
 * @returns {SVGCircleElement}
 */
function createCircleElement(position, color, tooltip = null) {
    const circleElement = document.createElementNS(SVG_NAMESPACE, "circle");

    circleElement.setAttribute("cx", position.x + "");
    circleElement.setAttribute("cy", position.y + "");
    circleElement.setAttribute("r", 3 + "");
    circleElement.setAttribute("fill", color);

    if (tooltip !== null) {
        circleElement.setAttribute("class", "highlightable");
        const tooltipElement = createTitleElement(tooltip);
        circleElement.appendChild(tooltipElement);
    }

    return circleElement;
}

/**
 * @param {Position[]} positions
 * @param {string} color
 * @param {boolean} dashed
 * @param {string | null} tooltip
 * @param {number} thickness
 * @returns {SVGPathElement}
 */
function createPathElement(
    positions,
    color,
    dashed,
    tooltip = null,
    thickness = 3
) {
    const joinedPositions = positions
        .map((position) => `${position.x},${position.y}`)
        .join(" ");

    const pathElement = document.createElementNS(SVG_NAMESPACE, "path");

    pathElement.setAttribute("d", "M " + joinedPositions);
    pathElement.setAttribute("fill", "none");
    pathElement.setAttribute("stroke", color);
    pathElement.setAttribute("stroke-width", thickness + "");

    if (dashed) {
        pathElement.setAttribute("stroke-dasharray", "5,10");
    }

    if (tooltip !== null) {
        const tooltipElement = createTitleElement(tooltip);
        pathElement.setAttribute("class", "highlightable-path");
        pathElement.appendChild(tooltipElement);
    }

    return pathElement;
}

const LabelOrientation = {
    up: 'up',
    down: 'down'
};

/**
 * @param {Position} position
 * @param {string} text
 * @param {string} color
 * @param {LabelOrientation} orientation
 * @returns {SVGElement}
 */
function createLabelElement(
    position,
    text,
    color,
    orientation
) {
    const posUp1 = position.move(20, -50);
    const posUp2 = posUp1.move(5, 0);
    const posUpText = posUp2.move(5, 0);

    const posDown1 = position.move(-20, 50);
    const posDown2 = posDown1.move(-5, 0);
    const posDownText = posDown2.move(-5, 0);

    const pathElement = createPathElement(
        orientation === LabelOrientation.up ? [position, posUp1, posUp2] : [position, posDown1, posDown2],
        color,
        false,
        null,
        1);

    const textElement = createTextElement(
        color,
        orientation === LabelOrientation.up ? posUpText : posDownText,
        text,
        AlignmentBaseline.middle,
        orientation === LabelOrientation.up ? TextAnchor.start : TextAnchor.end
    );

    const groupElement = document.createElementNS(SVG_NAMESPACE, "g");

    groupElement.appendChild(pathElement);
    groupElement.appendChild(textElement);

    return groupElement;
}
