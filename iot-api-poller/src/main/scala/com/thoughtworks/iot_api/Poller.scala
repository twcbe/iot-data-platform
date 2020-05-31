package com.thoughtworks.iot_api

import org.apache.pulsar.client.api.{PulsarClient, Schema}
import play.api.libs.json.{JsNull, JsValue, _}

import scala.collection.mutable.ArrayBuffer

class Poller(iotApi: Api, config: Configuration) {

  def pollAndPublish(): Unit = {

    val client = PulsarClient.builder()
      .serviceUrl(config.pulsarUrl())
      .build()

    val roomProducer = client.newProducer(Schema.STRING)
      .topic("iot-data/raw/rooms")
      .create()

    val activityProducer = client.newProducer(Schema.STRING)
      .topic("iot-data/raw/room_activities")
      .create()


    val cbeRooms = iotApi.getMeetingRoomsInOffice("Coimbatore")

    val indriyaIds: ArrayBuffer[JsValue] = cbeRooms match {
      case Some(rooms) => {
        roomProducer.send(rooms.toString())
        (rooms \\ "indriyaId").asInstanceOf[ArrayBuffer[JsValue]]
      }
      case _ => ArrayBuffer()
    }
    roomProducer.close()

    indriyaIds.filter(id => id != JsNull)
      .map(id => iotApi.getRoomActivitiesByIndriyaId(id.as[String]))
      .filter(_.isDefined)
      .map(_.get)
      .foreach(json => activityProducer.send(json.toString()))
    activityProducer.close()
    client.close()

  }
}

object Poller {
  def apply(iotApi: Api, config: Configuration): Poller = new Poller(iotApi, config)
}

object PollerApp extends App {
  println("-- Starting poller app --")

  val config = Configuration()
  val authServer = AuthServer(config.authServerUrl())
  val httpClient = HttpClient(authServer, config.clientId(), config.clientSecret())
  val iotApi = Api(config.apiBaseUrl(), httpClient)
  val poller = Poller(iotApi, config)
  poller.pollAndPublish()
}