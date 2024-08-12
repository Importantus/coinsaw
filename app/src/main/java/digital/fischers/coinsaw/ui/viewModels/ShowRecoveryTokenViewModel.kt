package digital.fischers.coinsaw.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import digital.fischers.coinsaw.ui.Screen
import javax.inject.Inject

@HiltViewModel
class ShowRecoveryTokenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val groupId: String = savedStateHandle.get<String>(Screen.ARG_GROUP_ID)!!
    val recoveryToken: String = savedStateHandle.get<String>(Screen.ARG_RECOVERY_TOKEN)!!
}