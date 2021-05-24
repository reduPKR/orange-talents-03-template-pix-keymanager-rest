package br.com.zup.pix.contaPix.remover

import br.com.zup.pix.RemoverChaveRequest
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class RemoverChaveRequest(
    @field:NotBlank
    val clienteId: String?,
    @field:NotBlank
    val pixId: String?,
) {
    fun toModel(): RemoverChaveRequest {
        return RemoverChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .build()
    }


}
