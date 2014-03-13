modules = {
    application {
        resource url:'js/application.js'
    }

    customCSS
            {
                resource url: "css/custom01.css"
            }
    bootstrap{
        resource url: "bower_components/bootstrap/docs/assets/js/bootstrap.min.js"
        resource url: "bower_components/bootstrap/docs/assets/css/bootstrap.css"
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
}