package org.readium.r2.testapp.presentation.fragment

import org.readium.r2.testapp.presentation.helper.Const
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.util.Locale
import org.readium.r2.testapp.R
import org.readium.r2.testapp.databinding.FragmentChooseLanguageBinding
import org.readium.r2.testapp.presentation.helper.SharedPreference


@Suppress("DEPRECATION")
class ChooseLanguageFragment : Fragment() {

    private lateinit var binding: FragmentChooseLanguageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseLanguageBinding.inflate(inflater, container, false)

        loadLocale()
        setTexts()
        initListener()

        return binding.root
    }


    private fun setLocale(language: String) {
        val local = Locale(language)
        Locale.setDefault(local)
        val config = Configuration()
        config.locale = local
        val baseContext = requireContext()
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        saveLanguagePreference(language)
        setTexts()
    }

    private fun loadLocale() {
        val savedLang = SharedPreference.getLang() ?: Const.ENGLISH_LANG
        setLocale(savedLang)
    }

    private fun saveLanguagePreference(language: String) {
        SharedPreference.setLang(language)
    }

    private fun setTexts() {
        binding.choiceLanguageText.text =
            getString(R.string.text_what_language_are_you_studying_now)
        binding.englishLanguageText.text = getString(R.string.text_english)
        binding.russianLanguageText.text = getString(R.string.text_russian)
        binding.spainLanguageText.text = getString(R.string.text_spain)
        binding.frenchLanguageText.text = getString(R.string.text_french)
    }


    private fun initListener() {
        binding.englishLanguageButton.setOnClickListener {
            setLocale(Const.ENGLISH_LANG)
            navigateToAddBookFragment()
        }

        binding.russianLanguageButton.setOnClickListener {
            setLocale(Const.RUSSIAN_LANG)
            navigateToAddBookFragment()
        }

        binding.frenchLanguageButton.setOnClickListener {
            setLocale(Const.FRENCH_LANG)
            navigateToAddBookFragment()
        }

        binding.spainLanguageButton.setOnClickListener {
            setLocale(Const.SPANISH_LANG)
            navigateToAddBookFragment()
        }
    }


    private fun navigateToAddBookFragment() {
        val fragment = AddBookFragment()
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment).addToBackStack(fragment.tag)
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null)
        transaction.commit()
    }

}