package br.com.zup.pix.contaPix.registrar

import br.com.zup.pix.PixServerRegistrarServiceGrpc
import br.com.zup.pix.contaPix.ErrorResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/registrar/cliente/{clienteId}")
class RegistrarChavePixController(
    @Inject val grpcClient: PixServerRegistrarServiceGrpc.PixServerRegistrarServiceBlockingStub
) {
    @Post
    fun registar(@PathVariable clienteId: String, @Body @Valid request: NovaChaveRequest)
            : HttpResponse<Any> {
        val requestGrpc = request.toModel(clienteId)

        try {
            val response = grpcClient.registrar(requestGrpc)
            return HttpResponse.created(
                HttpResponse.uri("/registrar/pix/$clienteId/pix/${response.pixId}")
            )
        } catch (e: Exception) {
            val statusCode = (e as StatusRuntimeException).status.code

            return when (statusCode) {
                Status.NOT_FOUND.code -> HttpResponse.notFound(ErrorResponse("Cliente: $clienteId não encontrado"))
                Status.INVALID_ARGUMENT.code -> HttpResponse.badRequest(ErrorResponse("Dados invalidos"))
                Status.ALREADY_EXISTS.code -> HttpResponse.unprocessableEntity<Any?>()
                else -> HttpResponse.serverError(ErrorResponse("Erro interno"))
            }
        }
    }
}