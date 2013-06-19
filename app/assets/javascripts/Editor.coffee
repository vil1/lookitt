class lookitt.Editor

  constructor: (session) ->

    $("#submit").click(() ->
      if (window.getSelection().rangeCount)
        range = window.getSelection().getRangeAt(0)
        container = verifySelection(range)
        if container
          session.send(container)
      return false
    )

    $('#editor').popover({
      'content': 'ahoy!'
      'placement': 'bottom'
    })

    $(document).bind('keyup mouseup',(event) ->
      if (window.getSelection().rangeCount)
        range = window.getSelection().getRangeAt(0)
        container = verifySelection(range) unless range.collapsed
        if container
          $('#editor').popover('show')
        return false
    )

  verifySelection = (range) =>
    container = $('#editor pre[data-resource="file"]')
    return {
      startPath: getPathFrom(container, range.startContainer),
      start: range.startOffset,
      endPath: getPathFrom(container, range.endContainer),
      end: range.endOffset,
    } if $(range.commonAncestorContainer).parents().filter($('#editor'))

  getPathFrom = (node, descendant) =>
    $.map($(descendant).parentsUntil(node),(n) -> $(n).index()).reverse()


