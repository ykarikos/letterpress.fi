package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone        
      }
    }
    
    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get
        
        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("Letterpress")
      }
    }
    
    "check tiles match" in {
      running(FakeApplication()) {
        val newgame = route(FakeRequest(POST, "/newgame").withFormUrlEncodedBody(
            ("name", "foo")
        )).get
        
        status(newgame) must equalTo(SEE_OTHER)
        val location = redirectLocation(newgame).get
        location must startWith("/game/")
        
        val id = location.split("/")(2)
        
        val submit = route(FakeRequest(POST, "/submit").withFormUrlEncodedBody(
            ("word", "foo"), ("id", id), ("tiles", "1,2")
        )).get
        
        status(submit) must equalTo(OK)
        contentAsString(submit) must equalTo("Submitted tiles and word don't match.")
      }
    }
  }
}