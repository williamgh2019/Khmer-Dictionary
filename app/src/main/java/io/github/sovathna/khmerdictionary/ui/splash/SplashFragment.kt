package io.github.sovathna.khmerdictionary.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.sovathna.khmerdictionary.BuildConfig
import io.github.sovathna.khmerdictionary.R
import io.github.sovathna.khmerdictionary.databinding.FragmentSplashBinding
import io.github.sovathna.khmerdictionary.extensions.kmNumString
import io.github.sovathna.khmerdictionary.ui.viewBinding

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

  val binding by viewBinding(FragmentSplashBinding::bind)
  private val viewModel by viewModels<SplashViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      tvVersion.text =
        getString(R.string.splash_version_text, BuildConfig.VERSION_NAME.kmNumString())
      btnRetry.setOnClickListener {
        viewModel.downloadDatabase()
      }
    }
    viewModel.stateLiveData.observe(viewLifecycleOwner, ::render)
  }


}