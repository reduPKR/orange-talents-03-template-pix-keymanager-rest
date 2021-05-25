package br.com.zup.pix.contaPix.detalhar

import br.com.zup.pix.*
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ConsultarChaveControllerTest{
//    @Inject
//    lateinit var grpcClient: PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub
//
//    @field:Client("/")
//    @Inject
//    lateinit var client: HttpClient
//
//    @Test
//    internal fun `deve retornar dados pelo clienteId e pixId`(){
//        val clientId = UUID.randomUUID().toString()
//        val pixId = UUID.randomUUID().toString()
//
//        val responseGrpc = ConsultarChavePixResponse.newBuilder()
//            .setClienteId(clientId)
//            .setPixId(pixId)
//            .setChave(
//                ConsultarChavePixResponse.ChavePix.newBuilder()
//                    .setTipoChaveValue(1)
//                    .setChave("64370752019")
//                    .setConta(
//                        ConsultarChavePixResponse.ChavePix.ContaInfo.newBuilder()
//                            .setTipoConta(TipoConta.CONTA_CORRENTE)
//                            .setInstituicao("ITAÚ UNIBANCO S.A.")
//                            .setNomeTitular("Rafael")
//                            .setCpfTitular("64370752019")
//                            .setAgencia("0001")
//                            .setNumeroConta("889976")
//                            .build()
//                    )
//                        .setCriadoEm(Timestamp.newBuilder()
//                        .setSeconds(1621868286)
//                        .setNanos(52526000)
//                        .build())
//                    .build()
//            ).build()
//
//        Mockito.doReturn(responseGrpc).`when`(grpcClient).consultar(Mockito.any())
//
//        val request = HttpRequest.GET<Any>("/consultar/cliente/$clientId/pix/$pixId")
//        val response = client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)
//
//        assertEquals(HttpStatus.OK, response.status)
//    }
//
//    @Factory
//    @Replaces(factory = GrpcClientFactory::class)
//    internal class Clients{
//        @Singleton
//        fun blockingStub() = Mockito.mock(PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub::class.java)
//    }

//    @MockBean(PixServerConsultarServiceGrpc::class)
//    fun pixServerMock(): PixServerConsultarServiceGrpc {
//        return Mockito.mock(PixServerConsultarServiceGrpc::class.java)
//    }
//
//    @Factory
//    @Replaces(factory = GrpcClientFactory::class)
//    class Clients {
//        @Singleton
//        fun blockingStub(@GrpcChannel("pix") channel: ManagedChannel)
//                : PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub? {
//            return PixServerConsultarServiceGrpc.newBlockingStub(channel)
//        }
//    }
}