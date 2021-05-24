package br.com.zup.pix.contaPix.detalhar

import br.com.zup.pix.ConsultarChavePixResponse
import br.com.zup.pix.TipoConta
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class ConsultaResponse(response: ConsultarChavePixResponse) {
    val pixId = response.pixId
    val tipo = response.chave.tipoChave
    val chave = response.chave.chave

    val criadaEm = response.chave.criadoEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

    val tipoConta = when(response.chave.conta.tipoConta){
        TipoConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
        TipoConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
        else -> "UNKNOWN_CONTA"
    }

    val conta = mapOf(
        Pair("tipoConta",tipoConta),
        Pair("instituicao",response.chave.conta.instituicao),
        Pair("titular",response.chave.conta.nomeTitular),
        Pair("cpf",response.chave.conta.cpfTitular),
        Pair("agencia",response.chave.conta.agencia),
        Pair("numeroConta",response.chave.conta.numeroConta)
    )
}
