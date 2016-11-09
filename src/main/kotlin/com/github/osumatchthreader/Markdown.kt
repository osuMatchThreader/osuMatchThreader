package com.github.osumatchthreader

import com.github.osumatchthreader.gui.SortMode
import com.github.osumatchthreader.gui.UserInput
import com.github.osumatchthreader.model.*
import java.io.StringWriter
import java.io.Writer

val LINE_SEPERATOR = StringWriter().apply { appendln() }.toString()
private val LINE_BREAK = LINE_SEPERATOR.repeat(2)
private val TABLE_HEADER = listOf(
    "Team", "Slot", "Score", "Accuracy", "Player", "Combo", "300s", "100s", "50s", "Misses")
private val TABLE_LINE_FORMAT = Array(TABLE_HEADER.size) { " %s " }.joinToString(" | ")
private val TABLE_LINE_HEADER = StringWriter().let {
  it.appendln(TABLE_LINE_FORMAT.format(*TABLE_HEADER.toTypedArray()))
  it.append(TABLE_LINE_FORMAT.format(*Array(TABLE_HEADER.size) { "---" }))
}.toString()

private fun Long.formatAsScore() = "%,d".format(this)
private fun Writer.lineBreak() = append(LINE_BREAK)
private fun Writer.spacer() = apply {
  lineBreak()
  appendln("&nbsp;")
  lineBreak()
}

private fun Writer.lineSeperator() = apply {
  spacer()
  appendln("---")
  spacer()
}

fun OsuUser.toMarkdown() = "[$name]($url)"
fun Beatmap.toMarkdown() = "[$track]($url) (mapped by: ${mapper.toMarkdown()})"

fun MatchScore.toMarkdown(userInput: UserInput): String = TABLE_LINE_FORMAT.format(
    when (team) {
      Team.BLUE -> userInput.blueTeamName
      Team.RED -> userInput.redTeamName
    }, slot, "${score.formatAsScore()}${if (failed) " FAIL" else ""}", "$accuracy%", player.toMarkdown(),
    combo, num300, num100, num50, misses)

fun TeamScores.toMarkdown(userInput: UserInput): String {
  val bScore = blueTeamScore.formatAsScore()
  val rScore = redTeamScore.formatAsScore()
  val winner = when (winner) {
    Team.BLUE -> userInput.blueTeamName
    Team.RED -> userInput.redTeamName
  }

  val _out = StringWriter()
  _out.appendln("*${userInput.blueTeamName}* $bScore - $rScore *${userInput.redTeamName}*  ")
  _out.appendln("**$winner** wins by **${difference.formatAsScore()}**!")
  return _out.toString()
}

fun Match.toMarkdown(userInput: UserInput): String {
  val _out = StringWriter()
  _out.appendln(teamScores.toMarkdown(userInput))
  _out.spacer()
  _out.appendln("**Beatmap:** ${beatmap.toMarkdown()}  ")
  _out.appendln("**Game Mode:** $gameMode  ")
  _out.spacer()
  _out.appendln(TABLE_LINE_HEADER)

  playerScores.sortedByDescending { it ->
    when (userInput.sortMode) {
      SortMode.SLOTS -> -it.slot
      SortMode.SCORE -> it.score
      SortMode.TEAM_THEN_SLOTS ->
        if (it.team == teamScores.winner) Integer.MAX_VALUE - it.slot
        else -it.slot
      SortMode.TEAM_THEN_SCORE ->
        if (it.team == teamScores.winner) it.score
        else Integer.MIN_VALUE + it.score
    }
  }.map { it.toMarkdown(userInput) }.forEach { _out.appendln(it) }
  return _out.toString()
}

fun MatchSeries.toMarkdown(userInput: UserInput): String {
  val _body = StringWriter()
  var blueWins = 0L
  var redWins = 0L

  matches.forEachIndexed { i, match ->
    var matchNum = i + 1L
    val isWarmup: Boolean = matchNum <= userInput.warmupMatchCount
    if (!isWarmup) {
      matchNum -= userInput.warmupMatchCount
      when (match.teamScores.winner) {
        Team.BLUE -> blueWins++
        Team.RED -> redWins++
      }
    }

    _body.lineSeperator()
    _body.appendln("#${if (isWarmup) "WARMUP" else "MATCH"} $matchNum")
    _body.appendln(match.toMarkdown(userInput))
  }

  _body.lineSeperator()

  val _header = StringWriter()
  _header.lineSeperator()
  _header.appendln("#${userInput.blueTeamName} $blueWins - $redWins ${userInput.redTeamName}")

  return "$_header$_body"
}