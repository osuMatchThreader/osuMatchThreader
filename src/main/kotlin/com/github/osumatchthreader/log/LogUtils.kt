@file:Suppress("NOTHING_TO_INLINE")

package com.github.osumatchthreader.log

import com.github.osumatchthreader.resource
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Log : ReadOnlyProperty<Any, Logger> {
  override fun getValue(thisRef: Any, property: KProperty<*>): Logger =
      Logger.getLogger(thisRef.javaClass.name)
}

inline fun caller(): StackTraceElement = Thread.currentThread().stackTrace[1]

inline fun Logger.entering(vararg params: Any) {
  val source = caller()
  entering(source.className, source.methodName, params)
}

inline fun <T> Logger.exitWith(returnValue: T): T {
  val source = caller()
  exiting(source.className, source.methodName, returnValue)
  return returnValue
}

inline fun setupLogging() {
  LogManager.getLogManager().readConfiguration(resource("logging.properties"))
  val global = Logger.getLogger("").apply {
    info("Log configuration initialized")
  }

  Thread.setDefaultUncaughtExceptionHandler { t, e ->
    global.log(Level.SEVERE, "Uncaught exception on thread " + t.name, e)
  }
}

