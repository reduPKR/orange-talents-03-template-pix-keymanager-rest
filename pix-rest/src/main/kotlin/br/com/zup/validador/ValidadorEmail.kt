package br.com.zup.validador

class ValidadorEmail(val chave: String) {
    fun validar(): Boolean {
        return chave.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$".toRegex())
    }
}
