modules = {
    application {
        resource url:'js/application.js'
    }

    customCSS
            {
                resource url: "css/custom01.css"
            }
    bootstrap{
        resource url: "bower_components/bootstrap/dist/js/bootstrap.min.js"
        resource url: "bower_components/bootstrap/dist/css/bootstrap.min.css"
    }


    jquery
            {
                resource url:"bower_components/jquery/jquery.min.js"
            }

    jqueryFormValidator
            {
                resource url:"js/FormValidators/jquery.validate.min.js"
                resource url:"js/FormValidators/jquery.validate.Bootstrap.js"
            }

    zoomMagnifier
            {
                resource url:"css/magnifier.css"
                resource url:"js/zoomMagnifier.js"
            }
    canvas2Image
            {
                resource url:"js/Canvas2Image/base64.js"
                resource url:"js/Canvas2Image/canvas2image.js"
            }


}