package com.example.petcareconnect.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.petcareconnect.data.db.dao.*
import com.example.petcareconnect.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 🐾 PetCareDatabase
 * Base de datos principal de la aplicación PetCare Connect.
 * Contiene todas las entidades y expone los DAOs correspondientes.
 */
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
    version = 4, // 🔹 incrementa versión si cambias entidades
    exportSchema = false // 🔹 desactiva exportación si no usas schemaLocation
)
abstract class PetCareDatabase : RoomDatabase() {

    // --- DAOs disponibles ---
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

        /**
         * Obtiene o crea la instancia única de la base de datos.
         */
        fun getDatabase(context: Context): PetCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PetCareDatabase::class.java,
                    "petcare_db"
                )
                    .addCallback(DatabaseCallback()) // Insertar datos iniciales
                    .fallbackToDestructiveMigration() // ⚠️ recrea BD si cambias estructura
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Callback que se ejecuta solo al crear la BD por primera vez.
     * Inserta datos iniciales como los estados base.
     */
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    db.execSQL("INSERT INTO estados (nombre) VALUES ('Activo')")
                    db.execSQL("INSERT INTO estados (nombre) VALUES ('Inactivo')")

                    // 🔹 Insertamos un usuario administrador por defecto
                    db.execSQL("""
                        INSERT INTO usuario (nombre, email, telefono, password, rol) 
                        VALUES ('Admin', 'admin@petcare.cl', '999999999', 'admin123', 'ADMIN')
                    """.trimIndent())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
