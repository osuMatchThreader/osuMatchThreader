package com.github.osumatchthreader.parser

import com.github.osumatchthreader.log.Log
import com.github.osumatchthreader.log.entering
import com.github.osumatchthreader.log.exitWith
import com.github.osumatchthreader.model.Match
import org.jsoup.nodes.Element

class MatchParser(val beatmapParser: BeatmapParser,
                  val matchScoreParser: MatchScoreParser,
                  val teamScoresParser: TeamScoresParser) {
  val log by Log()

  fun parse(e: Element): Match {
    log.entering()
    check(e.className() == "game")

    val beatmap = e.getElementsByClass("beatmap").first()
        .let { beatmapParser.parse(it) }

    val data = e.select("div > strong").map {
      val oldParent = it.parent()
      it.remove()
      val text = oldParent.text()
      oldParent.appendChild(it)
      text
    }

    val gameName = data[0]
    val duration = data[1]
    val gameMode = data[2]
    val scoringMode = data[3]
    val playerScores = e.select("tr.row1p, tr.row2p").map {
      matchScoreParser.parse(it)
    }
    val teamScores = e.select("div.team_scores").first().let { teamScoresParser.parse(it) }

    return log.exitWith(Match(beatmap, gameName, duration, gameMode, scoringMode, playerScores, teamScores))
  }
}