package br.com.zup.pix.contaPix.listagem

import br.com.zup.pix.ListarChavePixResponse

class ListaResponse(val response: ListarChavePixResponse) {
    fun montarLista(): List<ChavePixResponse> {
        return response.listaChavesList.map { ChavePixResponse(it) }
    }
}
