package com.github.osumatchthreader.model

import java.math.BigDecimal

data class MatchScore(
    val team: Team,
    val slot: Long,
    val score: Long,
    val failed: Boolean,
    val accuracy: BigDecimal,
    val player: OsuUser,
    val combo: Long,
    val num300: Long,
    val num100: Long,
    val num50: Long,
    val misses: Long
)