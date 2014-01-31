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



    jqueryFormValidator
            {
                resource url:"js/formValidators/jquery.validate.min.js"
                resource url:"js/formValidators/jquery.validate.Bootstrap.js"

            }


}