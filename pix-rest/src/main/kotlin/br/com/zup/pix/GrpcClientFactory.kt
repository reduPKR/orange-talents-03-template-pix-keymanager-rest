package br.com.zup.pix

import br.com.zup.pix.PixServer
import br.com.zup.pix.PixServerRegistrarServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("pix") val channel: ManagedChannel) {
    @Singleton
    fun RegistrarChave() = PixServerRegistrarServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun RemoverChave() = PixServerRemoveServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultarChave() = PixServerConsultarServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listarChave() = PixServerListarServiceGrpc.newBlockingStub(channel)
}