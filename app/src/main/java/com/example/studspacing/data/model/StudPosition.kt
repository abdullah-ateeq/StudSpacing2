package com.example.studspacing.data.model

/**
 * Represents the architectural role of a stud within the framed wall.
 */
enum class StudTag {
    START,  // Leading flush stud at the start of the wall (0 mark)
    FIELD,  // Regular intermediate stud placed at on-center spacing intervals
    END     // Terminal flush stud at the end of the wall
}

/**
 * Represents an individual stud and its measurement positions along the wall.
 *
 * @property studNumber 1-based sequential stud index
 * @property centerPosition The measurement to the centerline of this stud
 * @property edgePosition The measurement to the leading edge (tape measure mark line)
 * @property tag Architectural role of this stud (START, FIELD, END)
 */
data class StudPosition(
    val studNumber: Int,
    val centerPosition: Double,
    val edgePosition: Double,
    val tag: StudTag
)
