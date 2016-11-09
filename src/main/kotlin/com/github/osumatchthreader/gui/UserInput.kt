package com.github.osumatchthreader.gui

data class UserInput(
    val warmupMatchCount: Long,
    val blueTeamName: String,
    val redTeamName: String,
    val mpUrl: String,
    val sortMode: SortMode,
    val header: String,
    val footer: String
)

enum class SortMode {
  SLOTS,
  SCORE,
  TEAM_THEN_SLOTS,
  TEAM_THEN_SCORE
}