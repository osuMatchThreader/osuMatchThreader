package com.github.osumatchthreader.parser

import com.github.osumatchthreader.log.Log
import com.github.osumatchthreader.log.entering
import com.github.osumatchthreader.log.exitWith
import com.github.osumatchthreader.model.Beatmap
import com.github.osumatchthreader.model.Track
import org.jsoup.nodes.Element

class BeatmapParser(val osuUserParser: OsuUserParser) {
  val log by Log()

  fun parse(e: Element): Beatmap {
    log.entering()
    check(e.attr("class") == "beatmap")
    val maintext = e.select("div.maintext").first()
    val artist = maintext.select("span.artist").text()
    val eTitle = e.select("a[href].title").first()
    val url = eTitle.absUrl("href")
    val title = eTitle.text()
    val mapper = e.select("div.left-aligned > div > a[href]").first()
        .let { osuUserParser.parse(it) }

    return log.exitWith(Beatmap(url, Track(artist, title), mapper))
  }
}