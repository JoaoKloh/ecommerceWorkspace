"use client"

import Link from "next/link"
import { useEffect, useState } from "react"
import { Instagram, Phone, Mail, MapPin, ShieldCheck } from "lucide-react"

export function StoreFooter() {
  const [isAdmin, setIsAdmin] = useState(false)

  useEffect(() => {
    const checkAdminStatus = () => {
      const authCookie = document.cookie.match(new RegExp('(^| )is-authenticated=([^;]+)'))
      const isAuthenticated = authCookie && authCookie[2] === "true"

      const storedRoles = localStorage.getItem("user-roles")
      const roles = storedRoles ? JSON.parse(storedRoles) : []
      const hasAdminRole = roles.includes("ROLE_ADMIN")

      setIsAdmin(isAuthenticated && hasAdminRole)
    }

    checkAdminStatus()
    window.addEventListener("storage", checkAdminStatus)
    return () => window.removeEventListener("storage", checkAdminStatus)
  }, [])

  return (
    // 🍏 Atualizado para bg-white puro e dark:bg-black para manter o padrão Apple
    <footer className="w-full bg-white dark:bg-[#000000] border-t border-border/40 transition-colors">
      <div className="mx-auto max-w-7xl px-5 py-12 md:px-8 md:py-16">
        
        {/* 🍏 GRID RESPONSIVO: 1 coluna nativa no mobile, alinhamento cirúrgico à esquerda */}
        <div className="grid grid-cols-1 gap-y-10 sm:grid-cols-2 lg:grid-cols-4 lg:gap-x-8 lg:gap-y-0">
          
          {/* Seção Brand */}
          <div className="space-y-3">
            <h3 className="text-sm font-semibold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
              Marie e Anne
            </h3>
            <p className="text-xs leading-relaxed text-[#86868b] dark:text-[#86868b] max-w-xs text-pretty">
              Doces artesanais feitos com amor e ingredientes selecionados. 
              Transformamos momentos especiais em memórias doces.
            </p>
          </div>

          {/* Seção Links Rápidos */}
          <div className="space-y-3">
            <h4 className="text-sm font-semibold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
              Explorar
            </h4>
            <nav className="flex flex-col gap-2.5">
              <Link href="/cardapio" className="text-xs text-[#86868b] hover:text-[#1d1d1f] dark:hover:text-[#f5f5f7] transition-colors w-fit">
                Cardápio
              </Link>
              <Link href="/sobre" className="text-xs text-[#86868b] hover:text-[#1d1d1f] dark:hover:text-[#f5f5f7] transition-colors w-fit">
                Sobre Nós
              </Link>
              <Link href="/contato" className="text-xs text-[#86868b] hover:text-[#1d1d1f] dark:hover:text-[#f5f5f7] transition-colors w-fit">
                Contato
              </Link>
              
              {isAdmin && (
                <Link 
                  href="/admin" 
                  className="mt-1 inline-flex items-center gap-1.5 text-xs font-medium text-[#0066cc] dark:text-[#2997ff] hover:underline"
                >
                  <ShieldCheck className="h-3.5 w-3.5" />
                  Painel Administrativo
                </Link>
              )}
            </nav>
          </div>

          {/* Seção Contato */}
          <div className="space-y-3">
            <h4 className="text-sm font-semibold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
              Canais de Atendimento
            </h4>
            <div className="flex flex-col gap-2.5">
              <a href="tel:+5524988654555" className="inline-flex items-center gap-2 text-xs text-[#86868b] hover:text-[#1d1d1f] dark:hover:text-[#f5f5f7] transition-colors w-fit">
                <Phone className="h-3.5 w-3.5 flex-shrink-0" />
                (24) 98865-4555
              </a>
              <a href="mailto:chocolatesmarieeanne@gmail.com" className="inline-flex items-center gap-2 text-xs text-[#86868b] hover:text-[#1d1d1f] dark:hover:text-[#f5f5f7] transition-colors w-fit">
                <Mail className="h-3.5 w-3.5 flex-shrink-0" />
                chocolatesmarieeanne@gmail.com
              </a>
              <div className="inline-flex items-start gap-2 text-xs text-[#86868b] leading-normal">
                <MapPin className="h-3.5 w-3.5 flex-shrink-0 mt-0.5" />
                <span>Av. Piabanha, 559 - Centro<br />Petrópolis - RJ</span>
              </div>
            </div>
          </div>

          {/* Seção Social */}
          <div className="space-y-3">
            <h4 className="text-sm font-semibold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
              Comunidade
            </h4>
            <div className="flex flex-col gap-2.5">
              <a
                href="https://instagram.com/chocolatesmarieeanne"
                target="_blank"
                rel="noopener noreferrer"
                className="inline-flex items-center gap-2 text-xs text-[#86868b] hover:text-[#1d1d1f] dark:hover:text-[#f5f5f7] transition-colors w-fit group"
              >
                <Instagram className="h-4 w-4 transition-transform group-hover:scale-105" />
                <span>Instagram</span>
              </a>
              <p className="text-[11px] leading-normal text-[#86868b]/80 max-w-[200px]">
                Acompanhe os bastidores e lançamentos semanais da nossa cozinha.
              </p>
            </div>
          </div>

        </div>

        {/* 🍏 RODAPÉ LEGAL: Linha divisória ainda mais sutil sobre o fundo branco */}
        <div className="mt-12 pt-5 border-t border-border/30 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between text-left">
          <p className="text-[11px] text-[#86868b]">
            Copyright &copy; {new Date().getFullYear()} Chocolates Marie e Anne. Todos os direitos reservados.
          </p>
          <p className="text-[11px] text-[#86868b]/60 sm:text-right">
            Petrópolis, Rio de Janeiro.
          </p>
        </div>

      </div>
    </footer>
  )
}