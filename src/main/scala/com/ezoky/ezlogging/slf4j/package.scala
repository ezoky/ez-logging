package com.ezoky.ezlogging

/**
 * @author gweinbach on 27/12/2020
 * @since 0.2.0
 */
package object slf4j {

  implicit val Slf4JLoggerFactory: EzLoggerFactory =
    EzLoggerFactory.Slf4J
}
