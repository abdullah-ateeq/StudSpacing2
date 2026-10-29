package com.example.studspacing.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studspacing.R
import com.example.studspacing.databinding.FragmentAboutBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * About screen providing app details, versioning, carpentry tips,
 * and Play Console compliance policies (Privacy Policy & Terms of Service).
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVersionInfo()
        setupLegalDialogs()
    }

    private fun setupVersionInfo() {
        val versionName = getString(R.string.app_version_name)
        binding.tvAboutVersion.text = getString(R.string.app_version_format, versionName)
    }

    private fun setupLegalDialogs() {
        // Privacy Policy Dialog
        binding.tvPrivacyPolicy.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.privacy_policy_title)
                .setMessage(R.string.privacy_policy_content)
                .setPositiveButton(R.string.action_close, null)
                .show()
        }

        // Terms of Service Dialog
        binding.tvTermsOfService.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.terms_of_service_title)
                .setMessage(R.string.terms_of_service_content)
                .setPositiveButton(R.string.action_close, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
