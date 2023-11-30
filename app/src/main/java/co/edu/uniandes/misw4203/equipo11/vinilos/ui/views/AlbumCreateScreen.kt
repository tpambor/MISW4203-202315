package co.edu.uniandes.misw4203.equipo11.vinilos.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJsonRequest
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.FormUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun AlbumCreateScreen(snackbarHostState: SnackbarHostState, navController: NavHostController,  activityScope: CoroutineScope) {

    val viewModel: AlbumViewModel = viewModel(
        factory = AlbumViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(AlbumViewModel.KEY_ALBUM_REPOSITORY, AlbumRepository())
        }
    )

    val formState by viewModel.formState.collectAsStateWithLifecycle(
        FormUiState.Input
    )

    val insertAlbumResult by viewModel.insertAlbumResult.collectAsStateWithLifecycle()

    if (formState == FormUiState.Saved) {
        LaunchedEffect(formState) {
            activityScope.launch {
                snackbarHostState.showSnackbar("Álbum agregado exitosamente")
            }
            navController.navigate("albums")
        }
    }

    Box( modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .semantics { this.contentDescription = "Formulario para agregar un álbum" }
    ) {
        AlbumCreateForm(viewModel, formState)
    }

    if (insertAlbumResult.isFailure) {
        val exception = insertAlbumResult.exceptionOrNull()
        val message = exception?.message ?: "Error desconocido"
        LaunchedEffect(insertAlbumResult) {
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
        }
    }

}

data class FormField(
    var value: String,
    var error: Boolean = false,
    var errorMsg: String = ""
)

@Composable
fun BasicInput(
    field: FormField,
    counter: Boolean = false,
    counterMaxLength: Int? = null,
    onValueChanged: (FormField) -> Unit,
    formPlaceholder: String,
    minLines: Int = 1,
    testTag: String,
) {
    OutlinedTextField(
        value = field.value,
        onValueChange = { newValue ->
            onValueChanged(field.copy(value = newValue))
        },
        minLines = minLines,
        label = { Text(formPlaceholder) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        isError = field.error,
        supportingText = {
            if (field.errorMsg.isNotEmpty())
                Text(text = field.errorMsg, modifier = Modifier.padding(bottom = 8.dp))
            else if(counter) {
                Text(
                    text = "${ field.value.length} / $counterMaxLength",
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription =
                                "${field.value.length} de $counterMaxLength caracteres utilizados"
                        },
                    textAlign = TextAlign.End,
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Selector(
    field: FormField,
    options: List<String>,
    onValueChanged: (FormField) -> Unit,
    fieldPlaceholder: String,
    testTag: String
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var fieldIndex by remember { mutableIntStateOf(0) }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = {
            dropdownExpanded = !dropdownExpanded
        },
        modifier = Modifier.testTag(testTag)
    ) {
        OutlinedTextField(
            value = field.value,
            onValueChange = {},
            readOnly = true,
            label = { Text(fieldPlaceholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            isError = field.error,
            supportingText = {
                if (field.errorMsg.isNotEmpty())
                    Text(text = field.errorMsg, modifier = Modifier.padding(bottom= 8.dp))
            }
        )
        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false },
        ){
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        fieldIndex = index
                        dropdownExpanded = false
                        onValueChanged(field.copy(value = options[index]))
                    },
                    modifier = Modifier.testTag("$testTag-$index")
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumCreateForm(viewModel: AlbumViewModel, formState: FormUiState) {
    val genreOptions = listOf("Classical", "Salsa", "Rock", "Folk")
    val recordLabelOptions = listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records")

    val formEnabled = formState == FormUiState.Input
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

    val context = LocalContext.current

    fun validateForm() {
        val errorMessages = mutableListOf<Pair<String, String>>()
        val imageRegex = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|JPG|gif|GIF|png|PNG|jpeg|JPEG)"
        val currentDate = Instant.now().toEpochMilli()


        if (name.value.isEmpty()) {
            errorMessages.add("name" to context.getString(R.string.mandatory_field_2, "nombre"))
        }else if (name.value.length > 200) {
            errorMessages.add("name" to context.getString(R.string.max_char_field_2, "nombre", "200"))
        } else {
            name = name.copy(error = false, errorMsg = "")
        }

        if (cover.value.isEmpty()) {
            errorMessages.add("cover" to context.getString(R.string.mandatory_field_1, "URL de la portada"))
        }else if (!cover.value.matches(imageRegex.toRegex())) {
            errorMessages.add("cover" to context.getString(R.string.err_cover_format))
        } else {
            cover = cover.copy(error = false, errorMsg = "")
        }

        if (genre.value.isEmpty()) {
            errorMessages.add("genre" to context.getString(R.string.mandatory_field_2, "género"))
        }else {
            genre = genre.copy(error = false, errorMsg = "")
        }

        if (releaseDate.value.isEmpty()) {
            errorMessages.add("releaseDate" to context.getString(R.string.mandatory_field_1, "fecha"))
        }else if (releaseDateState.selectedDateMillis!! > currentDate) {
            errorMessages.add("releaseDate" to context.getString(R.string.err_future_date))
        }else {
            releaseDate = releaseDate.copy(error = false, errorMsg = "")
        }

        if (recordLabel.value.isEmpty()) {
            errorMessages.add("recordLabel" to context.getString(R.string.mandatory_field_1, "disquera"))
        }else {
            recordLabel = recordLabel.copy(error = false, errorMsg = "")
        }

        if (description.value.isEmpty()) {
            errorMessages.add("description" to context.getString(R.string.mandatory_field_1, "descripción"))
        }else if (description.value.length > 2000) {
            errorMessages.add("description" to context.getString(R.string.max_char_field_1, "descripción", "2000"))
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
        }else {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val date = LocalDate.parse(releaseDate.value, dateFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00-05:00")

            val releaseDateISO =  date.format(outputFormatter)

            println("releaseDateISO ${releaseDateISO}")

            val newAlbum = AlbumJsonRequest(
                name = name.value,
                cover = cover.value,
                releaseDate = releaseDateISO,
                description = description.value,
                genre = genre.value,
                recordLabel = recordLabel.value,
            )

            viewModel.insertAlbum(newAlbum)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        BasicInput(
            field = name,
            counter = true,
            counterMaxLength = AlbumViewModel.NAME_MAX_LENGTH,
            onValueChanged = { updatedName ->
                name = updatedName
            },
            formPlaceholder = "Nombre",
            testTag = "create-name"
        )

        BasicInput(
            field = cover,
            onValueChanged = { updatedCover ->
                cover = updatedCover
            },
            formPlaceholder = "URL de la portada",
            testTag = "create-cover"
        )

        Selector(
            field = genre,
            options = genreOptions,
            onValueChanged = { updatedGenre ->
                genre = updatedGenre
            },
            fieldPlaceholder = "Género",
            testTag = "create-genre"
        )

        OutlinedTextField(
            value = releaseDate.value,
            onValueChange = {},
            enabled = false,
//            readOnly = true,
            label = { Text("Fecha de estreno") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { showDatePicker = true },
                    onClickLabel = "Abrir calendario y escoger fecha"
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
                        },
                        modifier = Modifier.semantics { this.contentDescription = "Seleccionar fecha" }
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

        Selector(
            field = recordLabel,
            options = recordLabelOptions,
            onValueChanged = { updatedRecordLabel ->
                recordLabel = updatedRecordLabel
            },
            fieldPlaceholder = "Disquera",
            testTag = "create-recordLabel"
        )

        BasicInput(
            field = description,
            onValueChanged = { updatedDescription ->
                description = updatedDescription
            },
            formPlaceholder = "Descripción",
            minLines = 3,
            counter = true,
            counterMaxLength = AlbumViewModel.DESCRIPTION_MAX_LENGTH,
            testTag = "create-description"
        )

        Button(
            onClick = { validateForm() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("album-submit")
                .semantics { this.contentDescription = "Validar información y agregar álbum" },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )

        ) {
            if (formEnabled) {
                Text(
                    text = stringResource(id = R.string.add),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(
                        with(LocalDensity.current) {
                            MaterialTheme.typography.titleMedium.lineHeight.toDp()
                        }
                    )
                )
            }
        }

    }
}