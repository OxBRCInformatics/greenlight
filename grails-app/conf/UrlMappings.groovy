class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.${format})?"{
            constraints {
                // apply constraints here
            }
        }

		"/api/consents/${lookupId}?(.${format})?"(controller: "consentStatus")
        "/consentForm/search" (view:"consentForm/search")
        "/consentForm/cuttingRoom" (view:"consentForm/cuttingRoom")
		"/attachment/unAnnotatedList" (view:"attachment/unAnnotatedList")
		"/attachment/annotatedList" (view:"attachment/annotatedList")
		"/attachment/list" (view:"attachment/unAnnotatedList")

        "/"(view:"/index")
        "404"(view:'/errors/404')
        "403"(view:'/errors/403')
        "500"(view:'/error')
	}
}
