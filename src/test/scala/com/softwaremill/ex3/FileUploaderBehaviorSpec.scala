package com.softwaremill.ex3

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.softwaremill.ex2.FileManager
import com.softwaremill.ex3.FileUploaderBehavior.{GetStatus, Response, UploadFile}
import org.scalatest.flatspec.AnyFlatSpecLike

class FileUploaderBehaviorSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike {
  behavior of "FileUploader ex 3"

  private val fileManager = new FileManager

  it should "respond after uploading file" in {
    // given
    val uploader = testKit.spawn(FileUploaderBehavior(fileManager))
    val probe    = createTestProbe[Response]()

    // when
    uploader ! UploadFile(probe.ref)

    // then
    probe.expectMessageType[FileUploaderBehavior.FileUploaded.type]
  }

  it should "get status while uploading" in {
    // given
    val uploader = testKit.spawn(FileUploaderBehavior(fileManager))
    val probe    = createTestProbe[Response]()
    uploader ! UploadFile(probe.ref)

    // when
    uploader ! GetStatus(probe.ref)

    // then
    probe.expectMessageType[FileUploaderBehavior.UploadingInProgress.type]
  }

  it should "get status when not started" in {
    // given
    val uploader = testKit.spawn(FileUploaderBehavior(fileManager))
    val probe    = createTestProbe[Response]()

    // when
    uploader ! GetStatus(probe.ref)

    // then
    probe.expectMessageType[FileUploaderBehavior.UploadingNotStarted.type]
  }

  it should "not start only one upload at the same time" in {
    // given
    val uploader = testKit.spawn(FileUploaderBehavior(fileManager))
    val probe    = createTestProbe[Response]()

    // when
    uploader ! UploadFile(probe.ref)
    uploader ! UploadFile(probe.ref)

    // then
    probe.expectMessageType[FileUploaderBehavior.UploadingInProgress.type]
    probe.expectMessageType[FileUploaderBehavior.FileUploaded.type]
    probe.expectNoMessage()
  }

  it should "get status after file is uploaded" in {
    // given
    val uploader    = testKit.spawn(FileUploaderBehavior(fileManager))
    val probe       = createTestProbe[Response]()
    val statusProbe = createTestProbe[Response]()
    uploader ! UploadFile(probe.ref)

    eventually {
      // when
      uploader ! GetStatus(statusProbe.ref)

      // then
      statusProbe.expectMessageType[FileUploaderBehavior.FileUploaded.type]
    }
  }

  it should "start next upload when the first is done" in {
    // given
    val uploader = testKit.spawn(FileUploaderBehavior(fileManager))
    val probe    = createTestProbe[Response]()

    // when
    uploader ! UploadFile(probe.ref)

    // then
    probe.expectMessageType[FileUploaderBehavior.FileUploaded.type]

    // when
    uploader ! UploadFile(probe.ref)

    // then
    probe.expectMessageType[FileUploaderBehavior.FileUploaded.type]
  }
}
