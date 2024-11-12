package org.readium.r2.testapp.presentation.fragment

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import java.util.Locale
import org.readium.r2.testapp.R
import org.readium.r2.testapp.databinding.FragmentMainBinding
import org.readium.r2.testapp.presentation.helper.SharedPreference
import org.readium.r2.testapp.presentation.helper.Const

class  MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.circularBackground.background = createDrawableOval(context, R.color.circular_color)
        binding.ballSmallCircularBackground.background = createDrawableOval(
            context,
            R.color.circular_color
        )
        binding.bigCircularBackground.background = createDrawableOval(
            context,
            R.color.big_circular_color
        )
        binding.smallCircularBackground.background = createDrawableOval(
            context,
            R.color.small_circular_color
        )

        loadLocale()
        initListeners()

        return binding.root

    }


    private fun loadLocale() {
        val savedLang = SharedPreference.getLang() ?: Const.ENGLISH_LANG
        setLocale(savedLang)
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = requireContext().resources.configuration
        config.setLocale(locale)
        requireContext().createConfigurationContext(config)
    }


    private fun initListeners() {
        binding.registerButton.setOnClickListener {
            val fragment = ChooseLanguageFragment()
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment).addToBackStack(fragment.tag)
            transaction.commit()
        }
    }

    private fun createDrawableOval(context: Context?, color: Int): GradientDrawable? {
        if (context == null) return null
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(ContextCompat.getColor(context, color))
        return drawable
    }

}