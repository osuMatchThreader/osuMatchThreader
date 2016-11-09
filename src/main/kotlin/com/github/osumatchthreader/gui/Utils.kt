package com.github.osumatchthreader.gui

import javafx.scene.layout.Pane
import javafx.scene.layout.Region

fun <T: Region> T.fill() = apply { setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE) }
fun <T : Region> T.addTo(pane: Pane): T = apply {
  fill()
  pane.children.add(this)
}