export interface Product {
  id: string
  nome: string
  descricao: string
  preco: number
  desconto: number
  imagem: string
  categoria: string
  disponivel: boolean
}

export interface CartItem {
  produto: Product
  quantidade: number
}

export interface DashboardStats {
  totalVendas: number
  ticketMedio: number
  novosClientes: number
  vendasMensais: {
    mes: string
    vendas: number
  }[]
}

export interface Cliente {
  id: string
  nome: string
  email: string
  telefone: string
  endereco?: Endereco
}

export interface Endereco {
  rua: string
  numero: string
  complemento?: string
  bairro: string
  cidade: string
  estado: string
  cep: string
}

export interface Pedido {
  id: string
  cliente: Cliente
  items: CartItem[]
  total: number
  status: "pendente" | "confirmado" | "preparando" | "entregue" | "cancelado"
  dataEntrega: string
  observacoes?: string
  formaPagamento: "pix" | "cartao" | "dinheiro"
}

export type CheckoutStep = "identificacao" | "entrega" | "pagamento"
