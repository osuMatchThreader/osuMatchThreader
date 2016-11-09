package com.github.osumatchthreader.parser

import com.github.osumatchthreader.log.Log
import com.github.osumatchthreader.log.entering
import com.github.osumatchthreader.log.exitWith
import com.github.osumatchthreader.model.MatchScore
import com.github.osumatchthreader.model.Team
import org.jsoup.nodes.Element
import java.math.BigDecimal

class MatchScoreParser(
    val osuUserParser: OsuUserParser
) {
  val log by Log()

  fun parse(e: Element): MatchScore {
    log.entering()
    check(e.attr("class").startsWith("row") && e.tagName() == "tr")

    val columns = e.select("td")
    val slot = columns[0].text().toLong()
    val team = columns[0].attr("style")
        .substringAfter("background:")
        .substringBefore(";")
        .toUpperCase()
        .let { Team.valueOf(it) }

    val scoreString = columns[1].text()
    val score = scoreString.substringBefore(" FAIL").toLongFromScore()
    val failed = scoreString.contains(" FAIL")

    val accuracy = columns[2].text().dropLast(1).let(::BigDecimal)
    val playerElement = columns[3].select("a[href]").first()
    val user = osuUserParser.parse(playerElement)
    val combo = columns[4].text().toLong()
    val num300 = columns[5].text().toLong()
    val num100 = columns[6].text().toLong()
    val num50 = columns[7].text().toLong()
    val misses = columns[8].text().toLong()

    return log.exitWith(MatchScore(team, slot, score, failed, accuracy, user, combo, num300, num100, num50, misses))
  }
}