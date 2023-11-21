package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.CollectorRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.CollectorViewModel

@Composable
fun CollectorDetailScreen(collectorId: Int, snackbarHostState: SnackbarHostState, navController: NavHostController) {
    val viewModel: CollectorViewModel = viewModel(
        factory = CollectorViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, CollectorRepository())
            set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
        }
    )

    val collector by viewModel.collector.collectAsStateWithLifecycle(
        null
    )
    
    Text(text = collector?.name ?: "Todo")
}
