package com.test.runar.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.test.runar.extensions.SingleLiveEvent
import com.test.runar.model.AffimDescriptionModel
import com.test.runar.model.LayoutDescriptionModel
import com.test.runar.model.RuneDescriptionModel
import com.test.runar.model.UserLayoutModel
import com.test.runar.repository.DatabaseRepository
import com.test.runar.repository.SharedDataRepository
import com.test.runar.repository.SharedPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class InterpretationViewModel(application: Application) : AndroidViewModel(application) {
    var preferencesRepository = SharedPreferencesRepository.get()
    val fontSize: LiveData<Float> = MutableLiveData(SharedDataRepository.fontSize)
    var runesData: List<RuneDescriptionModel> = emptyList()
    var affirmData: List<AffimDescriptionModel> = emptyList()


    private var _selectedRune = SingleLiveEvent<RuneDescriptionModel>()
    private var _currentAffirm = SingleLiveEvent<String>()
    private var _currentInterpretation = SingleLiveEvent<String>()
    private var  _currentAusp = SingleLiveEvent<Int>()
    private var _selectedLayout = SingleLiveEvent<LayoutDescriptionModel>()
    private var userLayout = arrayListOf<Int>()

    var currentAusp : LiveData<Int> = _currentAusp
    val selectedLayout: LiveData<LayoutDescriptionModel> = _selectedLayout
    var currentAffirm : LiveData<String> = _currentAffirm
    var currentInterpretation : LiveData<String> = _currentInterpretation
    var selectedRune : LiveData<RuneDescriptionModel> = _selectedRune


    fun getSelectedRuneData(id: Int) {
        val runeId = userLayout[id]
        for (rune in runesData) {
            if (rune.runeId == runeId) {
                _selectedRune.postValue(rune)
            }
        }
    }
    fun getInterpretation() {
        val layoutId = selectedLayout.value?.layoutId
        var result: String = ""
        when (layoutId) {
            1 -> result = getFullDescriptionForRune(userLayout[1]) + "."
            2 -> {
                CoroutineScope(IO).launch {
                    val index = userLayout[1] * 100 + userLayout[2]
                    val inter = DatabaseRepository.getTwoRunesInterpretation(index)
                    val res = String.format(selectedLayout.value?.interpretation!!,inter)
                    _currentInterpretation.postValue(res)
                }
                return
            }
            3 -> result = String.format(selectedLayout.value?.interpretation!!,
                    getMeaningForRune(userLayout[1]),getMeaningForRune(userLayout[2]),getMeaningForRune(userLayout[3]))

            4 -> result = String.format(selectedLayout.value?.interpretation!!,
                    getMeaningForRune(userLayout[1]),getMeaningForRune(userLayout[2]),getMeaningForRune(userLayout[3]),getMeaningForRune(userLayout[4]))
            5 -> result = String.format(selectedLayout.value?.interpretation!!,
                    getMeaningForRune(userLayout[1]),getMeaningForRune(userLayout[2]),getMeaningForRune(userLayout[4]))
            6 -> result = String.format(selectedLayout.value?.interpretation!!,
                    getMeaningForRune(userLayout[1]),getMeaningForRune(userLayout[2]),getMeaningForRune(userLayout[3]),getMeaningForRune(userLayout[5]),getMeaningForRune(userLayout[4]))
            7 -> result = String.format(selectedLayout.value?.interpretation!!,
                    getMeaningForRune(userLayout[2]),getMeaningForRune(userLayout[1]),getMeaningForRune(userLayout[4]),getMeaningForRune(userLayout[3]),getMeaningForRune(userLayout[5]),getMeaningForRune(userLayout[6]))
            8 -> result = String.format(selectedLayout.value?.interpretation!!,
                    getMeaningForRune(userLayout[1]),getMeaningForRune(userLayout[2]),getMeaningForRune(userLayout[3]),getMeaningForRune(userLayout[4]),getMeaningForRune(userLayout[5]),getMeaningForRune(userLayout[6]),getMeaningForRune(userLayout[7]))
        }
        _currentInterpretation.postValue(result)
    }

    fun saveUserLayout() {
        val userId = preferencesRepository.userId
        val layoutId = selectedLayout.value?.layoutId
        val currentDate = System.currentTimeMillis() / 1000L
        CoroutineScope(IO).launch {
            val userLayout = UserLayoutModel(
                    userId,
                    currentDate,
                    layoutId,
                    userLayout[0],
                    userLayout[1],
                    userLayout[2],
                    userLayout[3],
                    userLayout[4],
                    userLayout[5],
                    userLayout[6],
                    currentInterpretation.value
            )
            DatabaseRepository.addUserLayout(userLayout)
        }
    }

    fun getAuspForCurrentLayout() {
        val layoutId = selectedLayout.value?.layoutId
        var ausp = 0
        when (layoutId) {
            1 -> ausp = getSumOfAusp(arrayListOf(1))
            2 -> ausp = getSumOfAusp(arrayListOf(1,2)) / 2
            3 -> ausp = getSumOfAusp(arrayListOf(3))
            4 -> ausp = getSumOfAusp(arrayListOf(3,4)) / 2
            5 -> ausp = getSumOfAusp(arrayListOf(2,3,4))/ 3
            6 -> ausp =getSumOfAusp(arrayListOf(3,4,5))/ 3
            7 -> ausp =getSumOfAusp(arrayListOf(3,5,6))/ 3
            8 -> ausp =getSumOfAusp(arrayListOf(3,4,6,7))/ 4
        }
        _currentAusp.postValue(ausp)
    }

    fun getAffimForCurrentLayout(ausp: Int) {
        while (true) {
            val affirmElement = affirmData.random()
            when (ausp) {
                in 0..19 -> {
                    _currentAffirm.postValue(affirmElement.lvl1)
                    return
                }
                in 20..29 -> {
                    _currentAffirm.postValue(affirmElement.lvl2)
                    return
                }
                in 30..39 -> {
                    _currentAffirm.postValue(affirmElement.lvl3)
                    return
                }
                in 40..50 -> {
                    if (affirmElement.lvl4 != null || affirmElement.lvl4 != "") {
                        _currentAffirm.postValue(affirmElement.lvl4)
                        return
                    }
                }
            }
        }
    }

    fun getSumOfAusp(ids: ArrayList<Int>): Int {
        var sum =0
        for(runePos in ids){
            for (rune in runesData) {
                if (rune.runeId == userLayout[runePos]) {
                    sum+= rune.ausp!!
                }
            }
        }
        return sum
    }

    private fun getMeaningForRune(id: Int): String {
        for (rune in runesData) {
            if (rune.runeId == id) {
                return rune.meaning!!
            }
        }
        return ""
    }

    private fun getFullDescriptionForRune(id: Int): String {
        for (rune in runesData) {
            if (rune.runeId == id) {
                return rune.fullDescription!!
            }
        }
        return ""
    }

    fun getRuneDataFromDB() {
        CoroutineScope(IO).launch {
            runesData = DatabaseRepository.getRunesList()
        }
    }

    fun getAffirmationsDataFromDB() {
        CoroutineScope(IO).launch {
            affirmData = DatabaseRepository.getAffirmList()
        }
    }

    fun getLayoutDescription(id: Int) {
        CoroutineScope(IO).launch {
            _selectedLayout.postValue(DatabaseRepository.getLayoutDetails(id))
        }
    }

    fun setCurrentUserLayout(currentLayout: ArrayList<Int>) {
        userLayout = currentLayout
    }
}