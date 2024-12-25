package com.bignerdranch.android.crime

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bignerdranch.android.crime.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CrimeDetailFragment : Fragment() {

    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var crimeDao: CrimeDao
    private var crime: Crime? = null
    private var suspectPhoneNumber: String? = null

    private val pickContact = registerForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
        uri?.let {
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)
            requireActivity().contentResolver.query(it, queryFields, null, null, null)?.use { cursor ->
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val suspectName = cursor.getString(0)
                    val contactId = cursor.getString(1)

                    // Получение номера телефона для подозреваемого
                    val phoneCursor = requireActivity().contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )

                    phoneCursor?.use {
                        if (it.moveToFirst()) {
                            suspectPhoneNumber = it.getString(0)
                        }
                    }

                    binding.chooseSuspectButton.text = suspectName
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crimeId: UUID? = arguments?.getSerializable(ARG_CRIME_ID) as? UUID
        crimeDao = CrimeDatabase.getDatabase(requireContext()).crimeDao()

        crimeId?.let {
            lifecycleScope.launch {
                crime = crimeDao.getCrime(it)
                crime?.let {  updateUI(it) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.crimeDateButton.setOnClickListener {
            saveCrime()
        }

        binding.chooseSuspectButton.isEnabled = crime?.isSolved ?: false
        binding.crimeSolved.setOnCheckedChangeListener { _, isChecked ->
            binding.chooseSuspectButton.isEnabled = isChecked
        }

        binding.chooseSuspectButton.setOnClickListener {
            checkContactPermissionAndPickContact()
        }

        binding.sendReportButton.setOnClickListener {
            sendCrimeReport()
        }
    }

    private fun checkContactPermissionAndPickContact() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQUEST_CONTACT_PERMISSION)
        } else {
            pickContact.launch(null)
        }
    }

    private fun sendCrimeReport() {
        val report = getCrimeReport()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$suspectPhoneNumber")
            putExtra("sms_body", report)
        }
        startActivity(intent)
    }

    private fun getCrimeReport(): String {
        val dateString = binding.crimeDateButton.text.toString()
        val details = binding.crimeDetails.text.toString()

        return getString(R.string.crime_report,
            binding.crimeTitle.text.toString(),
            dateString,
            details)
    }

    private fun updateUI(crime: Crime) {
        binding.crimeTitle.setText(crime.title)
        binding.crimeDetails.setText(crime.details)
        updateDate(crime.date)
        binding.crimeSolved.isChecked = crime.isSolved
    }

    private fun updateDate(date: Date) {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault())
        binding.crimeDateButton.text = dateFormat.format(date)
    }

    private fun saveCrime() {
        val newCrime = crime?.copy(
            title = binding.crimeTitle.text.toString(),
            details = binding.crimeDetails.text.toString(),
            isSolved = binding.crimeSolved.isChecked
        ) ?: Crime(
            title = binding.crimeTitle.text.toString(),
            details = binding.crimeDetails.text.toString(),
            date = Date(),
            isSolved = binding.crimeSolved.isChecked
        )

        lifecycleScope.launch {
            crimeDao.addCrime(newCrime)
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val REQUEST_CONTACT_PERMISSION = 1

        fun newInstance(crimeId: UUID): CrimeDetailFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeDetailFragment().apply {  arguments = args }
        }
    }
}