package com.ezoky.ezlogging

import org.scalatest.matchers.should.Matchers
import org.mockito.ArgumentMatchers._
import org.mockito.MockitoSugar
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author gweinbach on 25/11/2020
 * @since 0.1.0
 */
class LogSpec
  extends AnyWordSpec
    with Matchers
    with MockitoSugar {
  
  "Log" should {

    "log a message of the right level when asked if this level is enabled" in {

      val (traceLevel, traceLogger) = Testing.Log(LogLevel.Trace, _.isTraceEnabled, true)
      val expectedTraceMessage = "A Trace Message"

      traceLevel.log(expectedTraceMessage)

      verify(traceLogger).trace(expectedTraceMessage)
      verify(traceLogger, never).debug(anyString)
      verify(traceLogger, never).info(anyString)
      verify(traceLogger, never).warn(anyString)
      verify(traceLogger, never).error(anyString)


      val (debugLevel, debugLogger) = Testing.Log(LogLevel.Debug, _.isDebugEnabled, true)
      val expectedDebugMessage = "A Debug Message"

      debugLevel.log(expectedDebugMessage)

      verify(debugLogger, never).trace(anyString)
      verify(debugLogger).debug(expectedDebugMessage)
      verify(debugLogger, never).info(anyString)
      verify(debugLogger, never).warn(anyString)
      verify(debugLogger, never).error(anyString)


      val (infoLevel, infoLogger) = Testing.Log(LogLevel.Info, _.isInfoEnabled, true)
      val expectedInfoMessage = "A Info Message"

      infoLevel.log(expectedInfoMessage)

      verify(infoLogger, never).trace(anyString)
      verify(infoLogger, never).debug(anyString)
      verify(infoLogger).info(expectedInfoMessage)
      verify(infoLogger, never).warn(anyString)
      verify(infoLogger, never).error(anyString)


      val (warnLevel, warnLogger) = Testing.Log(LogLevel.Warn, _.isWarnEnabled, true)
      val expectedWarnMessage = "A Warn Message"

      warnLevel.log(expectedWarnMessage)

      verify(warnLogger, never).trace(anyString)
      verify(warnLogger, never).debug(anyString)
      verify(warnLogger, never).info(anyString)
      verify(warnLogger).warn(expectedWarnMessage)
      verify(warnLogger, never).error(anyString)


      val (errorLevel, errorLogger) = Testing.Log(LogLevel.Error, _.isErrorEnabled, true)
      val expectedErrorMessage = "A Error Message"

      errorLevel.log(expectedErrorMessage)

      verify(errorLogger, never).trace(anyString)
      verify(errorLogger, never).debug(anyString)
      verify(errorLogger, never).info(anyString)
      verify(errorLogger, never).warn(anyString)
      verify(errorLogger).error(expectedErrorMessage)
    }


    "log nothing when asked if this level is disabled" in {

      val (traceLevel, traceLogger) = Testing.Log(LogLevel.Trace, _.isTraceEnabled, false)
      val expectedTraceMessage = "A Trace Message"

      traceLevel.log(expectedTraceMessage)

      verify(traceLogger, never).trace(anyString)
      verify(traceLogger, never).debug(anyString)
      verify(traceLogger, never).info(anyString)
      verify(traceLogger, never).warn(anyString)
      verify(traceLogger, never).error(anyString)


      val (debugLevel, debugLogger) = Testing.Log(LogLevel.Debug, _.isDebugEnabled, false)
      val expectedDebugMessage = "A Debug Message"

      debugLevel.log(expectedDebugMessage)

      verify(debugLogger, never).trace(anyString)
      verify(debugLogger, never).debug(anyString)
      verify(debugLogger, never).info(anyString)
      verify(debugLogger, never).warn(anyString)
      verify(debugLogger, never).error(anyString)


      val (infoLevel, infoLogger) = Testing.Log(LogLevel.Info, _.isInfoEnabled, false)
      val expectedInfoMessage = "A Info Message"

      infoLevel.log(expectedInfoMessage)

      verify(infoLogger, never).trace(anyString)
      verify(infoLogger, never).debug(anyString)
      verify(infoLogger, never).info(anyString)
      verify(infoLogger, never).warn(anyString)
      verify(infoLogger, never).error(anyString)


      val (warnLevel, warnLogger) = Testing.Log(LogLevel.Warn, _.isWarnEnabled, false)
      val expectedWarnMessage = "A Warn Message"

      warnLevel.log(expectedWarnMessage)

      verify(warnLogger, never).trace(anyString)
      verify(warnLogger, never).debug(anyString)
      verify(warnLogger, never).info(anyString)
      verify(warnLogger, never).warn(anyString)
      verify(warnLogger, never).error(anyString)


      val (errorLevel, errorLogger) = Testing.Log(LogLevel.Error, _.isErrorEnabled, false)
      val expectedErrorMessage = "A Error Message"

      errorLevel.log(expectedErrorMessage)

      verify(errorLogger, never).trace(anyString)
      verify(errorLogger, never).debug(anyString)
      verify(errorLogger, never).info(anyString)
      verify(errorLogger, never).warn(anyString)
      verify(errorLogger, never).error(anyString)
    }
  }


  "A Log" should {
    "have its log method enabled depending on log config (here logback.xml)" in {

      import slf4j._

      assert(Log.Error[String]().isEnabled)
      assert(Log.Warn[String]().isEnabled)
      assert(Log.Info[String]().isEnabled)
      assert(!Log.Debug[String]().isEnabled)
      assert(!Log.Trace[String]().isEnabled)
      assert(Log.NoLog.isEnabled)
    }
  }

  "A Log" can {
    "be built from a string representation of the loglevel" in {

      import slf4j._

      assert(Log.parse[Int]("erRor ").isDefined)
      assert(Log.parse[Int]("  Warn ").isDefined)
      assert(Log.parse[Int]("INFO").isDefined)
      assert(Log.parse[Int]("Debug").isDefined)
      assert(Log.parse[Int]("\t trace").isDefined)
      assert(Log.parse[Int]("NoLog").isDefined)

      assert(Log.parse[Int]("error:").isEmpty)
      assert(Log.parse[Int]("warning").isEmpty)
    }
  }

}
