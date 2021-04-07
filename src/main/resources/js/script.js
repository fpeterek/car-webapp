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
    let coords = SMap.Coords.fromWGS84(position.longitude, position.latitude)

    return new SMap.Marker(coords, id, {url: marker})
}

class Waypoint {
    constructor(jsonObject) {
        this.position = new Position(jsonObject.position)
        this.id = jsonObject.id
    }

    asMarker() {
        return createMarker("img/waypoint.png", this.id, this.position)
    }
}

class Car {
    constructor(jsonObject) {
        this.direction = jsonObject.heading != null ? jsonObject.heading : 0
        this.position = new Position(jsonObject.position)
    }

    sprite() {
        let dist = this.direction % 15
        dist = (dist <= 7) ? (-dist) : (15-dist)

        let sprite = (this.direction + dist) % 360
        return 'img/' + sprite + '.png'
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
    xhr.open(method, "http://127.0.0.1:7778" + endpoint, true)
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

function applyGenericHandlers(xmlhttpreq) {
    xmlhttpreq.ontimeout = function(e) {
        console.log('Timeout while calling API')
    }
    xmlhttpreq.onerror = function(e) {
        console.log('Failed to make call to server')
    }
}

function putWaypoint(lat, lon) {
    let json = JSON.stringify({"position": {"latitude": lat, "longitude": lon}})
    console.log("lat: " + lat + " lon: " + lon)
    let req = createPostRequest()
    req.onload = function() {
        update()
    }
    applyGenericHandlers(req)
    req.send(json)
}

function deleteWaypoint(id) {
    let req = createDeleteRequest(id)
    req.onload = function() {
        update()
    }
    applyGenericHandlers(req)
    req.send()
}

function update() {
    let xhr = createInfoRequest()
    xhr.onload = function() {
        handleInfoResponse(xhr)
        render()
    }
    applyGenericHandlers(xhr)
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
        deleteWaypoint(e.target.getId())
    })

    map.getSignals().addListener(this, "map-click", function(e, elm) {
        let coords = SMap.Coords.fromEvent(e, map)
        let lonlat = coords.toWGS84()
        putWaypoint(lonlat[1], lonlat[0])
    })

    setInterval(update, 1000)
}
