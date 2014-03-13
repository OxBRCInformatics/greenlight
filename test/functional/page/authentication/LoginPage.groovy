package page.authentication;

import geb.Page

class LoginPage extends Page{
	
	static url = "login/auth"
	
	static at = {
		url == "login/auth" &&
		title == "Login"
	}
	
	static content = {
		loginForm 	{ $("#loginForm") }
		username 	{ loginForm.find('#username') }
		password 	{ loginForm.find('#password') }
		rememberMe 	{ loginForm.find('#remember_me') }

		submitButton() {
			loginForm.find("input", type: "submit")
		}
		

		errors(required:false) { $(".alert") }

		invalidUsernameOrPasswordError(required:false) {
			//errors.filter(text:contains("Sorry, we were not able to find a user with that username and password."))
            $("div.login_message")
		}
	}

    def loginAdminUser(){
        loginUser("admin","password")
    }

    def loginRegularUser(){
        loginUser("user1", "password1")
    }

    /**
     * Logs a user into the system with the given password.
     * Note that this is pretty poor practice (Passwords should be chararrays), but it's a test harness so it's okay :)
     * @param user The username to log in
     * @param pass The password for the user
     */
    def loginUser(String user, String pass){
        username = user
        password = pass
        submitButton.click()

    }
}