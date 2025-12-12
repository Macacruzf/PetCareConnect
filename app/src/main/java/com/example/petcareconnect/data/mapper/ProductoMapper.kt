package com.example.petcareconnect.data.mapper

import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.utils.getDrawableProducto

/**
 * Mapper para convertir ProductoDto (del backend) a Producto (modelo local).
 *
 * ✅ MIGRACIÓN A IMÁGENES DESDE BASE DE DATOS:
 * - Si el backend devuelve `imagenUrl`, se construye la URL completa
 * - La URL completa se guarda en `imagenUrl` del modelo local
 * - Como fallback temporal, si no hay imagen remota, se usa drawable local
 */
fun ProductoDto.toLocal(): Producto {
    // Construir URL completa para la imagen desde el backend
    // ⚠️ IMPORTANTE: Esta IP debe coincidir con la de ApiModule
    // - Para EMULADOR: "http://10.0.2.2:8086"
    // - Para DISPOSITIVO FÍSICO: "http://192.168.0.12:8086"
    val baseUrl = "http://26.241.40.40:8086" // ⚠️ Cambia según tu configuración
    val fullImagenUrl = if (!imagenUrl.isNullOrBlank()) {
        // El backend devuelve algo como "/api/v1/productos/1/imagen"
        // Construimos la URL completa
        "$baseUrl$imagenUrl"
    } else {
        null
    }

    return Producto(
        idProducto = idProducto ?: 0L,
        nombre = nombre,
        precio = precio,
        stock = stock,
        categoriaId = categoria.idCategoria,
        estado = EstadoProducto.valueOf(estado.uppercase()),

        // ✅ URL completa de la imagen desde el backend
        imagenUrl = fullImagenUrl,

        // imagenUri se usa solo para edición local (cámara/galería)
        imagenUri = null,

        // ✅ Fallback: si backend NO trae imagen → usar drawable por defecto
        // (Este mecanismo es temporal hasta que todas las imágenes estén en BD)
        imagenResId = if (fullImagenUrl == null) getDrawableProducto(nombre) else null
    )
}

