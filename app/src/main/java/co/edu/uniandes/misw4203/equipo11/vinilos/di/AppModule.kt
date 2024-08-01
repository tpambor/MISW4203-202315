package co.edu.uniandes.misw4203.equipo11.vinilos.di


import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.AlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumListViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val IoDispatcher = "IoDispatcher"

val appModule = module {
    single(named(IoDispatcher)) { Dispatchers.IO }
    singleOf(::AlbumRepository) { bind<IAlbumRepository>() }
    viewModel { AlbumListViewModel(get(), get(named(IoDispatcher))) }
}
