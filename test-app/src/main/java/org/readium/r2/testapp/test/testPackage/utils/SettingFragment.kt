package org.readium.r2.testapp.test.testPackage.utils

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.readium.r2.testapp.MainActivity
import org.readium.r2.testapp.R
import org.readium.r2.testapp.databinding.FragmentAddBookBinding
import org.readium.r2.testapp.databinding.FragmentSettingBinding
import org.readium.r2.testapp.test.testPackage.MainFragment

class SettingFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSettingBinding.inflate(inflater, container, false)
            binding.buttonLogout.setOnClickListener() {
                val fragment = MainFragment()
                val transaction: FragmentTransaction =
                    requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(fragment.tag)
                transaction.commit()
                dismiss()
            }


        return binding.root
    }
}