package com.example.petcareconnect.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.petcareconnect.data.db.converters.EstadoProductoConverter
import com.example.petcareconnect.data.db.dao.*
import com.example.petcareconnect.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Categoria::class,
        Producto::class,
        Venta::class,
        DetalleVenta::class,
        Usuario::class,
        Ticket::class
    ],
    version = 29,
    exportSchema = false
)
@TypeConverters(EstadoProductoConverter::class)
abstract class PetCareDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
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
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedDataCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedDataCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onOpen(db)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = INSTANCE ?: return@launch

                    val usuarioDao = database.usuarioDao()
                    val categoriaDao = database.categoriaDao()
                    val productoDao = database.productoDao()

                    // ---------------------------------------------------------
                    // USUARIOS BASE (SE MANTIENEN ACTIVOS)
                    // ---------------------------------------------------------
                    if (usuarioDao.getByEmail("admin@petcare.cl") == null) {
                        usuarioDao.insert(
                            Usuario(
                                nombreUsuario = "Administrador",
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
                                nombreUsuario = "Cliente de Prueba",
                                email = "cliente@petcare.cl",
                                telefono = "888888888",
                                password = "Cliente.123",
                                rol = "CLIENTE"
                            )
                        )
                    }

                    // ---------------------------------------------------------
                    // CATEGORÍAS BASE (DESACTIVADAS — MICRO SERVICIO MANDA)
                    // ---------------------------------------------------------
                    /*
                    val categoriasBase = listOf("Alimentos", "Accesorios", "Higiene", "Salud", "Juguetes")
                    if (categoriaDao.getAllOnce().isEmpty()) {
                        categoriasBase.forEach { categoriaDao.insert(Categoria(nombre = it)) }
                        Log.i("PetCareDB", "Categorías base creadas")
                    }
                    */

                    // ---------------------------------------------------------
                    // PRODUCTOS BASE (DESACTIVADOS — MICRO SERVICIO MANDA)
                    // ---------------------------------------------------------
                    /*
                    if (productoDao.getAllOnce().isEmpty()) {
                        val categorias = categoriaDao.getAllOnce()

                        val alimentosId = categorias.first { it.nombre == "Alimentos" }.idCategoria
                        val accesoriosId = categorias.first { it.nombre == "Accesorios" }.idCategoria
                        val higieneId = categorias.first { it.nombre == "Higiene" }.idCategoria
                        val saludId = categorias.first { it.nombre == "Salud" }.idCategoria
                        val juguetesId = categorias.first { it.nombre == "Juguetes" }.idCategoria

                        val productosBase = listOf(
                            // tus productos aquí...
                        )

                        productosBase.forEach { productoDao.insert(it) }
                        Log.i("PetCareDB", "Productos base creados")
                    }
                    */

                } catch (e: Exception) {
                    Log.e("PetCareDB", "Error al insertar datos base: ${e.message}")
                }
            }
        }
    }
}
