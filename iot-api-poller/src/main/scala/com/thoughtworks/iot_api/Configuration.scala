package com.thoughtworks.iot_api

class Configuration {

  def authServerUrl(): String = {
    envOrDefault("AUTH_SERVER_URL", "http://localhost:8080/auth/token")
  }

  def clientId(): String = {
    envOrDefault("CLIENT_ID", "DE_MINI_PROJECT")
  }

  def clientSecret(): String = {
    envOrDefault("CLIENT_SECRET", "SECRET")
  }

  def apiBaseUrl(): String = {
    envOrDefault("API_BASE_URL", "http://localhost:8080")
  }

  def pulsarUrl(): String = {
    envOrDefault("PULSAR_URL", "pulsar://localhost:6650")
  }

  private def envOrDefault(key: String, default: String): String = {
    getenv(key).getOrElse(default)
  }

  private def getenv(key: String): Option[String] = {
    System.getenv(key) match {
      case null => None
      case other => Some(other)
    }
  }
}

object Configuration {
  def apply(): Configuration = new Configuration()
}
