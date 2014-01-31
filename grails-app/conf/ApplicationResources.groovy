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

    knockout
            {
                resource url:"js/knockout-3.0.0.js"
            }
}