class lookitt.Timeline
  constructor: (session) ->
    messages = []

    session.attach((message) ->
      messageObj =  JSON.parse(message.data)
      html = """
             <div class="media" data-message="#{messages.length}">
             <a href="#" class="pull-left"><img src="http://lorempixel.com/64/64/" class="media-object" alt=""></a>
             <div class="media-body">
             <h4 class="media-heading">
               #{messageObj.user}  said :
             </h4>
             #{messageObj.comment || 'nothing'}
             </div>
             </div>
             """

      messages.push(messageObj)
      msgNode = $.parseHTML(html)[0]
      msgNode.onclick  = () ->
        container = $('#editor pre[data-resource="file"] ol')
        msg = messages[$(this).data("message")]
        range = document.createRange()
        range.setStart($.children(container)[msg.payload.startPath], msg.payload.start)
        range.setEnd($.children(container)[msg.payload.endPath], msg.payload.end)
        selection = window.getSelection()
        selection.removeAllRanges()
        selection.addRange(range)
        false
      $('[data-timeline]')[0].appendChild(msgNode)
    )

    resolve = (node, path) ->
      if path.length
        [id, tail...] = path
        resolve(node.childNodes[id], tail)
      else
        node
