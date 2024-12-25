package com.bignerdranch.android.crime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.crime.databinding.FragmentCrimeListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CrimeListFragment : Fragment() {

    private var _binding: FragmentCrimeListBinding? = null
    private val binding get() = _binding!!

    private val crimeDao: CrimeDao by lazy {
        CrimeDatabase.getDatabase(requireContext()).crimeDao()
    }

    private val crimeAdapter = CrimeAdapter { crime ->
        // Переход на CrimeDetailFragment с передачей данных
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CrimeDetailFragment.newInstance(crime.id))
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.crimeRecyclerView.adapter = crimeAdapter

        binding.addCrimeButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CrimeDetailFragment())
                .addToBackStack(null)
                .commit()
        }

        // Обновляем список преступлений
        lifecycleScope.launch {
            crimeDao.getCrimes().collect { crimes ->
                crimeAdapter.submitList(crimes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}