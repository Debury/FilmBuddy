package cz.mendelu.pef.xchomo.filmbuddy.di



import cz.mendelu.pef.xchomo.filmbuddy.firebase.AuthRepository
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { AuthRepository }
    viewModel { RegisterViewModel( get() ) }
    viewModel { LoginViewModel( get() )}
    viewModel { AddViewModel( get() )}
    viewModel { FilmScreenViewModel( get() )}
}
