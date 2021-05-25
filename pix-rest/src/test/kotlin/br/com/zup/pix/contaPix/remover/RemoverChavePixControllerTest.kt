package br.com.zup.pix.contaPix.remover

import br.com.zup.pix.GrpcClientFactory
import br.com.zup.pix.PixServerRemoveServiceGrpc
import br.com.zup.pix.RemoverChaveResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RemoverChavePixControllerTest{
    @Inject
    lateinit var grpcClient: PixServerRemoveServiceGrpc.PixServerRemoveServiceBlockingStub

    @field:Client("/")
    @Inject
    lateinit var client: HttpClient

    @AfterEach
    internal fun tearDown() {
        Mockito.reset(grpcClient)
    }

    @Test
    internal fun `deve remover uma chave`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val responseGrpc = RemoverChaveResponse.newBuilder()
            .setClienteId(clientId)
            .setPixId(pixId)
            .build()

        Mockito.doReturn(responseGrpc).`when`(grpcClient).remover(Mockito.any())

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)
        val response = client.toBlocking().exchange(request, RemoverChaveRequest::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    internal fun `nao deve remover uma chave quando não encontrar a chave`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.remover(Mockito.any()))
            .thenThrow(StatusRuntimeException(Status.NOT_FOUND)::class.java)

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)

        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, RemoverChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    internal fun `nao deve remover uma chave quando os argumentos forem invalidos`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.remover(Mockito.any()))
            .thenThrow(StatusRuntimeException(Status.INVALID_ARGUMENT)::class.java)

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)

        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, RemoverChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve remover uma chave quandoo correr um erro no servidor`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        Mockito.`when`(grpcClient.remover(Mockito.any()))
            .thenThrow(StatusRuntimeException::class.java)

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)

        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, RemoverChaveRequest::class.java)
        }

        with(error){
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, status.code)
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class Clients{
        @Singleton
        fun blockingStub() = Mockito.mock(PixServerRemoveServiceGrpc.PixServerRemoveServiceBlockingStub::class.java)
    }
}