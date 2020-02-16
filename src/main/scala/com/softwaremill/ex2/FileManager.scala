package com.softwaremill.ex2

import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FileManager extends StrictLogging {

  def startUpload(): Future[Unit] = Future {
    logger.info("File upload started")
    Thread.sleep(2000)
  }
}
