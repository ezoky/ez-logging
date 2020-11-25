package com.ezoky.ezlogging

import org.scalatest.matchers.should.Matchers
import org.mockito.ArgumentMatchers._
import org.mockito.MockitoSugar
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author gweinbach on 25/11/2020
 * @since 0.1.0
 */
class LogLevelSpec
  extends AnyWordSpec
    with Matchers
    with MockitoSugar {
  
  "LogLevel" should {

    "log a message of the right level when asked if this level is enabled" in {

      val (traceLevel, traceLogger) = Testing.LogLevel(_.trace, _.isTraceEnabled, true)
      val expectedTraceMessage = "A Trace Message"

      traceLevel.log(expectedTraceMessage)

      verify(traceLogger).trace(expectedTraceMessage)


      val (debugLevel, debugLogger) = Testing.LogLevel(_.debug, _.isDebugEnabled, true)
      val expectedDebugMessage = "A Debug Message"

      debugLevel.log(expectedDebugMessage)

      verify(debugLogger).debug(expectedDebugMessage)


      val (infoLevel, infoLogger) = Testing.LogLevel(_.info, _.isInfoEnabled, true)
      val expectedInfoMessage = "A Info Message"

      infoLevel.log(expectedInfoMessage)

      verify(infoLogger).info(expectedInfoMessage)


      val (warnLevel, warnLogger) = Testing.LogLevel(_.warn, _.isWarnEnabled, true)
      val expectedWarnMessage = "A Warn Message"

      warnLevel.log(expectedWarnMessage)

      verify(warnLogger).warn(expectedWarnMessage)


      val (errorLevel, errorLogger) = Testing.LogLevel(_.error, _.isErrorEnabled, true)
      val expectedErrorMessage = "A Error Message"

      errorLevel.log(expectedErrorMessage)

      verify(errorLogger).error(expectedErrorMessage)
    }


    "log nothing when asked if this level is disabled" in {

      val (traceLevel, traceLogger) = Testing.LogLevel(_.trace, _.isTraceEnabled, false)
      val expectedTraceMessage = "A Trace Message"

      traceLevel.log(expectedTraceMessage)

      verify(traceLogger, never).trace(anyString)


      val (debugLevel, debugLogger) = Testing.LogLevel(_.debug, _.isDebugEnabled, false)
      val expectedDebugMessage = "A Debug Message"

      debugLevel.log(expectedDebugMessage)

      verify(debugLogger, never).debug(anyString)


      val (infoLevel, infoLogger) = Testing.LogLevel(_.info, _.isInfoEnabled, false)
      val expectedInfoMessage = "A Info Message"

      infoLevel.log(expectedInfoMessage)

      verify(infoLogger, never).info(anyString)


      val (warnLevel, warnLogger) = Testing.LogLevel(_.warn, _.isWarnEnabled, false)
      val expectedWarnMessage = "A Warn Message"

      warnLevel.log(expectedWarnMessage)

      verify(warnLogger, never).warn(anyString)


      val (errorLevel, errorLogger) = Testing.LogLevel(_.error, _.isErrorEnabled, false)
      val expectedErrorMessage = "A Error Message"

      errorLevel.log(expectedErrorMessage)

      verify(errorLogger, never).error(anyString)
    }
  }


  "A LogLevel" should {
    "have its log method enabled depending on log config (here logback.xml)" in {

      assert(LogLevel.Error[String]().isEnabled)
      assert(LogLevel.Warn[String]().isEnabled)
      assert(LogLevel.Info[String]().isEnabled)
      assert(!LogLevel.Debug[String]().isEnabled)
      assert(!LogLevel.Trace[String]().isEnabled)
      assert(LogLevel.NoLog.isEnabled)
    }
  }

  "A LogLevel" can {
    "be built from a string representation of the loglevel" in {

      assert(LogLevel.parse[Int]("erRor ").isDefined)
      assert(LogLevel.parse[Int]("  Warn ").isDefined)
      assert(LogLevel.parse[Int]("INFO").isDefined)
      assert(LogLevel.parse[Int]("Debug").isDefined)
      assert(LogLevel.parse[Int]("\t trace").isDefined)
      assert(LogLevel.parse[Int]("NoLog").isDefined)

      assert(LogLevel.parse[Int]("error:").isEmpty)
      assert(LogLevel.parse[Int]("warning").isEmpty)
    }
  }

}
