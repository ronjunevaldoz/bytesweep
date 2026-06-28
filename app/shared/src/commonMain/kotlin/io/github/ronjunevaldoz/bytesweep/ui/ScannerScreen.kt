package io.github.ronjunevaldoz.bytesweep.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.ronjunevaldoz.bytesweep.presenter.ScannerContract
import io.github.ronjunevaldoz.bytesweep.presenter.ScannerViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Wired screen — owns DI, state collection, and one-shot effect handling.
 * Not used in previews; preview [ScannerContent] instead.
 */
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ScannerContract.Effect.ShowMessage ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ScannerContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}
