package com.example.petcareconnect.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.petcareconnect.data.model.Carrito
import com.example.petcareconnect.data.model.Venta
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/*
 * ---------------------------------------------------------------------------
 * PedidosViewModel.kt
 * ---------------------------------------------------------------------------
 * Este ViewModel controla la gestión de pedidos y ventas dentro de PetCare Connect.
 * Permite registrar pedidos del carrito, marcarlos como entregados y almacenarlos
 * de forma persistente utilizando DataStore (reemplazo moderno de SharedPreferences).
 *
 * A diferencia de una base de datos Room, DataStore aquí se usa como contenedor
 * liviano de persistencia en formato JSON mediante la librería Gson.
 * ---------------------------------------------------------------------------
 */


// ---------------------------------------------------------------------------
// EXTENSIONES Y UTILIDADES
// ---------------------------------------------------------------------------

// Extensión para crear un DataStore único en el contexto de la aplicación.
// Este DataStore almacenará pedidos y ventas en formato JSON.
private val Context.dataStore by preferencesDataStore("pedidos_datastore")

// Función auxiliar que genera la fecha actual en formato legible (día/mes/año hora:minuto).
fun obtenerFechaActual(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date())
}


// ---------------------------------------------------------------------------
// DATA CLASS DEL PEDIDO
// ---------------------------------------------------------------------------
// Representa la estructura de un pedido registrado en la aplicación.
// Contiene los productos, el total, el método de pago y su estado actual.
data class Pedido(
    val id: Int,
    val items: List<Carrito>,
    val total: Double,
    val metodoPago: String,
    val estado: String = "Pendiente",
    val fecha: String = obtenerFechaActual()
)


// ---------------------------------------------------------------------------
// VIEWMODEL DE PEDIDOS Y VENTAS
// ---------------------------------------------------------------------------
// Se extiende de AndroidViewModel para acceder al contexto global del sistema.
// Utiliza DataStore + Gson para guardar los pedidos y ventas en almacenamiento persistente.
//
// ANIMACIÓN REACTIVA:
// Cada cambio emitido en los StateFlow (_pedidos y _historialVentas) provoca que
// las pantallas que observan estos valores (por ejemplo, LazyColumn o Text)
// se recomponen automáticamente en Jetpack Compose, mostrando transiciones suaves
// y actualizaciones en tiempo real sin necesidad de recargar manualmente la vista.
class PedidosViewModel(application: Application) : AndroidViewModel(application) {

    // Conversor JSON <-> Objetos Kotlin
    private val gson = Gson()

    // Claves de almacenamiento en DataStore
    private val pedidosKey = stringPreferencesKey("pedidos_guardados")
    private val historialKey = stringPreferencesKey("ventas_guardadas")

    // Contexto de la aplicación (necesario para acceder al DataStore)
    private val context = getApplication<Application>().applicationContext

    // Flujos reactivamente observables desde la UI
    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos

    private val _historialVentas = MutableStateFlow<List<Venta>>(emptyList())
    val historialVentas: StateFlow<List<Venta>> = _historialVentas

    // Contador interno que simula un ID autoincremental para los pedidos
    private var nextId = 1


    // -----------------------------------------------------------------------
    // BLOQUE DE INICIALIZACIÓN
    // -----------------------------------------------------------------------
    // Carga los pedidos y ventas guardadas en DataStore al iniciar la app.
    // Gracias al uso de viewModelScope, esto se ejecuta en una corrutina,
    // evitando bloqueos en el hilo principal y permitiendo animaciones fluidas
    // de carga (por ejemplo, mostrar un spinner mientras se leen los datos).
    init {
        viewModelScope.launch {
            _pedidos.value = cargarPedidos()
            _historialVentas.value = cargarHistorial()
            nextId = (_pedidos.value.maxOfOrNull { it.id } ?: 0) + 1
        }
    }


    // -----------------------------------------------------------------------
    // REGISTRAR NUEVO PEDIDO
    // -----------------------------------------------------------------------
    // Toma los productos actuales del carrito y los guarda como un pedido.
    // Además, recalcula el ID autoincremental.
    fun registrarPedido(items: List<Carrito>, total: Double, metodoPago: String) {
        if (items.isEmpty()) return

        viewModelScope.launch {
            val nuevo = Pedido(
                id = nextId++,
                items = items,
                total = total,
                metodoPago = metodoPago
            )

            val actualizados = _pedidos.value + nuevo
            _pedidos.value = actualizados
            guardarPedidos(actualizados)
        }
    }


    // -----------------------------------------------------------------------
    // MARCAR PEDIDO COMO ENTREGADO
    // -----------------------------------------------------------------------
    // Cambia el estado de un pedido a "Entregado" y lo añade al historial de ventas.
    // Esta acción también actualiza las pantallas que muestran pedidos o ventas
    // de manera inmediata, gracias al sistema de recomposición de Compose.
    fun marcarComoEntregado(id: Int) {
        viewModelScope.launch {
            val actualizados = _pedidos.value.map {
                if (it.id == id) it.copy(estado = "Entregado") else it
            }
            _pedidos.value = actualizados
            guardarPedidos(actualizados)

            // Crea una nueva venta basada en el pedido entregado.
            val pedidoEntregado = actualizados.find { it.id == id }
            pedidoEntregado?.let {
                val nuevaVenta = Venta(
                    fecha = it.fecha,
                    cliente = "Cliente ${it.id}", // Se puede vincular con currentUser.nombre
                    total = it.total,
                    metodoPago = it.metodoPago
                )

                val ventasActualizadas = _historialVentas.value + nuevaVenta
                _historialVentas.value = ventasActualizadas
                guardarHistorial(ventasActualizadas)
            }
        }
    }


    // -----------------------------------------------------------------------
    // FUNCIONES DE PERSISTENCIA CON DATASTORE
    // -----------------------------------------------------------------------
    // Estas funciones usan Gson para serializar las listas a formato JSON
    // antes de guardarlas en DataStore. Al cargarlas, se deserializan de nuevo
    // a objetos Kotlin.
    //
    // ANIMACIÓN REACTIVA:
    // Cada vez que un flujo es actualizado, Compose vuelve a dibujar la UI
    // (por ejemplo, las pantallas de historial o lista de pedidos) de manera automática.


    private suspend fun guardarPedidos(lista: List<Pedido>) {
        val json = gson.toJson(lista)
        context.dataStore.edit { prefs -> prefs[pedidosKey] = json }
    }

    private suspend fun cargarPedidos(): List<Pedido> {
        val json = context.dataStore.data.map { prefs -> prefs[pedidosKey] ?: "[]" }.first()
        val type = object : TypeToken<List<Pedido>>() {}.type
        return gson.fromJson(json, type)
    }

    private suspend fun guardarHistorial(lista: List<Venta>) {
        val json = gson.toJson(lista)
        context.dataStore.edit { prefs -> prefs[historialKey] = json }
    }

    private suspend fun cargarHistorial(): List<Venta> {
        val json = context.dataStore.data.map { prefs -> prefs[historialKey] ?: "[]" }.first()
        val type = object : TypeToken<List<Venta>>() {}.type
        return gson.fromJson(json, type)
    }


    // -----------------------------------------------------------------------
    // LIMPIAR HISTORIAL (solo para pruebas o reinicios)
    // -----------------------------------------------------------------------
    // Vacía completamente el historial y borra el valor de DataStore.
    // Esto podría usarse en entornos de testeo o al cerrar sesión de administrador.
    fun limpiarHistorial() {
        viewModelScope.launch {
            _historialVentas.value = emptyList()
            context.dataStore.edit { it.remove(historialKey) }
        }
    }
}
