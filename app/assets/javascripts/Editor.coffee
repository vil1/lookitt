class lookitt.Editor

  constructor: (session) ->
    session.attach (message) ->
      container = $("#editor").find('[data-resource="file"]')[0]
      console.log("caught")
      data = JSON.parse message.data
      range = document.createRange()
      range.setStart(resolve(container, data.startPath), data.start)
      range.setEnd(resolve(container, data.endPath), data.end)
      selection = window.getSelection()
      selection.removeAllRanges()
      selection.addRange(range)


    $("#submit").click(() ->
      if (window.getSelection().rangeCount)
        range = window.getSelection().getRangeAt(0)
        container = verifySelection(range)
        if container
          session.send(JSON.stringify(container))
      return false
    )

    verifySelection = (range) ->
      container = $("#editor").find('[data-resource="file"]')[0]
      containerRange =  document.createRange()
      containerRange.setStart(container, 0)
      containerRange.setEnd(container, container.childNodes.length)
      return {
        startPath: getPathFrom(container, range.startContainer),
        start: range.startOffset,
        endPath: getPathFrom(container, range.endContainer),
        end: range.endOffset,
      } if containerRange.compareBoundaryPoints(Range.START_TO_START, range) is -1 and containerRange.compareBoundaryPoints(Range.END_TO_END, range) is 1

    getPathFrom = (container, descendant) ->
      recurse = (node, path) ->
        if node isnt container
          siblings = node.parentNode.childNodes
          s = for child, id in siblings
            {'item':child, 'id':id}

          index = s.filter((elem)->
            elem.item is node
          )[0]
          recurse node.parentNode, [index.id, path...]
        else
          path
      recurse(descendant,[])

    resolve = (node, path) ->
      if path.length
        [id, tail...] = path
        resolve(node.childNodes[id], tail)
      else
        node
