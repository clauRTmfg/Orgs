package br.com.alura.orgs.extensions

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

fun BigDecimal.formataEmReais(): String? {
    val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "br"))
    return formatador.format(this)
}