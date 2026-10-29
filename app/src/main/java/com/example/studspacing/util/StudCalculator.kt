package com.example.studspacing.util

import com.example.studspacing.data.model.CalculationResult
import com.example.studspacing.data.model.StudPosition
import com.example.studspacing.data.model.StudTag
import kotlin.math.max

/**
 * Framing layout calculator for carpenters, contractors, and DIY builders.
 *
 * =========================================================================
 * FRAMING MATHEMATICS & LAYOUT EXPLANATION:
 * =========================================================================
 *
 * 1. LEAD STUD (Stud #1):
 *    - In residential & commercial construction, the lead stud sits flush
 *      against the outer start edge of the bottom and top plates.
 *    - Edge mark: 0.0 (where the tape measure hook is seated).
 *    - Centerline: thickness / 2 (e.g., 0.75" for standard 1.5" lumber).
 *
 * 2. INTERMEDIATE "ON-CENTER" (O.C.) STUDS:
 *    - On-center framing measures the distance between the center axis of adjacent studs.
 *    - To ensure sheet goods (standard 48" x 96" drywall sheets or OSB/plywood) break
 *      precisely centered on the face of a 1.5" stud, carpenters mark the edge of the
 *      first field stud back by half the thickness (16" - 3/4" = 15 1/4").
 *    - The centerline for field stud i (1-based index from field studs) is at:
 *      centerPosition = spacing * i.
 *    - The edge mark (where the carpenter draws an 'X' to orient the stud) is at:
 *      edgePosition = max(0.0, centerPosition - (thickness / 2.0)).
 *
 * 3. TERMINAL END STUD:
 *    - A final full stud is required flush against the far end of the wall.
 *    - Edge position: wallLength - thickness (the inside face) or wallLength (outer face).
 *    - Centerline: wallLength - (thickness / 2.0).
 *    - Even if the last on-center stud does not land exactly on the end wall boundary,
 *      framing building codes always require a terminal stud to close the wall cavity
 *      and provide backing for corner drywall fasteners.
 *    - If the last on-center stud falls within lumber thickness of the end stud,
 *      it is either omitted or doubled as a corner backing nailer.
 *
 * =========================================================================
 */
object StudCalculator {

    /**
     * Calculates the required number of studs and generates their layout markings.
     *
     * @param wallLength Total wall length (must be > 0)
     * @param spacing Desired on-center spacing (must be > 0 and <= wallLength)
     * @param thickness Stud thickness (default 1.5 in or 3.8 cm)
     * @param unit Measurement unit string ("in" or "cm")
     * @return [CalculationResult] containing count, breakdown, and list of [StudPosition]s
     */
    fun calculate(
        wallLength: Double,
        spacing: Double,
        thickness: Double,
        unit: String
    ): CalculationResult {
        require(wallLength > 0.0) { "Wall length must be greater than 0" }
        require(spacing > 0.0) { "Spacing must be greater than 0" }
        require(thickness > 0.0) { "Thickness must be greater than 0" }

        val positions = mutableListOf<StudPosition>()
        val halfThickness = thickness / 2.0

        // 1. Lead Stud (Stud 1) - Flush with wall start
        var studIndex = 1
        positions.add(
            StudPosition(
                studNumber = studIndex++,
                centerPosition = halfThickness,
                edgePosition = 0.0,
                tag = StudTag.START
            )
        )

        // 2. Field Studs (placed on-center)
        var currentCenter = spacing
        // Stop before we collide with the end stud's envelope
        val endStudCenterThreshold = wallLength - thickness

        while (currentCenter < endStudCenterThreshold) {
            val edgeMark = max(0.0, currentCenter - halfThickness)
            positions.add(
                StudPosition(
                    studNumber = studIndex++,
                    centerPosition = currentCenter,
                    edgePosition = edgeMark,
                    tag = StudTag.FIELD
                )
            )
            currentCenter += spacing
        }

        // 3. End Stud - Flush with wall end
        val endStudCenter = max(halfThickness, wallLength - halfThickness)
        val endStudEdge = max(0.0, wallLength - thickness)
        positions.add(
            StudPosition(
                studNumber = studIndex++,
                centerPosition = endStudCenter,
                edgePosition = endStudEdge,
                tag = StudTag.END
            )
        )

        return CalculationResult(
            wallLength = wallLength,
            spacing = spacing,
            thickness = thickness,
            unit = unit,
            totalStuds = positions.size,
            studPositions = positions
        )
    }
}
