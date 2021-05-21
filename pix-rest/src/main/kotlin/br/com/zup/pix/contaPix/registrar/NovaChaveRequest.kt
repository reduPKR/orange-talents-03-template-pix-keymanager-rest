package br.com.zup.pix.contaPix.registrar


import br.com.zup.pix.RegistrarChaveRequest
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.validador.ValidPixKey
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
class NovaChaveRequest(
    @field:NotNull
    val tipoChave: TipoChave?,
    @field:Size(max=77)
    val chave: String?,
    @field:NotNull
    val tipoConta: TipoConta?
) {
    fun toModel(clienteId: String?): RegistrarChaveRequest {
        val tipoChaveAux = tipoChave?.name ?: TipoChave.UNKNOWN_CHAVE.name
        val tipoContaAux = tipoConta?.name ?: TipoConta.UNKNOWN_CONTA.name

        return RegistrarChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .setTipoChave(br.com.zup.pix.TipoChave.valueOf(tipoChaveAux))
            .setChave(chave ?: "")
            .setTipoConta(br.com.zup.pix.TipoConta.valueOf(tipoContaAux))
            .build()
    }
}
