package co.edu.uniandes.misw4203.equipo11.vinilos.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.CollectorRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.theme.VinilosTheme
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.CollectorListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.ErrorUiState
import java.util.Date

@Composable
fun CollectorListScreen(snackbarHostState: SnackbarHostState) {
    val viewModel: CollectorListViewModel = viewModel(
        factory = CollectorListViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, CollectorRepository())
        }
    )
    val collectors by viewModel.collectors.collectAsStateWithLifecycle(
        emptyList()
    )
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle(
        true
    )
    val error by viewModel.error.collectAsStateWithLifecycle(
        ErrorUiState.NoError
    )

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.onRefresh() }
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        CollectorList(collectors)

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (error is ErrorUiState.Error) {
        val message = stringResource((error as ErrorUiState.Error).resourceId)
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectorItem(collector: CollectorWithPerformers) {
    ElevatedCard(
        modifier = Modifier.padding(8.dp),
        onClick = { /*TODO*/ }
    ) {
        Column(
            modifier = Modifier.padding(20.dp, 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 6.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                text = collector.collector.name
            )
            Text(
                modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 4.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                text = stringResource(R.string.collectors_list_artists_title)
            )
            Text(
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = collector.performers.joinToString { it.name }
            )
        }
    }
}

@Composable
private fun CollectorList(collectors: List<CollectorWithPerformers>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(360.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        items(collectors) {
                item: CollectorWithPerformers -> CollectorItem(item)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlbumListScreenPreview() {
    @Suppress("SpellCheckingInspection")
    val performers: List<Performer> = listOf(
        Performer(1, PerformerType.MUSICIAN, "Fulanito", "Red", "description", Date())
    )

    val collectors: List<CollectorWithPerformers> = listOf(
        CollectorWithPerformers(Collector(1, "Buscando américa", "Salsa", "red"), performers),
        CollectorWithPerformers(Collector(2, "Buscando américa", "Salsa", "red"), performers),
        CollectorWithPerformers(Collector(3, "Buscando américa", "Salsa", "red"), performers),
        CollectorWithPerformers(Collector(4, "Buscando américa", "Salsa", "red"), performers),
        CollectorWithPerformers(Collector(5, "Buscando américa", "Salsa", "red"), performers),
        CollectorWithPerformers(Collector(6, "Marcela Jimenez Suarez", "Salsa", "red"), performers),
    )

    VinilosTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CollectorList(collectors)
        }
    }
}
