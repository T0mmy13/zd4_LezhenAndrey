package com.bignerdranch.android.crime

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.crime.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contactRecyclerView.layoutManager = LinearLayoutManager(context)

        // Получение и отображение контактов
        val contacts = getContacts()
        val sortedContacts = contacts.sorted()

        val groupedContacts = sortedContacts.groupBy {  it.first().uppercaseChar() }

        val adapter = ContactAdapter(groupedContacts)
        binding.contactRecyclerView.adapter = adapter

        // Обработка кнопки возврата
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun getContacts(): List<String> {
        val contacts = mutableListOf<String>()
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null)

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                if (name != null) {
                    contacts.add(name)
                }
            }
        }
        return contacts
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}