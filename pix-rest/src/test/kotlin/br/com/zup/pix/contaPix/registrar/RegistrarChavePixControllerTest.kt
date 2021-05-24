package br.com.zup.pix.contaPix.registrar

import br.com.zup.pix.ChaveRegistradaResponse
import br.com.zup.pix.GrpcClientFactory
import br.com.zup.pix.PixServerListarServiceGrpc
import br.com.zup.pix.PixServerRegistrarServiceGrpc
import br.com.zup.pix.contaPix.ErrorResponse
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistrarChavePixControllerTest{
    @Inject lateinit var grpcClient: PixServerRegistrarServiceGrpc.PixServerRegistrarServiceBlockingStub

    @field:Client("/")
    @Inject lateinit var client: HttpClient

    @Test
    internal fun `deve registrar uma nova chave`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val responseGrpc = ChaveRegistradaResponse.newBuilder()
            .setClienteId(clientId)
            .setPixId(pixId)
            .build()

        doReturn(responseGrpc).`when`(grpcClient).registrar(Mockito.any())

        val chaveRequest = NovaChaveRequest(TipoChave.CPF, "738.274.864-24", TipoConta.CONTA_CORRENTE)
        val request = HttpRequest.POST("/registrar/cliente/$clientId",chaveRequest)
        val response = client.toBlocking().exchange(request, NovaChaveRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("LOCATION"))
        assertTrue(response.header("LOCATION")!!.contains(pixId))
    }

    @Test
    internal fun `nao deve cadastrar quando o cliente nao for encontrado`(){
        val clientId = UUID.randomUUID().toString()

        doReturn(Status.NOT_FOUND).`when`(grpcClient).registrar(Mockito.any())

        val chaveRequest = NovaChaveRequest(TipoChave.CPF, "738.274.864-24", TipoConta.CONTA_CORRENTE)
        val request = HttpRequest.POST("/registrar/cliente/$clientId",chaveRequest)

        val error = assertThrows<StatusRuntimeException> {
            client.toBlocking().exchange(request, NovaChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente: $clientId não encontrado", status.description)
        }
    }

    @Test
    internal fun `nao deve cadastrar quando algum argumento for invalido`(){
        val clientId = UUID.randomUUID().toString()

        doReturn(Status.INVALID_ARGUMENT).`when`(grpcClient).registrar(Mockito.any())

        val chaveRequest = NovaChaveRequest(TipoChave.CPF, "738.274.864-24", TipoConta.CONTA_CORRENTE)
        val request = HttpRequest.POST("/registrar/cliente/$clientId",chaveRequest)

        val error = assertThrows<StatusRuntimeException> {
            client.toBlocking().exchange(request, NovaChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.fromCodeValue(400).code, status.code)
            assertEquals("Dados invalidos", status.description)
        }
    }

    @Test
    internal fun `nao deve cadastrar quando a chave já existir`(){
        val clientId = UUID.randomUUID().toString()

        doReturn(Status.ALREADY_EXISTS).`when`(grpcClient).registrar(Mockito.any())

        val chaveRequest = NovaChaveRequest(TipoChave.CPF, "738.274.864-24", TipoConta.CONTA_CORRENTE)
        val request = HttpRequest.POST("/registrar/cliente/$clientId",chaveRequest)

        val error = assertThrows<StatusRuntimeException> {
            client.toBlocking().exchange(request, NovaChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.fromCodeValue(422).code, status.code)
        }
    }

    @Test
    internal fun `nao deve cadastrar se ocorrer algum erro`(){
        val clientId = UUID.randomUUID().toString()

        doReturn(HttpResponse.serverError(ErrorResponse("Erro interno"))).`when`(grpcClient).registrar(Mockito.any())

        val chaveRequest = NovaChaveRequest(TipoChave.CPF, "738.274.864-24", TipoConta.CONTA_CORRENTE)
        val request = HttpRequest.POST("/registrar/cliente/$clientId",chaveRequest)

        val error = assertThrows<StatusRuntimeException> {
            client.toBlocking().exchange(request, NovaChaveRequest::class.java)
        }

        with(error){
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status.code)
        }
    }

    //Substituir o cliente que esta no GrpcClientFactory
    @Factory
    @Replaces(factory = GrpcClientFactory::class)
     internal class Clients{
        @Singleton
        fun blockingStub() = Mockito.mock(PixServerRegistrarServiceGrpc.PixServerRegistrarServiceBlockingStub::class.java)
    }

//    @MockBean(PixServerRegistrarServiceGrpc::class)
//    fun pixServerMock(): PixServerRegistrarServiceGrpc{
//        return Mockito.mock(PixServerRegistrarServiceGrpc::class.java)
//    }
//
//    @Factory
//    @Replaces(factory = GrpcClientFactory::class)
//    class Clients {
//        @Singleton
//        fun blockingStub(@GrpcChannel("pix") channel: ManagedChannel)
//                : PixServerRegistrarServiceGrpc.PixServerRegistrarServiceBlockingStub? {
//            return PixServerRegistrarServiceGrpc.newBlockingStub(channel)
//        }
//    }
}