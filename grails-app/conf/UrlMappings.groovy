class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.${format})?"{
            constraints {
                // apply constraints here
            }
        }

        "/consentForm/search" (view:"consentForm/search")
        "/consentForm/cuttingRoom" (view:"consentForm/cuttingRoom")

        "/"(view:"/index")
        "404"(view:'/error')
        "500"(view:'/error')
	}
}
