package com.github.osumatchthreader.model

data class Match(
    val beatmap: Beatmap,
    val gameName: String,
    val duration: String,
    val gameMode: String,
    val scoringMode: String,
    val playerScores: List<MatchScore>,
    val teamScores: TeamScores
)