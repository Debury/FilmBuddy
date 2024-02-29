package cz.mendelu.pef.xchomo.filmbuddy

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import com.google.firebase.FirebaseApp
import cz.mendelu.pef.xchomo.filmbuddy.di.viewModelModule
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.utils.LanguageUtils
import java.util.*

class FilmBuddyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        FirebaseApp.initializeApp(this)
        setAppLanguage()
        startKoin {
            androidContext(this@FilmBuddyApplication)
            modules(listOf(
                viewModelModule,
            ))
        }
    }

    private fun setAppLanguage() {
        val isSlovak = LanguageUtils.isLanguageSlovak()
        val locale = if (isSlovak) {
            Locale("sk")
        } else {
            Locale("en")
        }
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
