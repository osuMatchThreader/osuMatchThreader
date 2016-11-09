package com.github.osumatchthreader.model

data class TeamScores(
    val blueTeamScore: Long,
    val redTeamScore: Long,
    val winner: Team
) {
  val difference = (blueTeamScore - redTeamScore).let { Math.abs(it) }
}