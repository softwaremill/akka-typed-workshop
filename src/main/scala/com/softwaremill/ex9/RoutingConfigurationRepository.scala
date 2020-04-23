package com.softwaremill.ex9

import java.util.concurrent.ConcurrentHashMap
import scala.jdk.CollectionConverters._

import scala.concurrent.Future

class RoutingConfigurationRepository {
  private val config = new ConcurrentHashMap[String, String]()

  def updateConfig(destination: String, routingDestination: String): Future[Unit] = {
    config.put(destination, routingDestination)
    Future.unit
  }

  def getConfig(): Future[Map[String, String]] = {
    Thread.sleep(1000)
    Future.successful(config.asScala.toMap)
  }
}
