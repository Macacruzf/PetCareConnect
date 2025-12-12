package com.example.petcareconnect.domain.validation

import android.util.Patterns

// =============================================================================
// VALIDACIONES DE USUARIO
// =============================================================================

/**
 * Valida que el email no esté vacío y cumpla con un formato válido
 * @return null si es válido, mensaje de error si no lo es
 */
fun validateEmail(email: String): String? {
    if (email.isBlank()) return "El correo electrónico es obligatorio"

    // Regex permisiva que acepta dominios internacionales (.cl, .ar, .mx, etc.)
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val ok = emailRegex.matches(email.trim())

    return if (!ok) "Formato de correo inválido (ejemplo: usuario@dominio.com)" else null
}

/**
 * Valida que el nombre solo contenga letras, espacios y caracteres especiales españoles
 * @return null si es válido, mensaje de error si no lo es
 */
fun validateNameLettersOnly(name: String): String? {
    if (name.isBlank()) return "El nombre es obligatorio"
    if (name.length < 2) return "El nombre debe tener al menos 2 caracteres"
    if (name.length > 100) return "El nombre es demasiado largo (máx. 100 caracteres)"

    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name.trim())) "Solo se permiten letras y espacios" else null
}

/**
 * Valida que el teléfono tenga solo dígitos y una longitud adecuada
 * @return null si es válido, mensaje de error si no lo es
 */
fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"

    val cleanPhone = phone.replace(Regex("[\\s-()]"), "") // Eliminar espacios, guiones y paréntesis

    if (!cleanPhone.all { it.isDigit() }) return "Solo se permiten números"
    if (cleanPhone.length !in 8..15) return "Debe tener entre 8 y 15 dígitos"

    return null
}

/**
 * Valida seguridad de la contraseña con requisitos mínimos
 * Requisitos: mínimo 8 caracteres, mayúscula, minúscula, número y símbolo
 * @return null si es válida, mensaje de error si no lo es
 */
fun validateStrongPassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length < 8) return "Mínimo 8 caracteres"
    if (!pass.any { it.isUpperCase() }) return "Debe incluir al menos una mayúscula"
    if (!pass.any { it.isLowerCase() }) return "Debe incluir al menos una minúscula"
    if (!pass.any { it.isDigit() }) return "Debe incluir al menos un número"
    if (!pass.any { !it.isLetterOrDigit() }) return "Debe incluir al menos un símbolo (ej: @, #, $)"
    if (pass.contains(' ')) return "No debe contener espacios"

    return null
}

/**
 * Valida que la confirmación de contraseña coincida con la contraseña original
 * @return null si coinciden, mensaje de error si no
 */
fun validateConfirm(pass: String, confirm: String): String? {
    if (confirm.isBlank()) return "Debes confirmar tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}

// =============================================================================
// VALIDACIONES DE PRODUCTOS
// =============================================================================

/**
 * Valida el nombre de un producto
 * @return null si es válido, mensaje de error si no lo es
 */
fun validateProductName(name: String): String? {
    if (name.isBlank()) return "El nombre del producto es obligatorio"
    if (name.length < 3) return "El nombre debe tener al menos 3 caracteres"
    if (name.length > 200) return "El nombre es demasiado largo (máx. 200 caracteres)"

    return null
}

/**
 * Valida el precio de un producto
 * @return null si es válido, mensaje de error si no lo es
 */
fun validatePrice(price: String): String? {
    if (price.isBlank()) return "El precio es obligatorio"

    val precioDouble = price.toDoubleOrNull()

    if (precioDouble == null) return "El precio debe ser un número válido"
    if (precioDouble <= 0) return "El precio debe ser mayor a 0"
    if (precioDouble > 99999999.99) return "El precio es demasiado alto"

    return null
}

/**
 * Valida el stock de un producto
 * @return null si es válido, mensaje de error si no lo es
 */
fun validateStock(stock: String): String? {
    if (stock.isBlank()) return "El stock es obligatorio"

    val stockInt = stock.toIntOrNull()

    if (stockInt == null) return "El stock debe ser un número entero"
    if (stockInt < 0) return "El stock no puede ser negativo"
    if (stockInt > 999999) return "El stock es demasiado alto (máx. 999,999)"

    return null
}

/**
 * Valida la descripción de un producto
 * @return null si es válida, mensaje de error si no lo es
 */
fun validateDescription(description: String): String? {
    if (description.isBlank()) return "La descripción es obligatoria"
    if (description.length < 10) return "La descripción debe tener al menos 10 caracteres"
    if (description.length > 1000) return "La descripción es demasiado larga (máx. 1000 caracteres)"

    return null
}

// =============================================================================
// VALIDACIONES DE PAGO
// =============================================================================

/**
 * Valida el número de tarjeta de crédito/débito (formato básico)
 * @return null si es válido, mensaje de error si no lo es
 */
fun validateCardNumber(cardNumber: String): String? {
    if (cardNumber.isBlank()) return "El número de tarjeta es obligatorio"

    val cleanNumber = cardNumber.replace(" ", "")

    if (!cleanNumber.all { it.isDigit() }) return "Solo se permiten números"
    if (cleanNumber.length !in 13..19) return "Número de tarjeta inválido (13-19 dígitos)"

    // Validación Luhn (checksum)
    if (!isValidLuhn(cleanNumber)) return "Número de tarjeta inválido"

    return null
}

/**
 * Valida el código CVV/CVC de una tarjeta
 * @return null si es válido, mensaje de error si no lo es
 */
fun validateCVV(cvv: String): String? {
    if (cvv.isBlank()) return "El CVV es obligatorio"
    if (!cvv.all { it.isDigit() }) return "El CVV solo debe contener números"
    if (cvv.length !in 3..4) return "El CVV debe tener 3 o 4 dígitos"

    return null
}

/**
 * Valida el nombre del titular de la tarjeta
 * @return null si es válido, mensaje de error si no lo es
 */
fun validateCardHolderName(name: String): String? {
    if (name.isBlank()) return "El nombre del titular es obligatorio"
    if (name.length < 3) return "El nombre debe tener al menos 3 caracteres"

    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name.trim())) "Solo se permiten letras y espacios" else null
}

/**
 * Valida la fecha de expiración de una tarjeta (formato MM/AA)
 * @return null si es válida, mensaje de error si no lo es
 */
fun validateExpiryDate(expiry: String): String? {
    if (expiry.isBlank()) return "La fecha de expiración es obligatoria"

    val parts = expiry.split("/")
    if (parts.size != 2) return "Formato inválido (usar MM/AA)"

    val month = parts[0].toIntOrNull()
    val year = parts[1].toIntOrNull()

    if (month == null || year == null) return "Formato inválido (usar MM/AA)"
    if (month !in 1..12) return "Mes inválido (01-12)"
    if (year < 0) return "Año inválido"

    // Validar que no esté vencida (comparación básica)
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
    val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1

    if (year < currentYear || (year == currentYear && month < currentMonth)) {
        return "La tarjeta ha expirado"
    }

    return null
}

// =============================================================================
// VALIDACIONES DE CANTIDAD Y DIRECCIONES
// =============================================================================

/**
 * Valida una cantidad (para carrito, pedidos, etc.)
 * @return null si es válida, mensaje de error si no lo es
 */
fun validateQuantity(quantity: String, maxStock: Int? = null): String? {
    if (quantity.isBlank()) return "La cantidad es obligatoria"

    val qty = quantity.toIntOrNull()

    if (qty == null) return "La cantidad debe ser un número entero"
    if (qty <= 0) return "La cantidad debe ser mayor a 0"

    if (maxStock != null && qty > maxStock) {
        return "Stock insuficiente (disponible: $maxStock)"
    }

    return null
}

/**
 * Valida una dirección
 * @return null si es válida, mensaje de error si no lo es
 */
fun validateAddress(address: String): String? {
    if (address.isBlank()) return "La dirección es obligatoria"
    if (address.length < 5) return "La dirección debe tener al menos 5 caracteres"
    if (address.length > 200) return "La dirección es demasiado larga (máx. 200 caracteres)"

    return null
}

// =============================================================================
// UTILIDADES
// =============================================================================

/**
 * Algoritmo de Luhn para validar números de tarjeta
 * @return true si el número pasa la validación Luhn
 */
private fun isValidLuhn(cardNumber: String): Boolean {
    var sum = 0
    var alternate = false

    for (i in cardNumber.length - 1 downTo 0) {
        var digit = cardNumber[i].toString().toInt()

        if (alternate) {
            digit *= 2
            if (digit > 9) digit -= 9
        }

        sum += digit
        alternate = !alternate
    }

    return sum % 10 == 0
}