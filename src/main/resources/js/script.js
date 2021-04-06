let map
let markerLayer
let mouse

let car = null
let waypoints = []

class Position {
    constructor(jsonObject) {
        this.latitude = jsonObject.latitude
        this.longitude = jsonObject.longitude
        this.adjust()
    }

    adjustLat() {
        if (this.latitude < -90) {
            this.latitude += 180
        } else if (this.latitude > 90) {
            this.latitude -= 180
        }
    }

    adjustLon() {
        if (this.longitude < -180) {
            this.longitude += 360
        } else if (this.longitude > 180) {
            this.longitude -= 360
        }
    }

    adjust() {
        this.adjustLat()
        this.adjustLon()
    }
}

function createMarker(sprite, id, position) {
    let marker = JAK.mel("div")
    let img = JAK.mel("img", {src: sprite})
    marker.appendChild(img)

    return new SMap.Marker(
        SMap.Coords.fromWGS84(position.longitude, position.latitude),
        id,
        {url: marker}
    )
}

class Waypoint {
    constructor(jsonObject) {
        this.position = new Position(jsonObject.position)
        this.id = jsonObject.id
    }

    asMarker() { // TODO: Provide sprite
        return createMarker("img/waypoint.png", this.id, this.position)
    }
}

class Car {
    constructor(jsonObject) {
        this.direction = jsonObject.heading
        this.position = new Position(jsonObject.position)
    }

    sprite() {
        let dist = this.direction % 15
        dist = (dist <= 7) ? (-dist) : (15-dist)

        let sprite = (this.direction + dist) % 360
        return 'img/' + sprite + '.png' // TODO: Provide sprites
    }

    asMarker() {
        return createMarker(this.sprite(), null, this.position)
    }
}

function render() {
    markerLayer.removeAll()
    for (let waypoint of waypoints) {
        markerLayer.addMarker(waypoint.asMarker())
    }
    if (car != null) {
        markerLayer.addMarker(car.asMarker())
    }
    markerLayer.enable()
}

function parseInfo(info) {

    car = new Car(info)

    let lst = []
    for (let wp of info.waypoints) {
        lst.push(new Waypoint(wp))
    }
    waypoints = lst
}

function handleInfoResponse(xhr) {
    if (xhr.status !== 200) {
        console.log("Server responded with " + xhr.status)
        return
    }
    let resp = xhr.responseText

    let parsed = JSON.parse(resp)

    if ('failure' in parsed) {
        return console.log(parsed.failure)
    }

    parseInfo(parsed)
}

function baseRequest(method, endpoint) {
    let xhr = new XMLHttpRequest()
    xhr.timeout = 500
    xhr.open(method, "http://127.0.0.1:7478" + endpoint, true)
    return xhr
}

function createInfoRequest() {
    return baseRequest("GET", "/info")
}

function createPostRequest() {
    let xhr = baseRequest("POST", "/waypoints")
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8")
    return xhr
}

function createDeleteRequest(id) {
    return baseRequest("DELETE", "/waypoints?id=" + encodeURIComponent(id))
}

/*function createSearchRequest(term) {
    let xhr = new XMLHttpRequest()
    xhr.open("GET", "http://127.0.0.1:5050/search?term=" + encodeURIComponent(term), true)
    xhr.timeout = 500
    return xhr
}

function handleSearchResponse(xhr) {
    if (xhr.status !== 200) {
        console.log("Server responded with " + xhr.status)
        return
    }
    let resp = xhr.responseText

    let parsed = JSON.parse(resp)

    if (parsed.status === 'failure') {
        console.log(parsed.reason)
        return
    }

    console.log("Search response received")
}

function performSearch() {
    console.log("Performing search")
    let term = document.getElementById("sbox").value
    let xhr = createSearchRequest(term)
    xhr.onload = function() {
        handleSearchResponse(xhr)
    }
    xhr.ontimeout = function(e) {
        console.log('Not found')
    }
    xhr.onerror = function(e) {
        console.log('Not found')
    }
    xhr.send()
}*/

function update() {
    let xhr = createInfoRequest()
    xhr.onload = function() {
        handleInfoResponse(xhr)
        render()
    }
    xhr.ontimeout = function(e) {
        console.log('Timeout while calling API')
    }
    xhr.onerror = function(e) {
        console.log('Failed to make call to server')
    }
    xhr.send()
}

function initMap() {
    let center = SMap.Coords.fromWGS84(18.16, 49.83);

    map = new SMap(JAK.gel("mapa"), center, 10);
    map.addDefaultLayer(SMap.DEF_BASE).enable();
    map.addDefaultControls();
    markerLayer = new SMap.Layer.Marker()
    map.addLayer(markerLayer)
    markerLayer.enable()

    let sync = new SMap.Control.Sync({bottomSpace:100})
    map.addControl(sync)

    mouse = new SMap.Control.Mouse(SMap.MOUSE_PAN | SMap.MOUSE_WHEEL | SMap.MOUSE_ZOOM)
    map.addControl(mouse)

    map.getSignals().addListener(this, "marker-click", function(e) {
        let marker = e.target
        /*let flight = marker.getId()

        for (let fl of flights) {
            if (fl.number === flight) {
                selected.flight = fl
                render()
            }
        }*/
    })

    setInterval(update, 1000)
}
