package io.github.ronjunevaldoz.bytesweep

import app.cash.turbine.test
import io.github.ronjunevaldoz.bytesweep.analysis.AnalyzeResponse
import io.github.ronjunevaldoz.bytesweep.domain.AnalyzeJunkUseCase
import io.github.ronjunevaldoz.bytesweep.domain.CleanJunkUseCase
import io.github.ronjunevaldoz.bytesweep.domain.FileLocationOpener
import io.github.ronjunevaldoz.bytesweep.domain.FolderScanner
import io.github.ronjunevaldoz.bytesweep.domain.JunkAnalysisService
import io.github.ronjunevaldoz.bytesweep.domain.ScanStorageUseCase
import io.github.ronjunevaldoz.bytesweep.domain.StorageScanner
import io.github.ronjunevaldoz.bytesweep.model.JunkCategory
import io.github.ronjunevaldoz.bytesweep.model.JunkItem
import io.github.ronjunevaldoz.bytesweep.model.ScanResult
import io.github.ronjunevaldoz.bytesweep.presenter.ScannerContract
import io.github.ronjunevaldoz.bytesweep.presenter.ScannerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeScanner(
    private val items: List<JunkItem>,
) : StorageScanner {
    var cleaned: List<JunkItem>? = null
    override suspend fun scan() = ScanResult(items)
    override suspend fun clean(items: List<JunkItem>): Long {
        cleaned = items
        return items.sumOf { it.sizeBytes }
    }
}

private class FakeAnalysisService : JunkAnalysisService {
    override suspend fun analyze(items: List<JunkItem>) =
        AnalyzeResponse(recommendations = emptyList(), summary = "ok")
}

private class FakeFileLocationOpener(override val isSupported: Boolean = true) : FileLocationOpener {
    override suspend fun open(item: JunkItem) = true
}

private class FakeFolderScanner(override val isSupported: Boolean = false) : FolderScanner {
    override val canDelete: Boolean = isSupported
    override suspend fun pickAndScan() = null
    override suspend fun delete(items: List<JunkItem>) = items.sumOf { it.sizeBytes }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ScannerViewModelTest {

    private val sample = listOf(
        JunkItem("a", "cache", "/a", 1000, JunkCategory.CACHE),
        JunkItem("b", "temp", "/b", 2000, JunkCategory.TEMP),
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `scan populates items`() = runTest {
        val vm = ScannerViewModel(
            ScanStorageUseCase(FakeScanner(sample)),
            CleanJunkUseCase(FakeScanner(sample)),
            AnalyzeJunkUseCase(FakeAnalysisService()),
            FakeFileLocationOpener(isSupported = false),

            FakeFolderScanner(),
        )
        vm.state.test {
            assertEquals(ScannerContract.State(), awaitItem()) // initial
            vm.onIntent(ScannerContract.Intent.ScanClicked)
            assertTrue(awaitItem().isScanning) // scanning = true
            val done = awaitItem()
            assertEquals(2, done.items.size)
            assertEquals(3000, done.totalBytes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clean removes selected items and reports reclaimed bytes`() = runTest {
        val scanner = FakeScanner(sample)
        val vm = ScannerViewModel(
            ScanStorageUseCase(scanner),
            CleanJunkUseCase(scanner),
            AnalyzeJunkUseCase(FakeAnalysisService()),
            FakeFileLocationOpener(),

            FakeFolderScanner(),
        )
        vm.onIntent(ScannerContract.Intent.ScanClicked)
        vm.state.test {
            // skip to a state that has the scanned items
            var current = awaitItem()
            while (current.items.isEmpty()) current = awaitItem()
            assertEquals(2, current.items.size)

            vm.onIntent(ScannerContract.Intent.CleanClicked)
            var afterClean = awaitItem()
            while (afterClean.isCleaning || afterClean.items.isNotEmpty()) afterClean = awaitItem()
            assertEquals(0, afterClean.items.size)
            assertEquals(3000, afterClean.lastReclaimedBytes)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
