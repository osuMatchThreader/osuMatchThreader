package com.github.osumatchthreader.parser

import com.github.osumatchthreader.log.Log
import com.github.osumatchthreader.log.entering
import com.github.osumatchthreader.log.exitWith
import com.github.osumatchthreader.model.Team
import com.github.osumatchthreader.model.TeamScores
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class TeamScoresParser {
  val log by Log()

  fun parse(e: Element): TeamScores {
    log.entering()
    fun Node.team() = this.attr("style").substringAfter("color:")
        .toUpperCase().let { Team.valueOf(it) }

    val scores = e.select("span[style]").associate {
      val team = it.team()
      val score = it.text().toLongFromScore()
      Pair(team, score)
    }

    val winner = e.select("b[style]").first().team()
    return log.exitWith(TeamScores(scores[Team.BLUE]!!, scores[Team.RED]!!, winner))
  }
}