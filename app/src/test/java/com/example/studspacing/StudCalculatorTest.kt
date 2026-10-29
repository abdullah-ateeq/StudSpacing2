package com.example.studspacing

import com.example.studspacing.data.model.StudTag
import com.example.studspacing.util.StudCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudCalculatorTest {

    @Test
    fun testStandard120InchWallAt16OnCenter() {
        val wallLength = 120.0
        val spacing = 16.0
        val thickness = 1.5
        val unit = "in"

        val result = StudCalculator.calculate(wallLength, spacing, thickness, unit)

        // Expected studs:
        // Stud 1: START (edge 0.0, center 0.75)
        // Stud 2: FIELD (edge 15.25, center 16.0)
        // Stud 3: FIELD (edge 31.25, center 32.0)
        // Stud 4: FIELD (edge 47.25, center 48.0)
        // Stud 5: FIELD (edge 63.25, center 64.0)
        // Stud 6: FIELD (edge 79.25, center 80.0)
        // Stud 7: FIELD (edge 95.25, center 96.0)
        // Stud 8: FIELD (edge 111.25, center 112.0)
        // Stud 9: END   (edge 118.5, center 119.25)
        // Total = 9 studs

        assertEquals(9, result.totalStuds)
        assertEquals(9, result.studPositions.size)

        // Verify lead stud
        val leadStud = result.studPositions.first()
        assertEquals(1, leadStud.studNumber)
        assertEquals(StudTag.START, leadStud.tag)
        assertEquals(0.0, leadStud.edgePosition, 0.001)
        assertEquals(0.75, leadStud.centerPosition, 0.001)

        // Verify intermediate drywall break (48")
        val stud48 = result.studPositions.find { it.centerPosition == 48.0 }
        assertTrue("Stud at 48\" center should exist for drywall break", stud48 != null)
        assertEquals(47.25, stud48!!.edgePosition, 0.001)
        assertEquals(StudTag.FIELD, stud48.tag)

        // Verify end stud
        val endStud = result.studPositions.last()
        assertEquals(9, endStud.studNumber)
        assertEquals(StudTag.END, endStud.tag)
        assertEquals(118.5, endStud.edgePosition, 0.001)
        assertEquals(119.25, endStud.centerPosition, 0.001)
    }

    @Test
    fun testMetricWallAt40CmOnCenter() {
        val wallLength = 240.0
        val spacing = 40.0
        val thickness = 3.8
        val unit = "cm"

        val result = StudCalculator.calculate(wallLength, spacing, thickness, unit)

        // Stud 1 (0cm), Stud 2 (40cm), Stud 3 (80cm), Stud 4 (120cm),
        // Stud 5 (160cm), Stud 6 (200cm), Stud 7 (240cm flush end)
        assertEquals(7, result.totalStuds)
        assertEquals(StudTag.START, result.studPositions.first().tag)
        assertEquals(StudTag.END, result.studPositions.last().tag)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidWallLengthThrowsException() {
        StudCalculator.calculate(0.0, 16.0, 1.5, "in")
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidSpacingThrowsException() {
        StudCalculator.calculate(120.0, 0.0, 1.5, "in")
    }
}
