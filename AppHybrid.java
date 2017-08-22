import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.html5.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.Cookie.Builder;

import io.appium.java_client.*;
import io.appium.java_client.android.*;
import io.appium.java_client.ios.*;

import com.perfectomobile.selenium.util.EclipseConnector;

public class AppiumTest {

    public static void main(String[] args) throws MalformedURLException, IOException {
        System.out.println("Run started");

        String browserName = "mobileOS";
        DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
        String host = <your cloud>;
        String user = <username>;
        String pw = <password>;
        
        // upload the application file to the Repository
		PerfectoLabUtils.uploadMedia(host, user, pw,
                "C:\\test\\applications\\com.voyagesoftech.myexpensemanager.apk",
                "PRIVATE:applications/com.voyagesoftech.myexpensemanager.apk");
        
        capabilities.setCapability("user", user);
        capabilities.setCapability("password", pw);
        capabilities.setCapability("deviceName", <Device ID>);
        capabilities.setCapability("automationName", "Appium");

        // Call this method if you want the script to share the devices with the Perfecto Lab plugin.
        setExecutionIdCapability(capabilities, host);

        // Application installation capabilities.
        capabilities.setCapability("app", "PRIVATE:applications/com.voyagesoftech.myexpensemanager.apk");
        capabilities.setCapability("autoInstrument", true);
        capabilities.setCapability("fullReset", true);
        // For Android:
        capabilities.setCapability("appPackage", "com.voyagesoftech.myexpensemanager");
        // capabilities.setCapability("appActivity", ".activities.BrowseActivity");
        // For iOS:
        // capabilities.setCapability("bundleId", "com.yoctoville.errands");

        AndroidDriver driver = new AndroidDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
        // IOSDriver driver = new IOSDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        
        try {
            // write your code here
        	Map<String, Object> params = new HashMap<>();
        	Boolean ExpClick = true;

    		driver.context("WEBVIEW");       	
        	// if the Expense button does not appear, need to skip over the ad
    		while (ExpClick) {
	        	try {
	        		// click the Expense button to enter a new expense
	        		driver.findElementByXPath("//*[text()='Expense']").click();
	        		ExpClick = false;
	        	} catch (Exception ne) {
	        		// did not find the Expense button - likely cause is ad display
	        		// go BACK and try again
	        		params.clear();
	        		params.put("keySequence", "BACK");
	        		driver.executeScript("mobile:presskey", params);
	        		System.out.println("Press the back button to get out of ad");
	        	}
        	}

        	// open the Expense category menu
        	driver.findElementByXPath("(//select[@id='CategoryExpense'])").click();

        	// select the expense category
        	driver.context("NATIVE_APP");
        	driver.scrollTo("Miscellaneous").click();

        	driver.context("WEBVIEW");
        	driver.findElementByXPath("(//select[@id='SubCategoryExpense'])").click();

        	driver.context("NATIVE_APP");
        	driver.findElementByXPath("//android.widget.CheckedTextView[@text='Office']").click();

        	driver.context("WEBVIEW");
        	driver.findElementByXPath("(//input[@id='EDescription'])").sendKeys("Notebooks");

        	driver.findElementByXPath("(//input[@id=\"Amountforex\"])").sendKeys("3.75");


        	driver.findElementByXPath("//*[text()='Save']").click();

        	// verify that Success Notification appears
        	driver.context("NATIVE_APP");
        	List<WebElement> elems = driver.findElementsByXPath("//android.widget.TextView[@text='Expense added succesfully']");
        	if (elems.isEmpty()) {
        		System.out.println("Expense not updated!");
        	} else {
        		driver.findElementByXPath("//android.widget.Button[@text='OK']").click();
        		System.out.println("Expense updated to ledger");
        	}

        	// use the image identification to click the home page button
        	params.clear();
        	params.put("content", "PRIVATE:Home-cropped.png");
        	driver.executeScript("mobile:image:select", params);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                driver.close();

                // In case you want to down the report or the report attachments, do it here.
                PerfectoLabUtils.downloadReport(driver, "pdf", "C:\\test\\reportAppHyb");
                // PerfectoLabUtils.downloadAttachment(driver, "video", "C:\\test\\report\\video", "flv");
                // PerfectoLabUtils.downloadAttachment(driver, "image", "C:\\test\\report\\images", "jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }

            driver.quit();
        }

        System.out.println("Run ended");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    private static void setExecutionIdCapability(DesiredCapabilities capabilities, String host) throws IOException {
        EclipseConnector connector = new EclipseConnector();
        String eclipseHost = connector.getHost();
        if ((eclipseHost == null) || (eclipseHost.equalsIgnoreCase(host))) {
            String executionId = connector.getExecutionId();
            capabilities.setCapability(EclipseConnector.ECLIPSE_EXECUTION_ID, executionId);
        }
    }
}
