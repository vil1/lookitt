class lookitt.Session
  constructor: (url) ->
    @ws = new WebSocket(url)



  send : (msg) -> @ws.send(msg)

  attach: (handler) ->
    @ws.addEventListener("message", handler)

