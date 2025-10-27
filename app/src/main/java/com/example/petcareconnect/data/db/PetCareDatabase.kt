package com.example.petcareconnect.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.petcareconnect.R
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
    version = 27, // ⬆ sube versión para forzar recreación de BD
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
                    .fallbackToDestructiveMigration() // Regenera si cambia versión
                    .addCallback(SeedDataCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }


      //Inserta datos iniciales al abrir la base de datos.

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

                    //  Usuarios base
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

                    //  Categorías base
                    val categoriasBase = listOf("Alimentos", "Accesorios", "Higiene", "Salud", "Juguetes")
                    if (categoriaDao.getAllOnce().isEmpty()) {
                        categoriasBase.forEach { categoriaDao.insert(Categoria(nombre = it)) }
                        Log.i("PetCareDB", " Categorías base creadas")
                    }

                    //  Estados base
                    if (estadoDao.getAllOnce().isEmpty()) {
                        estadoDao.insert(Estado(idEstado = 1, nombre = "Activo"))
                        estadoDao.insert(Estado(idEstado = 2, nombre = "Inactivo"))
                        Log.i("PetCareDB", " Estados base creados")
                    }

                    //  Productos base
                    if (productoDao.getAllOnce().isEmpty()) {
                        val categorias = categoriaDao.getAllOnce()
                        val estadoActivo = estadoDao.getAllOnce()
                            .firstOrNull { it.nombre == "Activo" }?.idEstado ?: 1

                        val alimentosId = categorias.firstOrNull { it.nombre == "Alimentos" }?.idCategoria ?: 1
                        val accesoriosId = categorias.firstOrNull { it.nombre == "Accesorios" }?.idCategoria ?: 2
                        val higieneId = categorias.firstOrNull { it.nombre == "Higiene" }?.idCategoria ?: 3
                        val saludId = categorias.firstOrNull { it.nombre == "Salud" }?.idCategoria ?: 4
                        val juguetesId = categorias.firstOrNull { it.nombre == "Juguetes" }?.idCategoria ?: 5

                        val productosBase = listOf(
                            //  Alimentos
                            Producto(
                                nombre = "Alimento para Perro DogChow 3kg",
                                precio = 15990.0,
                                stock = 25,
                                categoriaId = alimentosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.comida_perrodogchow
                            ),
                            Producto(
                                nombre = "Alimento Whiskas Gato 2,7kg",
                                precio = 13990.0,
                                stock = 30,
                                categoriaId = alimentosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.comidawhiskas_gato
                            ),
                            Producto(
                                nombre = "Snack Dental Pedigree 7un",
                                precio = 4990.0,
                                stock = 40,
                                categoriaId = alimentosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.snack_dentalpedigree
                            ),

                            // Accesorios
                            Producto(
                                nombre = "Correa Retráctil Azul",
                                precio = 8990.0,
                                stock = 15,
                                categoriaId = accesoriosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.correa_retractilazul
                            ),
                            Producto(
                                nombre = "Collar Rojo Ajustable",
                                precio = 4990.0,
                                stock = 25,
                                categoriaId = accesoriosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.collar_rojo
                            ),
                            Producto(
                                nombre = "Plato Doble Inoxidable",
                                precio = 6990.0,
                                stock = 20,
                                categoriaId = accesoriosId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.plato_doble
                            ),

                            // Higiene
                            Producto(
                                nombre = "Shampoo PelitoSuave para Gatos",
                                precio = 7990.0,
                                stock = 20,
                                categoriaId = higieneId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.shampoo_gato
                            ),
                            Producto(
                                nombre = "Toallitas Húmedas PetClean 50un",
                                precio = 5990.0,
                                stock = 30,
                                categoriaId = higieneId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.toallitas_petclean
                            ),
                            Producto(
                                nombre = "Cortaúñas de Acero Inoxidable",
                                precio = 4990.0,
                                stock = 18,
                                categoriaId = higieneId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.cortaunias
                            ),

                            //  Salud
                            Producto(
                                nombre = "Vitaminas Vita C",
                                precio = 12990.0,
                                stock = 25,
                                categoriaId = saludId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.vitaminas_vitac
                            ),
                            Producto(
                                nombre = "Antipulgas NexGard 10-25kg",
                                precio = 15990.0,
                                stock = 20,
                                categoriaId = saludId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.antipulgasnexgard
                            ),
                            Producto(
                                nombre = "Collar Antipulgas Bayer",
                                precio = 8990.0,
                                stock = 30,
                                categoriaId = saludId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.collar_antipulgas
                            ),

                            // Juguetes
                            Producto(
                                nombre = "Pelota Masticable de Goma",
                                precio = 3990.0,
                                stock = 40,
                                categoriaId = juguetesId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.juguete_goma
                            ),
                            Producto(
                                nombre = "Ratón de Tela para Gatos",
                                precio = 2990.0,
                                stock = 35,
                                categoriaId = juguetesId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.raton_tela
                            ),
                            Producto(
                                nombre = "Cuerda Mordedora Grande",
                                precio = 5990.0,
                                stock = 25,
                                categoriaId = juguetesId,
                                estadoId = estadoActivo,
                                imagenResId = R.drawable.cuerda_mordedora
                            )
                        )

                        productosBase.forEach { productoDao.insert(it) }
                        Log.i("PetCareDB", " Productos base creados con imágenes locales y categorías completas")
                    }

                    Log.i("PetCareDB", "Datos base cargados correctamente")

                } catch (e: Exception) {
                    Log.e("PetCareDB", "Error al insertar datos base: ${e.message}")
                }
            }
        }
    }
}
