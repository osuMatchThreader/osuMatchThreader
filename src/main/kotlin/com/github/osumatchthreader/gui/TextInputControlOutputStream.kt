package com.github.osumatchthreader.gui

import javafx.application.Platform
import javafx.scene.control.TextInputControl
import java.io.OutputStream

class TextInputControlOutputStream(private val textInputControl: TextInputControl) : OutputStream() {
  override fun write(b: Int) = Platform.runLater {
    textInputControl.appendText(b.toChar().toString())
  }
}