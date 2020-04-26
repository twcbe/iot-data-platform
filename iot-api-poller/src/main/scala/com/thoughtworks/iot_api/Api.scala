package com.thoughtworks.iot_api

import scalaj.http.{Http, HttpResponse}

class Api(serverUri: String, httpClient: HttpClient) {
  def getMeetingRoomsInOffice(officeName: String): Option[String] = {
    val httpResponse = httpClient.get(s"$serverUri/api/MeetingRoom/offices/$officeName/rooms")
    getRespBodyOnSuccess(httpResponse)
  }

  private def getRespBodyOnSuccess(httpResponse: HttpResponse[String]): Option[String] =
    if (httpResponse.isSuccess) Some(httpResponse.body) else None
}

object Api {
  def apply(serverUri: String, httpClient: HttpClient = HttpClient()) = new Api(serverUri, httpClient)

}

class HttpClient {
  def get(url: String): HttpResponse[String] = Http(url).asString
}

object HttpClient {
  def apply(): HttpClient = new HttpClient()
}