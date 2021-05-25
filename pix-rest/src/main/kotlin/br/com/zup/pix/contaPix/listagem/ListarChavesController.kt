package br.com.zup.pix.contaPix.listagem

import br.com.zup.pix.ListarChavePixRequest
import br.com.zup.pix.PixServerListarServiceGrpc
import br.com.zup.pix.contaPix.ErrorResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import javax.inject.Inject

@Validated
@Controller("/listar/chaves")
class ListarChavesController(
    @Inject val grpcClient: PixServerListarServiceGrpc.PixServerListarServiceBlockingStub
) {
    @Get("/cliente/{clienteId}")
    fun listar(@PathVariable clienteId: String): HttpResponse<Any>{
        val request = ListarChavePixRequest.newBuilder().setClienteId(clienteId).build()

        try {
            val response = grpcClient.listar(request)
            return HttpResponse.ok(ListaResponse(response).montarLista())
        }catch (e: Exception){
            return when ((e as StatusRuntimeException).status.code) {
                Status.NOT_FOUND.code -> HttpResponse.notFound(ErrorResponse("Cliente não encontrado"))
                Status.INVALID_ARGUMENT.code -> HttpResponse.badRequest(ErrorResponse("Argumentos invalidos"))
                else -> HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}