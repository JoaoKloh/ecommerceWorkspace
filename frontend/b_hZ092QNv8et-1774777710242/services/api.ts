import axios from "axios"
import type { Product } from "@/contexts/cart-context"

const httpClient = axios.create({
  baseURL: "/",
  headers: {
    'Content-Type': 'application/json',
    'bypass-tunnel-reminder': 'true',
    'ngrok-skip-browser-warning': 'true'
  },
  withCredentials: true,
});

export const api = {

  // --- AUTENTICAÇÃO (AuthenticationController) ---
  async gerarCodigo(email: string) {
    await httpClient.post("/api/v1/auth/gerarcodigo", { email })
  },
  
  async login({ email, codigo }: { email: string, codigo: string }) {
    const response = await httpClient.post("/api/v1/auth/verificarcodigo", { email, codigo })
    return response.data
  },

  async register(registerData: any) {
    const response = await httpClient.post("/api/v1/auth/register", registerData)
    return response.data
  },

  async logout() {
    await httpClient.post("/api/v1/auth/logout")
  },

  async getProfile() {
    const response = await httpClient.get("/api/v1/clientes/meuPerfil")
    return response.data
  },
  
  async upsertProfile(profileData: any) {
    const response = await httpClient.post("/api/v1/clientes/upsert", profileData)
    return response.data
  },
  
  async createProfile(profileData: any) {
    const response = await httpClient.post("/api/v1/clientes/create", profileData)
    return response.data
  },

  // --- ADMINISTRAÇÃO (AdminController) ---

  createProduct: async (productData: any, imageFile: File, idLoja: number) => {
    const formData = new FormData();

    const dto = {
      nome: productData.nome,
      descricao: productData.descricao,
      preco: Number(productData.preco),
      categoria: productData.categoria,
      estoque: Number(productData.estoque ?? 0),
      desconto: Number(productData.desconto ?? 0),
      estaDisponivel: !!(productData.estaDisponivel ?? productData.disponivel)
    };

    formData.append(
      "produto", 
      new Blob([JSON.stringify(dto)], { type: "application/json" })
    );
    
    formData.append("imagem", imageFile);

    const response = await httpClient.post(`/api/v1/admin/cadastrarproduto?lojaId=${idLoja}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
        "bypass-tunnel-reminder": "true",
      },
    });

    return response.data;
  },

  updateProduct: async (id: number, productData: any, imageFile?: File) => {
    const formData = new FormData();
    
    const dto = {
      nome: productData.nome,
      descricao: productData.descricao,
      preco: productData.preco,
      categoria: productData.categoria,
      estoque: productData.estoque || 0,
      desconto: productData.desconto || 0,
      estaDisponivel: productData.estaDisponivel
    };

    formData.append("produto", new Blob([JSON.stringify(dto)], { type: "application/json" }));
    
    if (imageFile) {
      formData.append("imagem", imageFile);
    }

    const response = await httpClient.put(`/api/v1/admin/atualizar/produto/${id}`, formData, {
      headers: { 
        "Content-Type": "multipart/form-data",
        "bypass-tunnel-reminder": "true"
      },
    });

    return response.data;
  },

  async toggleAvailability(id: number, status: boolean): Promise<void> {
    await httpClient.patch(`/api/v1/admin/alterardisponibilidade/${id}`, null, {
      params: { status }
    })
  },

  async listAllAdmin(): Promise<Product[]> {
    const response = await httpClient.get("/api/v1/admin/lista-completa")
    return response.data
  },

  async deleteProduct(id: number): Promise<void> {
    await httpClient.delete(`/api/v1/admin/deletar/${id}`)
  },

  async cancelOrder(idPedido: number): Promise<void> {
    await httpClient.put(`/api/v1/admin/cancelar/${idPedido}`)
  },
  
  async createOrder(CreateOrderPayload: any) {
    const response = await httpClient.post("/api/v1/pedido/createOrder", CreateOrderPayload)
    return response.data
  },

  async filterOrdersByDate(date: string) {
    const response = await httpClient.get("/api/v1/admin/filtraPedidosData", {
      params: { data: date }
    })
    return response.data
  },

  async filterOrdersByPeriod(start: string, end: string) {
    const response = await httpClient.get("/api/v1/admin/filtraPedidosPeriodo", {
      params: { inicio: start, fim: end }
    })
    return response.data
  },
  
  async getPedidos() {
    const response = await httpClient.get("/api/v1/pedido/meusPedidos")
    return response.data
  },
  
  async getClientePorEmail(email: string) {
    const response = await httpClient.get("/api/v1/admin/filtrarClientesPorEmail", {
      params: { email: email }
    })
    return response.data
  },

  // --- PRODUTOS (PÚBLICO) ---
  
  async getProducts(idLoja: number): Promise<Product[]> {
    const response = await httpClient.get("/api/v1/produtos", {
      params: { lojaId: idLoja }
    })
    
    return response.data.map((p: any) => {
      let enderecoFormatado = "Retirada no Local"
      
      if (p.endereco && typeof p.endereco === 'object') {
        const { rua, numero, bairro, cidade } = p.endereco
        enderecoFormatado = `${rua}, ${numero} - ${bairro}, ${cidade}`
      } else if (typeof p.endereco === 'string') {
        enderecoFormatado = p.endereco
      }

      return {
        id: p.id,
        idLoja: idLoja,
        nome: p.nome,
        descricao: p.descricao,
        categoria: p.categoria,
        imagem: p.imagem,
        preco: p.preco,
        precoComDesconto: p.precoDesconto,
        desconto: p.desconto ?? 0,
        estoque: p.estoque ?? 0,
        estaDisponivel: p.estaDisponivel,
        endereco: enderecoFormatado
      }
    }) // 🍏 CORREÇÃO: Parêntese e chaves fechados corretamente aqui!
  },

  // --- CARRINHO (CarrinhoController) ---

  async getCart() {
    const response = await httpClient.get("/api/v1/carrinho")
    return response.data
  },

  async addToCart(productId: string | number, quantidade: number) {
    const response = await httpClient.post("/api/v1/carrinho/adicionarItem", {
      idItem: productId,
      quantidade
    })
    return response.data
  },

  async removeFromCart(productId: string | number) {
    await httpClient.delete(`/api/v1/carrinho/removerItem/${productId}`)
  },

  async decreaseQuantity(productId: string | number, quantidade: number) {
    const response = await httpClient.put("/api/v1/carrinho/decrementarQuantidade", {
      idItem: productId,
      quantidade
    })
    return response.data
  },

  async clearCart() {
    await httpClient.delete("/api/v1/carrinho/limparCarrinho")
  },
  
  async criarPagamento(pedidoId: number, paymentrequestDTO: any) {
    const pagamento = {
      transactionAmount: Number(paymentrequestDTO.transactionAmount),
      description: paymentrequestDTO.description,
      paymentMethodId: paymentrequestDTO.paymentMethodId,
      token: paymentrequestDTO.token,
      installments: paymentrequestDTO.installments,
      issuerId: paymentrequestDTO.issuerId,
      payer: {
        email: paymentrequestDTO.payer.email,
        firstName: paymentrequestDTO.payer.firstName,
        lastName: paymentrequestDTO.payer.lastName,
        identification: {
          type: paymentrequestDTO.payer.identification.type,
          number: paymentrequestDTO.payer.identification.number
        }
      }
    }
    
    // Ajustado também o template literal da URL para bater com o padrão REST comum
    const response = await httpClient.post(`/api/v1/pagamentos/create/${pedidoId}`, {
      pedidoId: pedidoId,
      PaymentRequestDTO: pagamento
    })
    return response.data
  },
  
  async criarLoja(payload: any) {
    const response = await httpClient.post("/api/v1/pontos/inserirPonto", payload);
    return response;
  },
  
  async deletarLoja(idLoja: number) {
    const response = await httpClient.delete(`/api/v1/pontos/deletarPonto/${idLoja}`);
    return response;
  },
  
  async getLojas() {
    const response = await httpClient.get("/api/v1/pontos/buscarPontos")
    return response.data
  },
  
  async solicitarPagamento(ProcessamentoPgRequestDTO: any) {
    const response = await httpClient.post("/api/v1/pedido/solicitarPagamento", ProcessamentoPgRequestDTO)
    return response.data
  },
  async getCep(cep:string){
    const cepLimpo = cep.replace(/\D/g, '');
    try {
      const response = await axios.get(`https://brasilapi.com.br/api/cep/v1/${cepLimpo}`);
      console.log(response.data); 
    } catch (error) {
      console.error("Erro na busca de CEP:", error);
    }    
  },
  async oauthgoogle(code:string){
      const response = await httpClient.post("api/v1/auth/oauthGoogle",{code})
      return response.data
  }
}

export default api;