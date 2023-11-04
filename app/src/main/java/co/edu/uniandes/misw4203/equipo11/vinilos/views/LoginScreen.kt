package co.edu.uniandes.misw4203.equipo11.vinilos.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.edu.uniandes.misw4203.equipo11.vinilos.PreferenceDataStore
import co.edu.uniandes.misw4203.equipo11.vinilos.R
import co.edu.uniandes.misw4203.equipo11.vinilos.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.UserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.UserViewModel
import kotlinx.coroutines.launch

enum class UserType {
    Visitor,
    Collector
}

@Composable
fun LoginScreen(navController: NavHostController) {
//    val preferenceDataStore = PreferenceDataStore(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory,
        extras = MutableCreationExtras(CreationExtras.Empty).apply {
            set(UserViewModel.KEY_USER_REPOSITORY, UserRepository())
        }
    )

    val onLogin: (UserType) -> Unit = { userType ->
        coroutineScope.launch {
            when (userType) {
                UserType.Visitor -> userViewModel.setUser(User("Visitante"))
                UserType.Collector -> userViewModel.setUser(User("Coleccionista"))
            }

            navController.navigate("albums") {
                // Pop up everything as login screen should not be in backstack
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            modifier = Modifier
                .size(230.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(R.drawable.ic_vinilos),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 10.dp),
            text = stringResource(R.string.login_instruction),
        )

        Row {
            Button(
                onClick = { onLogin(UserType.Visitor) },
                modifier = Modifier
                    .padding(0.dp, 0.dp, 16.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) { Text(stringResource(R.string.login_visitor)) }

            Button(
                onClick = { onLogin(UserType.Collector) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) { Text(stringResource(R.string.login_collector)) }
        }
    }
}