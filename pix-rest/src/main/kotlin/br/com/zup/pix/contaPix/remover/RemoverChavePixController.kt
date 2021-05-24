package br.com.zup.pix.contaPix.remover

import br.com.zup.pix.PixServerRemoveServiceGrpc
import br.com.zup.pix.contaPix.ErrorResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/remover/chave")
class RemoverChavePixController(
    @Inject val grpcClient: PixServerRemoveServiceGrpc.PixServerRemoveServiceBlockingStub
) {
    @Delete
    fun remover(@Body @Valid request: RemoverChaveRequest): HttpResponse<Any> {
        val requestGrpc = request.toModel()

        try {
            val response = grpcClient.remover(requestGrpc)
            return HttpResponse.ok()
        }catch (e: Exception){
            val statusCode = (e as StatusRuntimeException).status.code

            return when (statusCode) {
                Status.NOT_FOUND.code -> HttpResponse.notFound(ErrorResponse("Chave pix não localizada"))
                Status.INVALID_ARGUMENT.code -> HttpResponse.badRequest(ErrorResponse("Dados invalidos"))
                else -> HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}