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

    pdfViewer {
        resource url:"js/PDF/pdf.js"
    }


    fontawsome
            {
                resource url:"css/font-awesome-4.0.3/css/font-awesome.min.css"
            }
}