package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Box( modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
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

data class FormField(
    var value: String,
    var error: Boolean = false,
    var errorMsg: String = ""
)

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

    val releaseDateState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
    )

    var showDatePicker by remember { mutableStateOf(false) }


    var name by remember { mutableStateOf(FormField(value = "", error = false, errorMsg = "")) }
    var cover by remember { mutableStateOf(FormField(value = "", error = false, errorMsg = "")) }
    var genre by remember { mutableStateOf(FormField(value = "", error = false, errorMsg = "")) }
    var releaseDate by remember { mutableStateOf(FormField(value = "", error = false, errorMsg = "")) }
    var recordLabel by remember { mutableStateOf(FormField(value = "", error = false, errorMsg = "")) }
    var description by remember { mutableStateOf(FormField(value = "", error = false, errorMsg = "")) }

    fun validateForm() {
        val errorMessages = mutableListOf<Pair<String, String>>()
        val imageRegex = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|gif|png|jpeg|)"
        val currentDate = Instant.now().toEpochMilli()

        if (name.value.isEmpty()) {
            errorMessages.add("name" to "El nombre es obligatorio")
        }else if (name.value.length > 200) {
            errorMessages.add("name" to "El nombre debe tener máximo 200 caracteres")
        } else {
            name = name.copy(error = false, errorMsg = "")
        }

        if (cover.value.isEmpty()) {
            errorMessages.add("cover" to "La URL de la portada es obligatoria")
        }else if (!cover.value.matches(imageRegex.toRegex())) {
            errorMessages.add("cover" to "La URL de la portada no es válida")
        } else {
            cover = cover.copy(error = false, errorMsg = "")
        }

        if (genre.value.isEmpty()) {
            errorMessages.add("genre" to "El género es obligatorio")
        }else {
            genre = genre.copy(error = false, errorMsg = "")
        }

        if (releaseDate.value.isEmpty()) {
            errorMessages.add("releaseDate" to "La fecha de estreno es obligatoria")
        }else if (releaseDateState.selectedDateMillis!! > currentDate) {
            errorMessages.add("releaseDate" to "La fecha de estreno no puede ser en el futuro")
        }else {
            releaseDate = releaseDate.copy(error = false, errorMsg = "")
        }

        if (recordLabel.value.isEmpty()) {
            errorMessages.add("recordLabel" to "La disquera es obligatoria")
        }else {
            recordLabel = recordLabel.copy(error = false, errorMsg = "")
        }

        if (description.value.isEmpty()) {
            errorMessages.add("description" to "La descripción es obligatoria")
        }else if (description.value.length > 2000) {
            errorMessages.add("description" to "La descripción debe tener máximo 2000 caracteres")
        } else {
            description = description.copy(error = false, errorMsg = "")
        }

        val hasError = errorMessages.isNotEmpty()

        if (hasError) {
            errorMessages.forEach { (fieldName, errorMsg) ->
                when (fieldName) {
                    "name" -> name = name.copy(error = true, errorMsg = errorMsg)
                    "cover" -> cover = cover.copy(error = true, errorMsg = errorMsg)
                    "genre" -> genre = genre.copy(error = true, errorMsg = errorMsg)
                    "releaseDate" -> releaseDate = releaseDate.copy(error = true, errorMsg = errorMsg)
                    "recordLabel" -> recordLabel = recordLabel.copy(error = true, errorMsg = errorMsg)
                    "description" -> description = description.copy(error = true, errorMsg = errorMsg)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name.value,
            onValueChange = { newValue ->
                name = name.copy(value = newValue)
            },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth(),
            isError = name.error,
            supportingText = {
                if (name.errorMsg.isNotEmpty())
                    Text(text = name.errorMsg, modifier = Modifier.padding(bottom = 8.dp))
            }
        )

        OutlinedTextField(
            value = cover.value,
            onValueChange = { newValue ->
                cover = cover.copy(value = newValue)
            },
            label = { Text("URL de la portada") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Uri
            ),
            modifier = Modifier
                .fillMaxWidth(),
            isError = cover.error,
            supportingText = {
                if (cover.errorMsg.isNotEmpty())
                    Text(text = cover.errorMsg, modifier = Modifier.padding(bottom = 8.dp))
            }
        )

        ExposedDropdownMenuBox(
            expanded = dropdownGenreExpanded,
            onExpandedChange = {
                dropdownGenreExpanded = !dropdownGenreExpanded
            },
        ) {
            OutlinedTextField(
                value = genre.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Género") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownGenreExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = genre.error,
                supportingText = {
                    if (genre.errorMsg.isNotEmpty())
                        Text(text = genre.errorMsg, modifier = Modifier.padding(bottom= 8.dp))
                }
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
                            genre = genre.copy(value = genreOptions[genreIndex])
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = releaseDate.value,
            onValueChange = {},
            enabled = false,
            label = { Text("Fecha de estreno") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { showDatePicker = true }
                ),
            leadingIcon = { Icon( Icons.Filled.DateRange, "Agregar fecha de estreno") },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if(releaseDate.error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.error
            ),
            isError = releaseDate.error,
            supportingText = {
                if (releaseDate.errorMsg.isNotEmpty())
                    Text(text = releaseDate.errorMsg, modifier = Modifier.padding(bottom = 8.dp))
            }

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
                }
            ) {
                DatePicker(
                    state = releaseDateState
                )
            }
        }

        releaseDate = releaseDate.copy(value = releaseDateState.selectedDateMillis?.let {
            Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
        }?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "")

        ExposedDropdownMenuBox(
            expanded = recordLabelExpanded,
            onExpandedChange = {
                recordLabelExpanded = !recordLabelExpanded
            },
        ) {
            OutlinedTextField(
                value = recordLabel.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Disquera") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recordLabelExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = recordLabel.error,
                supportingText = {
                    if (recordLabel.errorMsg.isNotEmpty())
                        Text(text = recordLabel.errorMsg, modifier = Modifier.padding(bottom = 8.dp))
                }
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
                            recordLabel = recordLabel.copy(value = recordLabelOptions[recordLabelIndex])
                        }
                    )
                }
            }
        }



        OutlinedTextField(
            value = description.value,
            onValueChange = { newValue ->
                description = description.copy(value = newValue) },
            label = { Text("Descripción") },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth(),
            isError = description.error,
            supportingText = {
                if (description.errorMsg.isNotEmpty())
                    Text(text = description.errorMsg, modifier = Modifier.padding(bottom = 8.dp))
            }
        )

        Button(
            onClick = { validateForm() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text("Agregar")
        }

    }
}
