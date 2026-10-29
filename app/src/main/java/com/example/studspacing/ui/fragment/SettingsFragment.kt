package com.example.studspacing.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.studspacing.R
import com.example.studspacing.databinding.FragmentSettingsBinding
import com.example.studspacing.ui.viewmodel.SettingsViewModel

/**
 * Settings screen for configuring units of measurement and visual theme.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUnitSwitch()
        setupThemeSwitch()
    }

    private fun setupUnitSwitch() {
        viewModel.isMetric.observe(viewLifecycleOwner) { isMetric ->
            binding.switchUnit.isChecked = isMetric
            updateUnitLabel(isMetric)
        }

        binding.switchUnit.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setMetric(isChecked)
            updateUnitLabel(isChecked)
        }
    }

    private fun updateUnitLabel(isMetric: Boolean) {
        val label = if (isMetric) {
            getString(R.string.unit_metric)
        } else {
            getString(R.string.unit_imperial)
        }
        binding.tvUnitActiveLabel.text = label
    }

    private fun setupThemeSwitch() {
        viewModel.isDarkMode.observe(viewLifecycleOwner) { isDark ->
            binding.switchTheme.isChecked = isDark
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
