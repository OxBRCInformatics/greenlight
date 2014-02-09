

<canvas id="the-canvas"   width="100%" height="100%" style="width: 100%;height: 100%;"></canvas>


<g:javascript>


    var attachmentId = ${attachmentId}

    var BASE64_MARKER = ';base64,';



    function convertDataURIToBinary(dataURI) {
      var base64Index = dataURI.indexOf(BASE64_MARKER) + BASE64_MARKER.length;
      var base64 = dataURI.substring(base64Index);
      var raw = window.atob(base64);
      var rawLength = raw.length;
      var array = new Uint8Array(new ArrayBuffer(rawLength));

      for(i = 0; i < rawLength; i++) {
        array[i] = raw.charCodeAt(i);
      }
      return array;
    }



        $.ajax({
        url:"${g.createLink(controller: 'attachment', action: 'viewPDF')}",
    dataType: 'json',
    data: {id:attachmentId},
    success: function(data) {
        var pdfAsDataUri = data.content
        var pdfAsArray = convertDataURIToBinary(pdfAsDataUri);
        PDFJS.disableWorker = true;
        PDFJS.getDocument(pdfAsArray).then(function getPdfHelloWorld(pdf) {
            pdf.getPage(1).then(function getPageHelloWorld(page) {
                var scale = 1.5;
                var viewport = page.getViewport(scale);
                var canvas = document.getElementById('the-canvas');
                var context = canvas.getContext('2d');
                canvas.height = viewport.height;
                canvas.width = viewport.width;
                page.render({canvasContext: context, viewport: viewport});
            });
        });
    },
    error: function(request, status, error) {
    $('#the-canvas').html("Can not render PDF!")
    }
});

</g:javascript>
