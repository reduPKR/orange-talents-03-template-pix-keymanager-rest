package br.com.zup.pix.contaPix.detalhar

import br.com.zup.pix.ConsultarChavePixRequest
import io.micronaut.core.annotation.Introspected

@Introspected
class ConsultarChaveRequest(
    val chave: String?,
    val filtro: FiltroPorPixId?
) {
    fun filtroPorPixId(): ConsultarChavePixRequest {
        return ConsultarChavePixRequest.newBuilder()
            .setPixId(
                ConsultarChavePixRequest.FiltroPorPixId.newBuilder()
                    .setPixId(filtro?.pixId)
                    .setClienteId(filtro?.clienteId)
                    .build()
            )
            .build()
    }
    fun filtroPorChave(): ConsultarChavePixRequest {
        return ConsultarChavePixRequest.newBuilder()
            .setChave(chave)
            .build()
    }
}
