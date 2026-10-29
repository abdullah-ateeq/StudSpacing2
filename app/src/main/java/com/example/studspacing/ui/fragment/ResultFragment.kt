package com.example.studspacing.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studspacing.R
import com.example.studspacing.data.model.CalculationResult
import com.example.studspacing.databinding.FragmentResultBinding
import com.example.studspacing.ui.adapter.HistoryAdapter
import com.example.studspacing.ui.adapter.StudPositionAdapter
import com.example.studspacing.ui.viewmodel.StudSpacingViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Result and History screen presenting stud layout markings and saved calculations.
 */
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudSpacingViewModel by activityViewModels()

    private var positionAdapter: StudPositionAdapter? = null
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHistoryRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(
            onItemClick = { savedItem ->
                // Reload selected history item into active calculation
                viewModel.loadCalculationFromHistory(savedItem)
                Snackbar.make(
                    binding.root,
                    getString(R.string.result_wall_length_format, savedItem.wallLength, savedItem.unit),
                    Snackbar.LENGTH_SHORT
                ).show()
                // Scroll up smoothly to inspect reloaded result
                binding.root.smoothScrollTo(0, 0)
            },
            onDeleteClick = { savedItem ->
                viewModel.deleteHistoryItem(savedItem.id)
                Snackbar.make(binding.root, R.string.history_item_deleted_msg, Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupListeners() {
        // Save to History button
        binding.btnSaveHistory.setOnClickListener {
            if (viewModel.isCurrentResultAlreadySaved()) {
                Snackbar.make(binding.root, R.string.msg_already_saved, Snackbar.LENGTH_SHORT).show()
            } else {
                val saved = viewModel.saveCurrentCalculation()
                if (saved) {
                    Snackbar.make(binding.root, R.string.msg_saved_to_history, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // New calculation navigation
        binding.btnNewCalculation.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_homeFragment)
        }

        // Clear all history with confirmation dialog
        binding.btnClearHistory.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.history_clear_dialog_title)
                .setMessage(R.string.history_clear_dialog_message)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete) { _, _ ->
                    viewModel.clearAllHistory()
                    Snackbar.make(binding.root, R.string.history_cleared_msg, Snackbar.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun observeViewModel() {
        // Observe current calculation result
        viewModel.currentResult.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                bindCalculationResult(result)
            } else {
                // If user tapped History tab without calculating yet,
                // fallback to the most recent history item if available
                val latest = viewModel.historyList.value?.firstOrNull()
                if (latest != null) {
                    viewModel.loadCalculationFromHistory(latest)
                } else {
                    binding.cardResultSummary.visibility = View.GONE
                    binding.tvPositionsHeader.visibility = View.GONE
                    binding.tvPositionsSub.visibility = View.GONE
                    binding.rvStudPositions.visibility = View.GONE
                }
            }
        }

        // Observe calculation history
        viewModel.historyList.observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
            if (history.isNullOrEmpty()) {
                binding.tvHistoryEmpty.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
                binding.btnClearHistory.visibility = View.GONE
            } else {
                binding.tvHistoryEmpty.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                binding.btnClearHistory.visibility = View.VISIBLE
            }
        }
    }

    private fun bindCalculationResult(result: CalculationResult) {
        binding.cardResultSummary.visibility = View.VISIBLE
        binding.tvPositionsHeader.visibility = View.VISIBLE
        binding.tvPositionsSub.visibility = View.VISIBLE
        binding.rvStudPositions.visibility = View.VISIBLE

        binding.tvTotalStudsCount.text = result.totalStuds.toString()
        binding.tvWallLengthSummary.text = getString(
            R.string.result_wall_length_format,
            result.wallLength,
            result.unit
        )
        binding.tvSpacingSummary.text = getString(
            R.string.result_spacing_format,
            result.spacing,
            result.unit
        )
        binding.tvThicknessSummary.text = getString(
            R.string.result_thickness_format,
            result.thickness,
            result.unit
        )

        // Setup or refresh stud positions adapter
        positionAdapter = StudPositionAdapter(result.unit)
        binding.rvStudPositions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = positionAdapter
        }
        positionAdapter?.submitList(result.studPositions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
