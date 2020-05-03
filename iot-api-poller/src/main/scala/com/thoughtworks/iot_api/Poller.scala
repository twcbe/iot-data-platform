package com.thoughtworks.iot_api

class Poller(iotApi: Api) {

  def pollAndPublish(): Unit = {
    val cbeRooms = iotApi.getMeetingRoomsInOffice("Coimbatore")
    println(cbeRooms)
  }
}

object Poller {
  def apply(iotApi: Api): Poller = new Poller(iotApi)
}

object PollerApp extends App {
  println("-- Starting poller app --")

  val config = Configuration()
  val authServer = AuthServer(config.authServerUrl())
  val httpClient = HttpClient(authServer, config.clientId(), config.clientSecret())
  val iotApi = Api(config.apiBaseUrl(), httpClient)
  val poller = Poller(iotApi)
  poller.pollAndPublish()
}