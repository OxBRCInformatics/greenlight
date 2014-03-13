/*
	This is the Geb configuration file.
	
	See: http://www.gebish.org/manual/current/configuration.html
*/


import geb.driver.SauceLabsDriverFactory
import org.openqa.selenium.Platform
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

reportsDir = new File("target/geb-reports")
reportOnTestFailureOnly = false

projectName = "greenlight"
seleniumVersion = "2.39.0"

driver = {
    new FirefoxDriver()
}




// Download the driver and set it up automatically
private void downloadDriver(File file, String path) {
    if (!file.exists()) {
        def ant = new AntBuilder()
        ant.get(src: path, dest: 'driver.zip')
        ant.unzip(src: 'driver.zip', dest: file.parent)
        ant.delete(file: 'driver.zip')
        ant.chmod(file: file, perm: '700')
    }
}

environments {


    // run as "grails -Dgeb.env=chrome test-app"
    // See: http://code.google.com/p/selenium/wiki/ChromeDriver
    chrome {
        def chromeDriver = new File('test/drivers/chrome/chromedriver')
        downloadDriver(chromeDriver, "http://chromedriver.googlecode.com/files/chromedriver_mac_23.0.1240.0.zip")
        System.setProperty('webdriver.chrome.driver', chromeDriver.absolutePath)
        driver = { new ChromeDriver() }
    }

    // run as "grails -Dgeb.env=firefox test-app"
    // See: http://code.google.com/p/selenium/wiki/FirefoxDriver
    firefox {
        driver = { new FirefoxDriver() }
    }
	sauceIe7 {
		String username = System.getenv("SAUCE_USERNAME");
		String apiKey = System.getenv("SAUCE_ACCESS_KEY");
		if(username == null || apiKey == null){
			throw new IllegalArgumentException("Sauce OnDemand credentials not set.")
		}
		DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
		caps.setCapability("tunnel-identifier", System.getenv("TRAVIS_JOB_NUMBER"));
		caps.setCapability("selenium-version", seleniumVersion);
		caps.setCapability("name", projectName);
		caps.setCapability("platform", Platform.XP);
		caps.setCapability("version", "7");


		driver = {
			new RemoteWebDriver(new URL("http://${username}:${apiKey}@ondemand.saucelabs.com:80/wd/hub"), caps)
		}
	}
	sauceChrome{
		String username = System.getenv("SAUCE_USERNAME");
		String apiKey = System.getenv("SAUCE_ACCESS_KEY");
		if(username == null || apiKey == null){
			throw new IllegalArgumentException("Sauce OnDemand credentials not set.")
		}
		DesiredCapabilities caps = DesiredCapabilities.chrome();
		caps.setCapability("tunnel-identifier", System.getenv("TRAVIS_JOB_NUMBER"));
		caps.setCapability("selenium-version", seleniumVersion);
		caps.setCapability("name", projectName);
		caps.setCapability("platform", Platform.LINUX);

		driver = {
			new RemoteWebDriver(new URL("http://${username}:${apiKey}@ondemand.saucelabs.com:80/wd/hub"), caps)
		}
	}
}