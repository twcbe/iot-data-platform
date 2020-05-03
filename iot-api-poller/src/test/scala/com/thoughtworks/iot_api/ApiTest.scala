package com.thoughtworks.iot_api

import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import scalaj.http.{Http, HttpRequest, HttpResponse}

class ApiTest extends AnyFeatureSpec with Matchers with GivenWhenThen with MockFactory {
  Feature("TW IoT platform API") {
    Scenario("should get response for the given URL") {
      Given("API instance with server URI")
      val mockHttpClient = mock[HttpClient]

      val iotApi = Api("http://localhost:8080", mockHttpClient)

      When("Get called on API instance for a resource and it receives success response")
      mockHttpClient.get _ expects "http://localhost:8080/api/MeetingRoom/offices/Coimbatore/rooms" returning
        HttpResponse("{\"rooms\":[{\"room_id\":1, \"room_name\": \"Kovai\"}]}", 200, Map())
      val response: Option[JsValue] = iotApi.getMeetingRoomsInOffice("Coimbatore")

      Then("should return response of that resource")
      response shouldBe Some(Json.parse("{\"rooms\":[{\"room_id\":1, \"room_name\": \"Kovai\"}]}"))
    }

    Scenario("should return None when the response code is non 2xx") {
      Given("API instance with server URI")
      val mockHttpClient = mock[HttpClient]

      val iotApi = Api("http://localhost:8080", mockHttpClient)

      When("Get called on API instance for a resource and it returns 4xx")
      mockHttpClient.get _ expects "http://localhost:8080/api/MeetingRoom/offices/Asgard/rooms" returning
        HttpResponse("", 404, Map())
      val response: Option[JsValue] = iotApi.getMeetingRoomsInOffice("Asgard")

      Then("it should give the response as None")
      response shouldBe None
    }
  }
}
