package com.devpass.spaceapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels

import com.bumptech.glide.Glide
import com.devpass.spaceapp.R
import com.devpass.spaceapp.data.model.NextLaunchesModel
import kotlin.Result
import com.devpass.spaceapp.databinding.ActivityLaunchBinding
import com.google.android.material.tabs.TabLayoutMediator

class LaunchActivity : AppCompatActivity() {
    private val rocketDetailsViewModel: RocketDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar o objeto launch da intent
        val launch = intent.getSerializableExtra("nextLaunch") as NextLaunchesModel

        // Preencher os campos com as informações do objeto launch
        binding.apply {
            launchTitleTextView.text = launch.name
            launchDateTextView.text = launch.date_utc
            launchStatusTextView.text = launch.status.toString()
        }

        if(launch.links.image.small.isEmpty()) {
            // caso a URL da imagem seja nula ou vazia, carrega uma imagem padrão
            Glide.with(this)
                .load(R.drawable.space_logo)
                .circleCrop()
                .into(binding.launchImage)
        } else {
            // carrega a imagem usando Glide
            Glide.with(this)
                .load(launch.links.image.small)
                .circleCrop()
                .into(binding.launchImage)
        }

        rocketDetailsViewModel.rocketDetail.observe(this, { result ->
            when (result) {
                is Result.Success -> {
                    val rocketDetail = result.data
                    Log.d("LaunchActivity", "Rocket Detail: $rocketDetail")
                    // faça algo com os dados de RocketDetail
                }
                is Result.Failure -> {
                    val exception = result.exception
                    Log.e("LaunchActivity", "Failed to fetch rocket detail", exception)
                    // trate o erro
                }
            }
        })



        // Cria uma instância do LaunchDetailsPagerAdapter com as informações do lançamento
        val launchDetailsPagerAdapter = LaunchDetailsPagerAdapter(this, launch)

        // Configura o ViewPager para usar o adapter
        binding.viewPager.adapter = launchDetailsPagerAdapter

        // Adiciona as abas ao TabLayout
        val titles = arrayOf(getString(R.string.tab_title_details), getString(R.string.tab_title_launchpad), getString(R.string.tab_title_rocket))
        for (title in titles) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title))
        }

        // Conecta o TabLayout e ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}