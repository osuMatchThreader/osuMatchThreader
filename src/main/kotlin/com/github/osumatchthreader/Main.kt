package com.github.osumatchthreader

import com.github.osumatchthreader.gui.setupPrimaryStage
import com.github.osumatchthreader.log.setupLogging
import javafx.application.Application
import javafx.stage.Stage
import java.io.PrintWriter
import java.io.StringWriter

class Main : Application() {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Main::class.java, *args)
    }
  }

  override fun start(primaryStage: Stage) {
    setupLogging()
    primaryStage.setupPrimaryStage()
  }
}

fun Throwable.stackTraceToString() = StringWriter().apply { printStackTrace(PrintWriter(this)) }.toString()
fun resource(name: String) = Main::class.java.classLoader.getResourceAsStream(name)!!
fun resourceAsString(name: String) = resource(name).bufferedReader().readText()