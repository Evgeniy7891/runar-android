package com.tnco.runar.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tnco.runar.repository.backend.BackendRepository
import com.tnco.runar.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SplashViewModel : ViewModel() {

    private val _splashCommand = SingleLiveEvent<Boolean>()
    private val _progress = MutableLiveData<Int>()

    val splashCommand: LiveData<Boolean> = _splashCommand
    val progress: LiveData<Int> = _progress

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val backendConnection = true

            delay(DELAY_BEFORE_START_LOADING)
            repeat(2) {
                delay(STEP_OF_LOADING)
                _progress.postValue(25 * (it + 1))
            }
            if (backendConnection) {
                val locale: String = Locale.getDefault().language
                if (locale == "ru") BackendRepository.getLibraryData("ru")
                else BackendRepository.getLibraryData("en")
            }
            repeat(2) {
                delay(STEP_OF_LOADING)
                _progress.postValue(25 * (it + 2))
            }
            _splashCommand.postValue(true)
        }
    }

    private companion object {
        const val DELAY_BEFORE_START_LOADING = 500L
        const val STEP_OF_LOADING = 500L
    }
}