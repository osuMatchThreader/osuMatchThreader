package com.github.osumatchthreader.model

data class Track(
    val artist: String,
    val title: String
) {
  override fun toString(): String = "$artist - $title"
}