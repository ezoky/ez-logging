/*
 * Copyright (c) 2020 - EZOKY
 */

package com.ezoky.ezlogging

import org.mockito.MockitoSugar
import org.scalatest.wordspec.AnyWordSpec

/**
 * @author gweinbach on 14/10/2020
 * @since 0.1.0
 */
class EzLoggableTypeSpec
  extends AnyWordSpec
    with MockitoSugar {

  import slf4j._
  import EzLoggableType._

  class A {
    this.info("info is available on any object")
  }

  "A logger defined for a type" should {
    "have the name of the type" in {

      import scala.reflect.runtime.universe.typeOf

      val ezLogger = new EzLoggableType[A]

      val slf4JLogger = ezLogger.logger
      assert(slf4JLogger.id == typeOf[A].typeSymbol.fullName)
    }
  }

  "Every object" should {
    "be loggable" in {

      assertCompiles(
        """
        "Toto".debug("A debug message")
        1.trace("Even an int can use trace")
        true.warn("A boolean can warn")
        new A {}
      """)

      // Just to please test coverage
      "Toto".debug("A debug message")
      1.trace("Even an int can use trace")
      true.warn("A boolean can warn")
      new A {}

      assertDoesNotCompile(
        """
        val x = new {
          this.debug("not in the constructor of an anonymous class (why ?)")
        }
      """)
    }
  }
}
