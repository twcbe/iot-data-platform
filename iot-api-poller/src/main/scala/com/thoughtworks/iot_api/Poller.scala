package com.thoughtworks.iot_api

import play.api.libs.json.{JsNull, JsValue}

import scala.collection.mutable.ArrayBuffer

class Poller(iotApi: Api) {

  def pollAndPublish(): Unit = {
    val cbeRooms = iotApi.getMeetingRoomsInOffice("Coimbatore")
    val indriyaIds: ArrayBuffer[JsValue] = cbeRooms match {
      case Some(rooms) => (rooms \\ "indriyaId").asInstanceOf[ArrayBuffer[JsValue]]
      case _ => ArrayBuffer()
    }
    indriyaIds.filter(id => id != JsNull)
      .map(id => iotApi.getRoomActivitiesByIndriyaId(id.as[String]))
        .map(_.getOrElse(""))
        .filter(_ != "")
        .foreach(println(_)) //TODO publish to Pulsar topic
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