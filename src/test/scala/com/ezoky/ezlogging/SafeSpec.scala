package com.ezoky.ezlogging

import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author gweinbach on 25/11/2020
 * @since 0.1.0
 */
class SafeSpec
  extends AnyWordSpec
    with Matchers
    with MockitoSugar {

  "Trying something that succeeds" should {
    "return a non empty result" in {

      import slf4j._

      val successfulResult = Safe(6.0 / 2)
      assert(successfulResult === Some(3.0))
    }
    "return a Right(value) when asked for Either" in {

      import slf4j._

      val successfulResult = Safe.trial(6.0 / 2)
      assert(successfulResult === Right(3.0))
    }
    "return a Right(value) when given a Right(value)" in {

      import slf4j._

      val successfulEntry: Either[Throwable, Double] = Right(6.0 / 2)
      val successfulResult = Safe.trialFromEither(successfulEntry)
      assert(successfulResult === Right(3.0))
    }

    "result in no logged message" in {

      val (loggerFactory, logger) = Testing.EzLoggableType[Int](_.isInfoEnabled, isEnabled = true)

      val successfulResult = Safe(
        {
          val x = "Hello World !"
          3
        },
        LogLevel.Info,
        showException = false
      )(loggerFactory)

      assert(successfulResult === Some(3))
      verify(logger, never).info(anyString)
    }
  }

  "Trying something that fails" should {

    "return an empty result" in {

      import slf4j._

      val failingResult = Safe(throw new ArrayIndexOutOfBoundsException(10))

      assert(failingResult.isEmpty)
    }
    "return a Left(...) when asked for Either" in {

      import slf4j._

      val indexOutOfRange = 10
      val exception = new ArrayIndexOutOfBoundsException(indexOutOfRange)
      val failingResult = Safe.trial(throw exception)

      val expectedMessage = s"${classOf[ArrayIndexOutOfBoundsException].getName}: Array index out of range: $indexOutOfRange"

      assert(failingResult.isLeft)
      assert(failingResult.left.toOption.get() === expectedMessage)
    }
    "return a Left(message) when given a Left(throwable)" in {

      import slf4j._

      val indexOutOfRange = 10
      val exception = new ArrayIndexOutOfBoundsException(indexOutOfRange)
      val failingEntry: Either[Throwable, Double] = Left(exception)
      val failingResult = Safe.trialFromEither(failingEntry)

      val expectedMessage = s"${classOf[ArrayIndexOutOfBoundsException].getName}: Array index out of range: $indexOutOfRange"

      assert(failingResult.isLeft)
      assert(failingResult.left.toOption.get() === expectedMessage)
    }

    "log an error message if log level is enabled" in {

      val (loggerFactory, logger) = Testing.EzLoggableType[Int](_.isDebugEnabled, isEnabled = true)

      val exceptionMessage = "Something is null"
      val failingResult = Safe(
        throw new NullPointerException(exceptionMessage),
        LogLevel.Debug,
        showException = false,
        messageTemplate = "this message will be logged %s: %s"
      )(loggerFactory)

      val expectedMessage = s"this message will be logged ${classOf[NullPointerException].getName}: $exceptionMessage"

      assert(failingResult.isEmpty)

      verify(logger).debug(expectedMessage)
    }

    "log a (default) error message even if message template is bad" in {

      val (loggerFactory, logger) = Testing.EzLoggableType[Int](_.isTraceEnabled, isEnabled = true)

      val exceptionMessage = "Something is null"
      val failingResult = Safe(
        throw new NullPointerException(exceptionMessage),
        LogLevel.Trace,
        showException = true,
        messageTemplate = "this message has too many string placeholders %s: %s (%s)"
      )(loggerFactory)

      val expectedMessage = s"${classOf[NullPointerException].getName}: $exceptionMessage"

      assert(failingResult.isEmpty)

      verify(logger).trace(expectedMessage)
    }

    "not compute error message if log level is not enabled" in {

      val (loggerFactory, logger) = Testing.EzLoggableType[Int](_.isWarnEnabled, isEnabled = false)

      val sleepTimeInMS = 10000L
      val timestampBefore = System.currentTimeMillis()

      val failingResult = Safe(
        throw new NullPointerException(),
        LogLevel.Warn,
        showException = false,
        messageTemplate = {
          Thread.sleep(sleepTimeInMS)
          "this message will not be logged %s: %s"
        }
      )(loggerFactory)

      val timestampAfter = System.currentTimeMillis()

      assert(failingResult.isEmpty)
      assert(timestampAfter - timestampBefore < sleepTimeInMS)

      verify(logger, never).warn(anyString)
    }
  }

}
