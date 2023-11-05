package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IUserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserViewModelTest {
    class FakeUserRepository: IUserRepository {
        private val flow = MutableSharedFlow<User?>()
        suspend fun emit(value: User?) = flow.emit(value)

        var loginCalled = false

        override fun getUser(): Flow<User?> {
            return flow
        }

        override suspend fun login(userType: UserType) {
            loginCalled = true
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val repository = FakeUserRepository()

        val viewModel = UserViewModel.Factory.create(
            UserViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(UserViewModel.KEY_USER_REPOSITORY, repository)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun getUser() = runTest {
        val repository = FakeUserRepository()

        val viewModel = UserViewModel.Factory.create(
            UserViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(UserViewModel.KEY_USER_REPOSITORY, repository)
            }
        )

        val user = User(UserType.Collector, 1)

        // Initially, there is no user yet
        assertEquals(null, viewModel.user.first())

        // Repository emits user
        repository.emit(user)

        // Then, user is available
        assertEquals(user, viewModel.user.first())
    }

    @Test
    fun canLoginAsCollector() = runTest {
        val repository = FakeUserRepository()

        val viewModel = UserViewModel.Factory.create(
            UserViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(UserViewModel.KEY_USER_REPOSITORY, repository)
            }
        )

        // Initially, status is not logged in
        assertEquals(UserViewModel.LoginUiState.NotLoggedIn, viewModel.status.first())

        // Login as collector
        viewModel.onLogin(UserType.Collector)

        // Then, status is logged in
        assertEquals(UserViewModel.LoginUiState.LoggedIn, viewModel.status.first())
        assertTrue(repository.loginCalled)
    }

    @Test
    fun canLoginAsVisitor() = runTest {
        val repository = FakeUserRepository()

        val viewModel = UserViewModel.Factory.create(
            UserViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(UserViewModel.KEY_USER_REPOSITORY, repository)
            }
        )

        // Initially, status is not logged in
        assertEquals(UserViewModel.LoginUiState.NotLoggedIn, viewModel.status.first())

        // Login as collector
        viewModel.onLogin(UserType.Visitor)

        // Then, status is logged in
        assertEquals(UserViewModel.LoginUiState.LoggedIn, viewModel.status.first())
        assertTrue(repository.loginCalled)
    }
}
