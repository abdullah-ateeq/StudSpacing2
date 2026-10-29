package com.example.studspacing.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.studspacing.R
import com.example.studspacing.databinding.FragmentHomeBinding
import com.example.studspacing.ui.viewmodel.StudSpacingViewModel

/**
 * Main input screen allowing users to specify wall dimensions and stud spacing.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Shared Activity-scoped ViewModel for persistent calculation state
    private val viewModel: StudSpacingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUnitHintsAndDefaults()
        setupPresets()
        setupCalculateButton()
    }

    override fun onResume() {
        super.onResume()
        // Refresh hints in case user changed unit in SettingsFragment
        setupUnitHintsAndDefaults()
    }

    private fun setupUnitHintsAndDefaults() {
        val isMetric = viewModel.isMetric()
        val unitSymbol = viewModel.getUnitSymbol()

        binding.tilWallLength.hint = if (isMetric) {
            getString(R.string.wall_length_hint_metric)
        } else {
            getString(R.string.wall_length_hint_imperial)
        }

        binding.tilSpacing.hint = if (isMetric) {
            getString(R.string.on_center_spacing_hint_metric)
        } else {
            getString(R.string.on_center_spacing_hint_imperial)
        }

        binding.tilThickness.hint = if (isMetric) {
            getString(R.string.stud_thickness_hint_metric)
        } else {
            getString(R.string.stud_thickness_hint_imperial)
        }

        // Set preset chip texts based on active unit
        if (isMetric) {
            binding.chipPreset1.text = getString(R.string.preset_40_cm)
            binding.chipPreset2.text = getString(R.string.preset_60_cm)
        } else {
            binding.chipPreset1.text = getString(R.string.preset_16_inch)
            binding.chipPreset2.text = getString(R.string.preset_24_inch)
        }

        // If fields are empty, populate standard default values
        if (binding.etWallLength.text.isNullOrBlank()) {
            binding.etWallLength.setText(viewModel.getDefaultWallLength().toString())
        }
        if (binding.etSpacing.text.isNullOrBlank()) {
            binding.etSpacing.setText(viewModel.getDefaultSpacing().toString())
        }
        if (binding.etThickness.text.isNullOrBlank()) {
            binding.etThickness.setText(viewModel.getDefaultThickness().toString())
        }
    }

    private fun setupPresets() {
        binding.chipGroupPresets.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val isMetric = viewModel.isMetric()
            when (checkedIds.first()) {
                R.id.chipPreset1 -> {
                    val value = if (isMetric) "40.0" else "16.0"
                    binding.etSpacing.setText(value)
                    binding.tilSpacing.error = null
                }
                R.id.chipPreset2 -> {
                    val value = if (isMetric) "60.0" else "24.0"
                    binding.etSpacing.setText(value)
                    binding.tilSpacing.error = null
                }
            }
        }
    }

    private fun setupCalculateButton() {
        binding.btnCalculate.setOnClickListener {
            if (validateInputs()) {
                val wallLength = binding.etWallLength.text.toString().toDouble()
                val spacing = binding.etSpacing.text.toString().toDouble()
                val thickness = binding.etThickness.text.toString().toDouble()

                val success = viewModel.performCalculation(wallLength, spacing, thickness)
                if (success) {
                    findNavController().navigate(R.id.action_homeFragment_to_resultFragment)
                }
            }
        }
    }

    /**
     * Rigorous input validation ensuring all parameters are valid positive numbers.
     */
    private fun validateInputs(): Boolean {
        var isValid = true

        // Clear previous error states
        binding.tilWallLength.error = null
        binding.tilSpacing.error = null
        binding.tilThickness.error = null

        val wallLengthStr = binding.etWallLength.text?.toString()?.trim()
        val spacingStr = binding.etSpacing.text?.toString()?.trim()
        val thicknessStr = binding.etThickness.text?.toString()?.trim()

        val wallLength = wallLengthStr?.toDoubleOrNull()
        val spacing = spacingStr?.toDoubleOrNull()
        val thickness = thicknessStr?.toDoubleOrNull()

        // 1. Validate Wall Length
        if (wallLengthStr.isNullOrEmpty()) {
            binding.tilWallLength.error = getString(R.string.error_wall_length_required)
            isValid = false
        } else if (wallLength == null || wallLength <= 0.0) {
            binding.tilWallLength.error = getString(R.string.error_wall_length_invalid)
            isValid = false
        }

        // 2. Validate Spacing
        if (spacingStr.isNullOrEmpty()) {
            binding.tilSpacing.error = getString(R.string.error_spacing_required)
            isValid = false
        } else if (spacing == null || spacing <= 0.0) {
            binding.tilSpacing.error = getString(R.string.error_spacing_invalid)
            isValid = false
        }

        // 3. Validate Thickness
        if (thicknessStr.isNullOrEmpty()) {
            binding.tilThickness.error = getString(R.string.error_thickness_required)
            isValid = false
        } else if (thickness == null || thickness <= 0.0) {
            binding.tilThickness.error = getString(R.string.error_thickness_invalid)
            isValid = false
        }

        // 4. Validate Spacing relative to Wall Length
        if (wallLength != null && spacing != null && spacing > wallLength) {
            binding.tilSpacing.error = getString(R.string.error_spacing_exceeds_wall)
            isValid = false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
