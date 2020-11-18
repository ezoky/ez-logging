package com.ezoky.ezlogging

import org.scalatest.matchers.should.Matchers
import org.mockito.ArgumentMatchers._
import org.mockito.MockitoSugar
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.{Logger => Underlying}

/**
 * These tests are adapted from `scala-logging` ([[https://github.com/lightbend/scala-logging]]).
 *
 * @author gweinbach on 14/10/2020
 * @since 0.1.0
 */
class EzLoggableSpec
  extends AnyWordSpec
    with Matchers
    with MockitoSugar {

    "Calling a disabled log" should {
      "not compute log message" in {

        val f = fixture(_.isInfoEnabled, isEnabled = false)
        import f._

        val sleepTimeInMS = 10000L
        val timestampBefore = System.currentTimeMillis()
        logger.info {
          Thread.sleep(sleepTimeInMS)
          "this message will not be logged"
        }
        val timestampAfter = System.currentTimeMillis()

        assert(timestampAfter - timestampBefore < sleepTimeInMS)
      }
    }

    // Error

    "Calling error with a message" should {

      "call the underlying logger's error method if the error level is enabled" in {
        val f = fixture(_.isErrorEnabled, isEnabled = true)
        import f._
        logger.error(msg)
        verify(underlying).error(msg)

        logger.error(msg, cause)
        verify(underlying).error(msg, cause)
      }

      "not call the underlying logger's error method if the error level is not enabled" in {
        val f = fixture(_.isErrorEnabled, isEnabled = false)
        import f._
        logger.error(msg)
        verify(underlying, never).error(anyString)
      }
    }


  // Warn

  "Calling warn with a message" should {

    "call the underlying logger's warn method if the warn level is enabled" in {
      val f = fixture(_.isWarnEnabled, isEnabled = true)
      import f._
      logger.warn(msg)
      verify(underlying).warn(msg)
    }

    "not call the underlying logger's warn method if the warn level is not enabled" in {
      val f = fixture(_.isWarnEnabled, isEnabled = false)
      import f._
      logger.warn(msg)
      verify(underlying, never).warn(anyString)
    }
  }


  // Info

  "Calling info with a message" should {

    "call the underlying logger's info method if the info level is enabled" in {
      val f = fixture(_.isInfoEnabled, isEnabled = true)
      import f._
      logger.info(msg)
      verify(underlying).info(msg)
    }

    "not call the underlying logger's info method if the info level is not enabled" in {
      val f = fixture(_.isInfoEnabled, isEnabled = false)
      import f._
      logger.info(msg)
      verify(underlying, never).info(anyString)
    }
  }


  // Debug

  "Calling debug with a message" should {

    "call the underlying logger's debug method if the debug level is enabled" in {
      val f = fixture(_.isDebugEnabled, isEnabled = true)
      import f._
      logger.debug(msg)
      verify(underlying).debug(msg)
    }

    "not call the underlying logger's debug method if the debug level is not enabled" in {
      val f = fixture(_.isDebugEnabled, isEnabled = false)
      import f._
      logger.debug(msg)
      verify(underlying, never).debug(anyString)
    }
  }


  // Trace

  "Calling trace with a message" should {

    "call the underlying logger's trace method if the trace level is enabled" in {
      val f = fixture(_.isTraceEnabled, isEnabled = true)
      import f._
      logger.trace(msg)
      verify(underlying).trace(msg)
    }

    "not call the underlying logger's trace method if the trace level is not enabled" in {
      val f = fixture(_.isTraceEnabled, isEnabled = false)
      import f._
      logger.trace(msg)
      verify(underlying, never).trace(anyString)
    }
  }


  def fixture(p: Underlying => Boolean, isEnabled: Boolean) =
    new {
      val msg = "msg"
      val cause = new RuntimeException("cause")
      val arg1 = "arg1"
      val arg2 = new Integer(1)
      val arg3 = "arg3"
      val arg4 = 4
      val arg4ref = arg4.asInstanceOf[AnyRef]
      val arg5 = true
      val arg5ref = arg5.asInstanceOf[AnyRef]
      val arg6 = 6L
      val arg6ref = arg6.asInstanceOf[AnyRef]
      val underlying = mock[org.slf4j.Logger]
      when(p(underlying)).thenReturn(isEnabled)
      val logger = EzLoggable(underlying)
    }
  }
