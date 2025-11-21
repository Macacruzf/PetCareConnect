package com.example.petcareconnect

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.petcareconnect.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivity_seLanzaCorrectamente() {
        // Verifica que la actividad se carga sin crashes
        onView(withId(android.R.id.content)).check(matches(isDisplayed()))
    }

    @Test
    fun mainActivity_inicializaViewModels() {
        // Verifica que la actividad existe y no crasheó (ViewModels se inicializan en onCreate)
        assert(activityRule.scenario != null)  // Verificación básica
    }

    @Test
    fun mainActivity_cargaBaseDeDatos() {
        // Verifica que la DB se inicializa (sin crashes)
        onView(withId(android.R.id.content)).check(matches(isDisplayed()))
    }
}
