package br.com.zup.pix.contaPix.listagem

import br.com.zup.pix.ListarChavePixResponse
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChavePixResponse(chavePix: ListarChavePixResponse.ChavePix) {
    val id = chavePix.pixId
    val tipoChave = chavePix.tipoChave
    val chave = chavePix.chave
    val tipoConta = chavePix.tipoConta
    val criadoEm = chavePix.criadoEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}
