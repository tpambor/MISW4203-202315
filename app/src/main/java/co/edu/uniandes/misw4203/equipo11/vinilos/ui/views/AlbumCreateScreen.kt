package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun AlbumCreateScreen(snackbarHostState: SnackbarHostState, navController: NavHostController) {

//    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle(
//        true
//    )
//
//    val error by viewModel.error.collectAsStateWithLifecycle(
//        ErrorUiState.NoError
//    )

//    val pullRefreshState = rememberPullRefreshState(
//        refreshing = isRefreshing,
//        onRefresh = {}
//    )

//    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column {
            AlbumCreateForm()
        }

//        PullRefreshIndicator(
//            refreshing = isRefreshing,
//            state = pullRefreshState,
//            modifier = Modifier.align(Alignment.TopCenter)
//        )
//    }

//    if (error is ErrorUiState.Error) {
//        val message = stringResource((error as ErrorUiState.Error).resourceId)
//        LaunchedEffect(error) {
//            snackbarHostState.showSnackbar(message)
//            viewModel.onErrorShown()
//        }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("ModifierParameter")
fun AlbumCreateForm() {
    val genreOptions = listOf("Classical", "Salsa", "Rock", "Folk")
    var genreIndex by remember { mutableIntStateOf(0) }
    var dropdownGenreExpanded by remember { mutableStateOf(false) }

    val recordLabelOptions = listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records")
    var recordLabelIndex by remember { mutableIntStateOf(0) }
    var recordLabelExpanded by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var urlPortada by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var fechaEstreno by remember { mutableStateOf("dd/mm/yyyy") }
    val releaseDateState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
    )
    var disquera by remember { mutableStateOf("") }
    val descripcion by remember { mutableStateOf("") }


    var showErrorSnackbar by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }


//    fun validateForm() {
//        val isValid = false
//            if (isValid) {
//            } else {
//                showErrorSnackbar = true
//            }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = urlPortada,
            onValueChange = { urlPortada = it },
            label = { Text("URL de la portada") },
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Uri
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = dropdownGenreExpanded,
            onExpandedChange = {
                dropdownGenreExpanded = !dropdownGenreExpanded
            },
        ) {
            OutlinedTextField(
                value = genero,
                onValueChange = {},
                readOnly = true,
                label = { Text("Género") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownGenreExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = dropdownGenreExpanded,
                onDismissRequest = { dropdownGenreExpanded = false },
            ){
                genreOptions.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            genreIndex = index
                            dropdownGenreExpanded = false
                            genero = genreOptions[genreIndex]
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = fechaEstreno,
            onValueChange = {},
            enabled = false,
            label = { Text("Fecha de estreno") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .clickable(
                    onClick = { showDatePicker = true }
                ),
            leadingIcon = { Icon( Icons.Filled.DateRange, "Agregar fecha de estreno") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                //For Icons
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    Button(
                        onClick = {
                            showDatePicker = false
                        }
                    ) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDatePicker = false
                        }
                    ) {
                        Text(text = "Cancelar")
                    }
                }
            ) {
                DatePicker(
                    state = releaseDateState
                )
            }
        }

        ExposedDropdownMenuBox(
            expanded = recordLabelExpanded,
            onExpandedChange = {
                recordLabelExpanded = !recordLabelExpanded
            },
        ) {
            OutlinedTextField(
                value = disquera,
                onValueChange = {},
                readOnly = true,
                label = { Text("Disquera") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recordLabelExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = recordLabelExpanded,
                onDismissRequest = { recordLabelExpanded = false },
            ){
                recordLabelOptions.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            recordLabelIndex = index
                            recordLabelExpanded = false
                            disquera = recordLabelOptions[recordLabelIndex]
                        }
                    )
                }
            }
        }

//        Text("Selected: ${fechaEstreno ?: "no selection"}")

        fechaEstreno = releaseDateState.selectedDateMillis?.let {
            Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
        }?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""

        // Manejar mensajes de error
        if (showErrorSnackbar) {
            ErrorSnackbar {
                showErrorSnackbar = false
            }
        }
    }
}

@Composable
fun ErrorSnackbar(onDismiss: () -> Unit) {
//    Snackbar(
//        Modifier.padding(16.dp), {
//            IconButton(onClick = onDismiss) {
//                Icon(imageVector = Icons.Default.Error, contentDescription = null)
//            }
//        },
//        backgroundColor = Color.Red
//    ) {
//        Text(text = stringResource(R'Error'))
//    }
}
