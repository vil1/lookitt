class lookitt.Session
  constructor: (url,@user) ->
    @ws = new WebSocket(url)

  send : (msg) -> @ws.send(JSON.stringify({'user' : @user, 'payload': msg}))

  attach: (handler) =>
    @ws.addEventListener("message", handler)

