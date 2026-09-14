package br.com.devops.devops.entity;

public enum StatusPedido {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    // Itens só podem ser alterados enquanto o pedido não foi entregue nem cancelado
    public boolean permiteAlterarItens() {
        return this != ENTREGUE && this != CANCELADO;
    }
}
