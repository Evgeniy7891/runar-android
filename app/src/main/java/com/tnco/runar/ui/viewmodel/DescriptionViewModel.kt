package com.tnco.runar.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tnco.runar.model.LayoutDescriptionModel
import com.tnco.runar.repository.DatabaseRepository
import com.tnco.runar.repository.SharedDataRepository
import com.tnco.runar.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionViewModel : ViewModel() {

    private var _selectedLayout = SingleLiveEvent<LayoutDescriptionModel>()

    val selectedLayout: LiveData<LayoutDescriptionModel> = _selectedLayout
    val fontSize: LiveData<Float> = MutableLiveData(SharedDataRepository.fontSize)

    fun getLayoutDescription(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            _selectedLayout.postValue(DatabaseRepository.getLayoutDetails(id))
        }
    }

    fun notShowSelectedLayout(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseRepository.notShow(id)
        }
    }


}