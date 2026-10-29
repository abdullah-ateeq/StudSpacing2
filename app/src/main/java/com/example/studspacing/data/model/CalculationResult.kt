package com.example.studspacing.data.model

import java.io.Serializable

/**
 * Encapsulates the complete result of a stud spacing calculation.
 *
 * @property wallLength Total length of the wall being framed
 * @property spacing Desired on-center (O.C.) spacing between studs
 * @property thickness Thickness/width of the framing lumber (e.g. 1.5 in for 2x4/2x6)
 * @property unit Unit symbol ("in" or "cm")
 * @property totalStuds Total count of studs required (including start and end studs)
 * @property studPositions List of stud positions along the length of the wall
 * @property timestamp Epoch timestamp when calculation was created
 */
data class CalculationResult(
    val wallLength: Double,
    val spacing: Double,
    val thickness: Double,
    val unit: String,
    val totalStuds: Int,
    val studPositions: List<StudPosition>,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
