package com.thoughtworks.iot_api

import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import scalaj.http.HttpResponse

class ApiTest extends AnyFeatureSpec with Matchers with GivenWhenThen with MockFactory {
  Feature("TW IoT platform API") {
    Scenario("should get meeting rooms for given office") {
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

    Scenario("should return None when rooms not found for given office name") {
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

    Scenario("should get room activities by indriya Id") {
      Given("API instance with server URL")
      val mockHttpClient = mock[HttpClient]

      val iotApi = Api("http://localhost:8080", mockHttpClient)


      When("Get called on API instance for a resource and it receives success response")

      val mockResponse = "[{\"activityType\":\"Motion\",\"timeStamp\":\"2020-03-23T05:51:16.064+00:00\",\"data\":" +
        "{\"MotionStatus\":1}},{\"activityType\":\"Temperature\",\"timeStamp\":\"2020-03-23T05:51:13.203+00:00\"," +
        "\"data\":{\"Temperature\":31.0}}]"

      mockHttpClient.get _ expects "http://localhost:8080/api/RoomActivity/activities/indriya/unique-id" returning
        HttpResponse(mockResponse, 200, Map())
      val response: Option[JsValue] = iotApi.getRoomActivitiesByIndriyaId("unique-id")

      Then("should return response of that resource")
      response shouldBe Some(
        Json.obj("indriyaId" -> "unique-id",
          "activities" -> Json.arr(
            Json.obj("activityType" -> "Motion", "timeStamp" -> "2020-03-23T05:51:16.064+00:00",
              "data" -> Json.obj("MotionStatus" -> 1)),
            Json.obj("activityType" -> "Temperature", "timeStamp" -> "2020-03-23T05:51:13.203+00:00",
              "data" -> Json.obj("Temperature" -> 31.0)))))
    }

    Scenario("should return None when activities not available for indriya ID") {
      Given("API instance with server URI")
      val mockHttpClient = mock[HttpClient]

      val iotApi = Api("http://localhost:8080", mockHttpClient)

      When("Get called on API instance to get room activities with unknown id and it returns 4xx")
      mockHttpClient.get _ expects "http://localhost:8080/api/RoomActivity/activities/indriya/unknown-id" returning
        HttpResponse("", 404, Map())
      val response: Option[JsValue] = iotApi.getRoomActivitiesByIndriyaId("unknown-id")

      Then("it should give the response as None")
      response shouldBe None
    }
  }
}
