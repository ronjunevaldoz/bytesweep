package io.github.ronjunevaldoz.bytesweep.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for the MVI pattern.
 *
 * - [State]  — immutable data class the screen renders.
 * - [Intent] — sealed interface of user actions.
 * - [Effect] — sealed interface of one-shot side effects (navigation, toasts).
 *
 * Effects use a [Channel] so each is delivered exactly once and never replays on
 * recomposition. State updates use [update] for atomic compare-and-swap.
 */
abstract class MviViewModel<State : Any, Intent : Any, Effect : Any>(
    initialState: State,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: Intent) {
        viewModelScope.launch { handleIntent(intent) }
    }

    protected abstract suspend fun handleIntent(intent: Intent)

    protected fun updateState(block: State.() -> State) {
        _state.update(block)
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
