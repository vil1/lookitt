class lookitt.Session
  constructor: (url) ->
    @ws = new WebSocket(url)

    @ws.onmessage = (msg) -> console.log(msg)

  send : (msg) -> @ws.send(msg)
