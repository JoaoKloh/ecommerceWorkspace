"use client"

import Link from "next/link"
import Image from "next/image"
import { useEffect, useState } from "react"
import api from "@/services/api" 
import { type Product } from "@/contexts/cart-context"
import { useRouter } from "next/navigation"

// 🛠️ Import do ProductCard mantido com sucesso
import { ProductCard } from "@/components/store/product-card"

export default function HomePage() {
  const router = useRouter()
  const [produtosDestaque, setProdutosDestaque] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [estaAutenticado, setEstaAutenticado] = useState(false)

  useEffect(() => {
    const inicializarHome = async () => {
      try {
        setLoading(true)
        try {
          await api.getProfile()
          setEstaAutenticado(true)
        } catch {
          setEstaAutenticado(false) 
        }
        
        const idLoja = 1 
        const data = await api.getProducts(idLoja)
        const destaques = data.filter((p) => p.estaDisponivel).slice(0, 4)
        setProdutosDestaque(destaques)
      } catch (error) {
        console.error("Erro ao carregar dados da Home:", error)
      } finally {
        setLoading(false)
      }
    }
    inicializarHome()
  }, [])

  const handleCardClickCapture = (e: React.MouseEvent) => {
    if (estaAutenticado) {
      e.stopPropagation()
      e.preventDefault()
      router.push("/cardapio")
    }
  }

  return (
    <div className="w-full min-h-screen bg-[#f5f5f7] dark:bg-[#000000] font-sans antialiased flex flex-col gap-3">
      
      {/* 🍏 HERO BLOCK 1: O Produto Principal em Tela Cheia */}
      <section className="relative w-full h-[80vh] sm:h-[92vh] bg-[#f5f5f7] dark:bg-[#16161d] overflow-hidden flex flex-col items-center justify-start pt-16 sm:pt-20">
        
        {/* Imagem de Fundo Absoluta (Ocupa o bloco inteiro, estilo Banner da Apple) */}
        <div className="absolute inset-0 w-full h-full z-0">
          <Image
            src="/placeholder.svg?height=1000&width=800"
            alt="Barra Dubai Marie & Anne"
            fill
            className="object-cover object-center sm:object-cover"
            priority
          />
          {/* Gradiente sutil para garantir leitura do texto no mobile */}
          <div className="absolute inset-0 bg-gradient-to-b from-black/20 via-transparent to-transparent dark:from-black/40" />
        </div>

        {/* Textos Flutuando por Cima da Imagem */}
        <div className="relative z-10 text-center px-6 space-y-2 max-w-xl mx-auto select-none">
          {/* 🎨 Mantido o tom elegante e contrastante (#9e3a00) para o texto de anúncio */}
          <span className="text-xs sm:text-sm font-semibold tracking-wider text-[#9e3a00] uppercase block">
            Confira o novo lançamento linha premium de chocolates
          </span>
          <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
            Barra Dubai
          </h1>
          <p className="text-lg sm:text-xl text-[#515154] dark:text-[#86868b] font-normal leading-tight text-balance">
            {/* Espaço reservado para subtítulo se necessário futuramente */}
          </p>
        </div>
      </section>

      {/* 🍏 HERO BLOCK 2: Linha Festas */}
      <section className="relative w-full h-[70vh] bg-white dark:bg-[#161617] overflow-hidden flex flex-col items-center justify-start pt-14">
        
        {/* Imagem de Fundo Inteira */}
        <div className="absolute inset-0 w-full h-full z-0">
          <Image
            src="/placeholder.svg?height=800&width=600"
            alt="Doces Finos Personalizados"
            fill
            className="object-cover object-center"
          />
          <div className="absolute inset-0 bg-gradient-to-b from-white/10 via-transparent to-transparent" />
        </div>

        <div className="relative z-10 text-center px-6 space-y-1.5 max-w-lg mx-auto">
          <h2 className="text-3xl sm:text-4xl font-bold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
            Chocolates geracionais
          </h2>
          <p className="text-base sm:text-lg text-[#515154] dark:text-[#86868b]">
            Biscoito canudinho feito por nós a mais de 30 anos.
          </p>
        </div>
      </section>

      {/* 🍏 SECTION 3: Vitrine Integrada */}
      <section className="w-full bg-white dark:bg-[#161617] py-14 px-4 sm:px-6 md:px-8">
        <div className="max-w-7xl mx-auto space-y-6">
          
          <div className="max-w-md space-y-0.5">
            <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
              Vitrine M&A
            </h2>
            <p className="text-sm text-[#86868b]">
              Os queridinhos da nossa cozinha especialmente para você!
            </p>
          </div>

          {loading ? (
  <div className="text-center py-12 text-xs font-medium text-[#86868b] tracking-wide">
    Sincronizando vitrine...
  </div>
) : (
  <div className="grid gap-x-5 gap-y-8 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 pt-2">
    {produtosDestaque.map((produto) => (
      <div 
        key={produto.id} 
        onClick={() => {
          if (estaAutenticado) {
            router.push("/cardapio")
          }
        }}
        // Se o usuário estiver logado, o card inteiro vira um link visual para o cardápio
        className={`w-full transition-all duration-200 ${
          estaAutenticado ? "cursor-pointer active:scale-[0.98]" : ""
        }`}
      >
        {/* Passamos o estado de autenticação para o card saber se muda o comportamento do botão interno dele */}
        <ProductCard produto={produto} estaAutenticado={estaAutenticado} />
      </div>
    ))}
  </div>
)}
        </div>
      </section>

      {/* 🍏 FOOTER BANNER */}
      <section className="bg-[#f5f5f7] dark:bg-[#161617] py-16 px-6 text-center">
        <div className="max-w-md mx-auto space-y-3">
          <h2 className="text-xl sm:text-2xl font-bold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
            Atendimento Personalizado
          </h2>
          <p className="text-xs sm:text-sm text-[#86868b] leading-relaxed text-balance">
            Precisa de um cardápio exclusivo para sua empresa ou festa?
          </p>
          <div className="pt-2">
            {/* 🎨 Alterado de volta para bg-primary e text-primary-foreground (mesmo padrão do carrinho) */}
            <Link 
              href="/contato" 
              className="inline-flex items-center justify-center rounded-full bg-primary hover:bg-primary/90 text-primary-foreground px-6 h-10 text-xs font-medium tracking-wide transition-colors active:scale-[0.98] duration-150"
            >
              Fale Conosco
            </Link>
          </div>
        </div>
      </section>

    </div>
  )
}