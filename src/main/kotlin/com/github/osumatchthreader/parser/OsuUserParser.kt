package com.github.osumatchthreader.parser

import com.github.osumatchthreader.log.Log
import com.github.osumatchthreader.log.entering
import com.github.osumatchthreader.log.exitWith
import com.github.osumatchthreader.model.OsuUser
import org.jsoup.nodes.Element

class OsuUserParser {
  val log by Log()

  fun parse(e: Element): OsuUser {
    log.entering()
    val url = e.absUrl("href")
    val name = e.text()
    return log.exitWith(OsuUser(url, name))
  }
}