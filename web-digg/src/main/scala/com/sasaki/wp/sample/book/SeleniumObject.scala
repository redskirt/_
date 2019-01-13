package com.sasaki.wp.sample.book

import java.util.concurrent.TimeUnit

import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.By
import com.sasaki.packages.independent

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Jan 12, 2019 10:05:50 PM
 * @Description
 */
object SeleniumObject {

  def main(args: Array[String]): Unit = {

    // https://chromedriver.storage.googleapis.com/index.html
    //    System.setProperty("webdriver.chrome.driver", "/Users/sasaki/git/_/web-digg/src/main/resources/chromedriver")
    //		  System.setProperty("webdriver.gecko.driver", "/Users/sasaki/git/_/web-digg/src/main/resources/geckodriver")
    val driver = new SafariDriver

    try {
      driver.get("https://www.amazon.cn/gp/search/ref=sr_adv_b/?search-alias=stripbooks&field-binding_browse-bin=2038564051&sort=relevancerank&page=1")
      val ele = driver.findElementById("atfResults")
      
      val div_pagn = driver.findElementById("pagn").findElements(By.className("pagnLink")).get(1).click()
      
//      val div_pagn2 = div_pagn.findElement(By.id("pagn")).findElements(By.className("pagnLink")).get(1).click()
      
      // 执行js得到整个HTML
//     val o =  driver.executeScript("return document.documentElement.outerHTML")
//      independent.writeFile("/Users/sasaki/Desktop/t.txt", o.toString())
      driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    } catch {
      case t: Throwable => t.printStackTrace() // TODO: handle error
    } 
    finally {
      driver.quit()
    }

  }

}