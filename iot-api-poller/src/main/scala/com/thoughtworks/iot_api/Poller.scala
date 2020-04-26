package com.thoughtworks.iot_api

class Poller(iotApi: Api) {

  def pollAndPublish(): Unit = {
    iotApi.getMeetingRoomsInOffice("Coimbatore")
  }
}

object Poller {
  def apply(iotApi: Api): Poller = new Poller(iotApi)
}

object PollerApp extends App {
  println("-- Starting poller app --")
  val iotApi = Api("http://localhost:8080")
  val poller = Poller(iotApi)
  poller.pollAndPublish()
}