import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.http.MediaType


@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends PlaySpec with OneAppPerSuite {

  val CONTENT_TYPE = "application/json";
  
  "Application" should {

    "send 404 on a bad request" in {
      route(FakeRequest(GET, "/boum")) mustBe None
    }

    "render the home document page" in {
      val home = route(FakeRequest(GET, "/").withHeaders(("Accept" -> CONTENT_TYPE))).get

      status(home) mustEqual(OK)
      contentType(home) mustBe Some(CONTENT_TYPE)
      
      val json = contentAsJson(home)
      
    }
  }
}
