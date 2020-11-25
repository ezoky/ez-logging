package com.ezoky.ezlogging

/**
 * Java style logging API (logger id is class id).
 *
 * @author gweinbach on 25/11/2020
 * @since 0.1.0
 */
trait EzLoggableClass
  extends EzLoggable {

  @transient
  final override protected lazy val loggerId: String = getClass.getName

}
