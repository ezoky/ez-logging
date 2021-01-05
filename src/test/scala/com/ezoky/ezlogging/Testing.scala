package com.ezoky.ezlogging

import org.mockito.MockitoSugar

import scala.reflect.runtime.universe.TypeTag

/**
 * @author gweinbach on 25/11/2020
 * @since 0.1.0
 */
object Testing
  extends MockitoSugar {

  def loggerFactoryFixture(isSomethingEnabled: EzLogger => Boolean,
                           isEnabled: Boolean): EzLoggerFactory = {

    val underlying: EzLogger = mock[EzLogger]
    when(isSomethingEnabled(underlying)).thenReturn(isEnabled)

    new EzLoggerFactory {
      override def buildLogger(loggerId: String): EzLogger = {
        when(underlying.id).thenReturn(loggerId)
        underlying
      }
    }
  }



  object EzLoggable {

//    def apply(id: String): EzLoggable =
//      new EzLoggable {
//        override protected lazy val loggerId: String = id
//      }

    def apply(id: String,
              factory: EzLoggerFactory): EzLoggable =

      new EzLoggable {
        override protected val loggerId: String = id
        override implicit protected val loggerFactory: EzLoggerFactory = factory
      }

    def apply(loggerId: String,
              isSomethingEnabled: EzLogger => Boolean,
              isEnabled: Boolean): EzLoggable = {
      val fixture = loggerFactoryFixture(isSomethingEnabled, isEnabled)
      Testing.EzLoggable(loggerId, fixture)
    }
  }

  object EzLoggableType {

    def apply[T: TypeTag](isSomethingEnabled: EzLogger => Boolean,
                          isEnabled: Boolean): (EzLoggerFactory, EzLogger) = {
      implicit val ezLoggerFactory = loggerFactoryFixture(isSomethingEnabled, isEnabled)
      val loggableFixture = new EzLoggableType[T]
      (ezLoggerFactory, loggableFixture.logger)
    }
  }

  object Log {

    def apply[T: TypeTag](logLevel: LogLevel,
                          isSomethingEnabled: EzLogger => Boolean,
                          isLogLevelEnabled: Boolean): (Log, EzLogger) = {
      implicit val fixture = loggerFactoryFixture(isSomethingEnabled, isLogLevelEnabled)
      val log = logLevel.buildLog[T]
      val loggableFixture = new EzLoggableType[T]
      (log, loggableFixture.logger)
    }
  }
}
