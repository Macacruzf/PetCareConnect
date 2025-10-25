package com.example.petcareconnect.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petcareconnect.R // 👈 necesario para usar imágenes locales
import com.example.petcareconnect.data.db.dao.*
import com.example.petcareconnect.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Categoria::class,
        Estado::class,
        Producto::class,
        Venta::class,
        DetalleVenta::class,
        Usuario::class,
        Ticket::class
    ],
    version = 24, // ⬆️ subimos versión para recrear BD
    exportSchema = false
)
abstract class PetCareDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun estadoDao(): EstadoDao
    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun detalleVentaDao(): DetalleVentaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: PetCareDatabase? = null

        fun getDatabase(context: Context): PetCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PetCareDatabase::class.java,
                    "petcare_db"
                )
                    .fallbackToDestructiveMigration() // 🔄 Regenera si cambia versión
                    .addCallback(SeedDataCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * 🌱 Inserta datos iniciales al abrir la base de datos.
     */
    private class SeedDataCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onOpen(db)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = INSTANCE ?: return@launch
                    val usuarioDao = database.usuarioDao()
                    val categoriaDao = database.categoriaDao()
                    val estadoDao = database.estadoDao()
                    val productoDao = database.productoDao()

                    // 👤 Usuarios base
                    if (usuarioDao.getByEmail("admin@petcare.cl") == null) {
                        usuarioDao.insert(
                            Usuario(
                                nombre = "Administrador",
                                email = "admin@petcare.cl",
                                telefono = "999999999",
                                password = "Admin.123",
                                rol = "ADMIN"
                            )
                        )
                    }
                    if (usuarioDao.getByEmail("cliente@petcare.cl") == null) {
                        usuarioDao.insert(
                            Usuario(
                                nombre = "Cliente de Prueba",
                                email = "cliente@petcare.cl",
                                telefono = "888888888",
                                password = "Cliente.123",
                                rol = "CLIENTE"
                            )
                        )
                    }

                    // 🏷️ Categorías base
                    val categoriasBase =
                        listOf("Alimentos", "Accesorios", "Higiene", "Salud", "Juguetes")
                    if (categoriaDao.getAllOnce().isEmpty()) {
                        categoriasBase.forEach { categoriaDao.insert(Categoria(nombre = it)) }
                        Log.i("PetCareDB", "✅ Categorías base creadas")
                    }

                    // ⚙️ Estados base
                    if (estadoDao.getAllOnce().isEmpty()) {
                        estadoDao.insert(Estado(idEstado = 1, nombre = "Activo"))
                        estadoDao.insert(Estado(idEstado = 2, nombre = "Inactivo"))
                        Log.i("PetCareDB", "✅ Estados base creados")
                    }

                    // 🐾 Productos base
                    if (productoDao.getAllOnce().isEmpty()) {
                        val categorias = categoriaDao.getAllOnce()
                        val estadoActivo =
                            estadoDao.getAllOnce().firstOrNull { it.nombre == "Activo" }?.idEstado
                                ?: 1

                        val alimentosId =
                            categorias.firstOrNull { it.nombre == "Alimentos" }?.idCategoria ?: 1
                        val accesoriosId =
                            categorias.firstOrNull { it.nombre == "Accesorios" }?.idCategoria ?: 2
                        val higieneId =
                            categorias.firstOrNull { it.nombre == "Higiene" }?.idCategoria ?: 3

                        val productosBase = listOf(
                            Producto(
                                nombre = "Alimento para Perro DogChow 3kg",
                                precio = 15990.0,
                                stock = 25,
                                categoriaId = alimentosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.comida_perrodogchow
                            ),
                            Producto(
                                nombre = "Juguete de Goma para Mascotas",
                                precio = 4990.0,
                                stock = 40,
                                categoriaId = accesoriosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.juguete_goma
                            ),
                            Producto(
                                nombre = "Shampoo para Gatos PelitoSuave",
                                precio = 7990.0,
                                stock = 20,
                                categoriaId = higieneId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.shampoo_gato
                            )
                        )

                        productosBase.forEach { productoDao.insert(it) }
                        Log.i("PetCareDB", "✅ Productos base creados con imágenes locales")
                    }

                    Log.i("PetCareDB", "🌱 Datos base cargados correctamente")

                } catch (e: Exception) {
                    Log.e("PetCareDB", "⚠️ Error al insertar datos base: ${e.message}")
                }
            }
        }
    }
}