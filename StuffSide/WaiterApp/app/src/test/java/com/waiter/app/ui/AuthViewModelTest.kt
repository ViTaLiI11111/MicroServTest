package com.waiter.app.ui

import com.waiter.app.core.UserRole
import com.waiter.app.core.Result
import com.waiter.app.data.dto.LoginResponse
import com.waiter.app.data.repo.AuthRepository
import com.waiter.app.ui.auth.AuthUiState
import com.waiter.app.ui.auth.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
public class AuthViewModelTest {
    //мокає репозиторій
    private val repository = mockk<AuthRepository>()

    //тестовий диспетчер для корутин
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp(){
        //підміняє мейн потік на тестовий
        Dispatchers.setMain(testDispatcher)

        //передає мок репозиторій у конструктор вьюмодел
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()//прибирає підміну
    }

    @Test
    fun `login success updates state to Success`() = runTest {
        //Given
        val role = UserRole.WAITER
        val username = "testUser"
        val password = "123"
        val fakeResponse = LoginResponse(userId = 1, username = "testUser", stationId = null)

        //налаштування мок: коли викличуть логін поверни result.Ok
        //coEvery використовуєься для suspend функцій
        coEvery {
            repository.login(role, username, password)
        } returns Result.Ok(fakeResponse)
        //When
        //ми передаємо пусту лямбду {} бо в тесті нaм не важливий callback навігації
        //нам важливий стан ViewModel
        viewModel.login(role, username, password) { _, _, _ -> }

        //прокручуємо час корутин вперед, щоб state встиг оновитись
        testDispatcher.scheduler.advanceUntilIdle()
        //Then
        val state = viewModel.uiState.value
        TestCase.assertEquals(AuthUiState.Success, state)
    }

    @Test
    fun `login failure updates state to Error`() = runTest {
        //Given
        val errorMsg = "Wrong password"
        coEvery {
            repository.login(any(), any(), any())
        } returns Result.Err(Exception(errorMsg))
        //When
        viewModel.login(UserRole.WAITER, "user", "wrong_pass") {_,_,_ -> }

        testDispatcher.scheduler.advanceUntilIdle()
        //Then
        val state = viewModel.uiState.value
        assertTrue(state is AuthUiState.Error)
        assertEquals(errorMsg, (state as AuthUiState.Error).message)
    }
}