package org.readium.r2.testapp.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import org.readium.r2.testapp.R
import org.readium.r2.testapp.databinding.FragmentSettingBinding
import org.readium.r2.testapp.presentation.helper.SharedPreference

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSettingBinding.inflate(inflater, container, false)

            binding.buttonLogout.setOnClickListener() {
                SharedPreference.clearSharedPreference()
                val fragment = MainFragment()
                val transaction: FragmentTransaction =
                    requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment).addToBackStack(fragment.tag)
                transaction.commit()
            }

        return binding.root
    }
}