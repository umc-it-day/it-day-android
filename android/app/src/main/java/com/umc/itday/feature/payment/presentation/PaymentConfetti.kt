@file:Suppress("MagicNumber")

package com.umc.itday.feature.payment.presentation

import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

internal fun celebrationParties(): List<Party> =
    listOf(
        confettiParty(position = Position.Relative(0.05, 0.05), angle = 45),
        confettiParty(position = Position.Relative(0.95, 0.05), angle = 135),
    )

private fun confettiParty(
    position: Position,
    angle: Int,
): Party =
    Party(
        speed = 4f,
        maxSpeed = 24f,
        damping = 0.9f,
        angle = angle,
        spread = 55,
        colors =
            listOf(
                0xFF3677F9.toInt(),
                0xFFFE3B30.toInt(),
                0xFFFFCC00.toInt(),
                0xFF36E330.toInt(),
                0xFFAF52DE.toInt(),
            ),
        position = position,
        emitter = Emitter(duration = 600, TimeUnit.MILLISECONDS).max(40),
    )

internal const val CONFETTI_VISIBLE_MILLIS = 2_500L
