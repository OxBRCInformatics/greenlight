class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.${format})?"{
            constraints {
                // apply constraints here
            }
        }

		"/api/consents/${lookupId}?(.${format})?"(controller: "consentStatus",action: "getStatus")
        "/consentForm/search" (view:"consentForm/search")
        "/consentForm/cuttingRoom" (view:"consentForm/cuttingRoom")
		"/attachment/unAnnotatedList" (view:"attachment/unAnnotatedList")
		"/attachment/annotatedList" (view:"attachment/annotatedList")
		"/attachment/list" (view:"attachment/unAnnotatedList")
		"/mainAngularApp" (view:"mainAngularApp/index")

        "/"(view:"/index")
        "404"(view:'/errors/404')
        "403"(view:'/errors/403')
        "400"(view:'/errors/400')
        "500"(view:'/error')
	}
}