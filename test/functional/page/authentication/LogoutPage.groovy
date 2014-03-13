package page.authentication

import geb.Page

class LogoutPage extends Page{
	
	static url = "logout/index"
	
	static at = {
		url == "logout/index" &&
		title == "Model Catalogue - Home"
	}
	
}