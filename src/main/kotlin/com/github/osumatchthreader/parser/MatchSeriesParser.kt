package com.github.osumatchthreader.parser

import com.github.osumatchthreader.log.Log
import com.github.osumatchthreader.log.entering
import com.github.osumatchthreader.log.exitWith
import com.github.osumatchthreader.model.MatchSeries
import org.jsoup.nodes.Element

class MatchSeriesParser(val matchParser: MatchParser) {
  val log by Log()

  companion object {
    fun newInstance(): MatchSeriesParser {
      val osuUserParser = OsuUserParser()
      val beatmapParser = BeatmapParser(osuUserParser)
      val matchScoreParser = MatchScoreParser(osuUserParser)
      val teamScoresParser = TeamScoresParser()
      val matchParser = MatchParser(beatmapParser, matchScoreParser, teamScoresParser)
      return MatchSeriesParser(matchParser)
    }
  }

  fun parse(e: Element): MatchSeries {
    log.entering()
    val header = e.select("h1").first().text()
    val matches = e.select("div.game").map { matchParser.parse(it) }
    return log.exitWith(MatchSeries(header, matches))
  }
}