package com.thoughtworks.iot_api

import java.nio.charset.StandardCharsets
import java.util.Base64

import play.api.libs.json.{JsValue, Json}
import scalaj.http.{Http, HttpResponse}

class Api(serverUri: String, httpClient: HttpClient) {
  def getMeetingRoomsInOffice(officeName: String): Option[JsValue] = {
    val httpResponse = httpClient.get(s"$serverUri/api/MeetingRoom/offices/$officeName/rooms")
    getRespBodyOnSuccess(httpResponse)
  }

  private def getRespBodyOnSuccess(httpResponse: HttpResponse[String]): Option[JsValue] =
    if (httpResponse.isSuccess) Some(Json.parse(httpResponse.body)) else None
}

object Api {
  def apply(serverUri: String, httpClient: HttpClient) = new Api(serverUri, httpClient)

}

class HttpClient(authServer: AuthServer, clientId: String, clientSecret: String) {
  def get(url: String): HttpResponse[String] = {
    val accessToken = authServer.accessToken(clientId, clientSecret)
    Http(url)
      .header("Authorization", s"Bearer $accessToken")
      .asString
  }
}

object HttpClient {
  def apply(authServer: AuthServer, clientId: String, clientSecret: String): HttpClient =
    new HttpClient(authServer, clientId, clientSecret)
}

class AuthServer(authServerUrl: String) {
  def accessToken(clientId: String, clientSecret: String): String = {
    // TODO Add cache impl with TTL based on expires_on in the response
    val creds = Base64.getEncoder.encodeToString(s"$clientId:$clientSecret".getBytes(StandardCharsets.UTF_8))
    val resp =
      Http(authServerUrl)
        .postForm(
          Seq("grant_type" -> "client_credentials",
            "scope" -> "api"))
        .header("authorization", s"Basic $creds").asString

    val jsonResp = Json.parse(resp.body)
    jsonResp("access_token").as[String]
  }
}

object AuthServer {
  def apply(authServerUrl: String): AuthServer = new AuthServer(authServerUrl)
}