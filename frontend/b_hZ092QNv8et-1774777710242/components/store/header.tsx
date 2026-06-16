"use client"

import { useState, useEffect } from "react"
import Link from "next/link"
import { useSearchParams } from "next/navigation"
import { ShoppingBag, Menu, X, User, LogOut } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useCart } from "@/contexts/cart-context"
import { CartDrawer } from "./cart-drawer"
import api from "@/services/api"
import { UserProfileDialog } from "@/components/user/user-profile"

interface NavLink {
  href: string
  label: string
}

const navLinks: NavLink[] = [
  { href: "/", label: "Início" },
  { href: "/cardapio", label: "Cardápio" },
  { href: "/sobre", label: "Sobre" },
  { href: "/contato", label: "Contato" },
]

export function StoreHeader() {
  const { totalItens, setIsCartOpen } = useCart()
  const searchParams = useSearchParams()
  
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isProfileOpen, setIsProfileOpen] = useState(false)

  useEffect(() => {
    const acao = searchParams.get("openProfile")
    if (acao === "pedidos") {
      setIsProfileOpen(true)
      if (typeof window !== "undefined") {
        window.history.replaceState(null, "", window.location.pathname)
      }
    }
  }, [searchParams])

  useEffect(() => {
    const checkAuth = () => {
      const match = document.cookie.match(new RegExp('(^| )is-authenticated=([^;]+)'))
      if (match && match[2] === "true") {
        setIsAuthenticated(true)
      } else {
        setIsAuthenticated(false)
      }
    }

    checkAuth()
    window.addEventListener("focus", checkAuth)
    return () => window.removeEventListener("focus", checkAuth)
  }, [])

  const handleLogout = async () => {
    try {
      await api.logout()
    } catch (error) {
      console.error("Erro ao deslogar")
    } finally {
      document.cookie = "is-authenticated=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;"
      localStorage.removeItem("user-roles")
      setIsAuthenticated(false)
      setIsMobileMenuOpen(false)
      window.location.href = "/"
    }
  }

  return (
    <>
      <header className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto flex h-16 items-center justify-between px-4 lg:px-8">
          <Link href="/" className="flex items-center gap-2 z-50">
            <span className="font-serif text-xl font-semibold tracking-tight text-foreground md:text-2xl">
              Marie e Anne
            </span>
          </Link>

          {/* Links para Desktop */}
          <nav className="hidden items-center gap-8 md:flex">
            {navLinks.map((link: NavLink) => (
              <Link key={link.href} href={link.href} className="text-sm font-medium text-muted-foreground transition-colors hover:text-primary">
                {link.label}
              </Link>
            ))}
          </nav>

          <div className="flex items-center gap-2 z-50">
            {isAuthenticated ? (
              <div className="flex items-center gap-2">
                <Button 
                  variant="ghost" 
                  size="icon" 
                  onClick={() => {
                    setIsProfileOpen(true)
                    setIsMobileMenuOpen(false)
                  }}
                  className="rounded-full bg-secondary border border-border hover:bg-secondary/80"
                  title="Meu Perfil"
                >
                  <User className="h-5 w-5 text-muted-foreground" />
                </Button>
                
                <Button variant="ghost" size="icon" onClick={handleLogout} className="text-muted-foreground hover:text-destructive hidden md:inline-flex">
                  <LogOut className="h-5 w-5" />
                </Button>
              </div>
            ) : (
              <Link href="/auth/login" onClick={() => setIsMobileMenuOpen(false)}>
                <Button variant="outline" size="sm" className="rounded-full px-6">
                  Entrar
                </Button>
              </Link>
            )}

            <Button variant="ghost" size="icon" className="relative text-muted-foreground" onClick={() => setIsCartOpen(true)}>
              <ShoppingBag className="h-5 w-5" />
              {totalItens > 0 && (
                <span className="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-primary text-xs font-medium text-primary-foreground">
                  {totalItens}
                </span>
              )}
            </Button>

            {/* Menu Hambúrguer Inteligente para Mobile */}
            <Button 
              variant="ghost" 
              size="icon" 
              className="md:hidden text-muted-foreground active:scale-95 transition-transform" 
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            >
              {isMobileMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
            </Button>
          </div>
        </div>

        {/* 📱 MENU DROP/DRAWER MOBILE (Estilo Cortina Premium Apple)
            Aparece apenas em resoluções menores que 'md' quando ativo */}
        {isMobileMenuOpen && (
          <div className="absolute top-16 left-0 w-full bg-background border-b border-border shadow-xl md:hidden animate-in fade-in slide-in-from-top-4 duration-200 z-40">
            <nav className="flex flex-col p-5 space-y-4 bg-background/95 backdrop-blur-md">
              {navLinks.map((link: NavLink) => (
                <Link 
                  key={link.href} 
                  href={link.href} 
                  onClick={() => setIsMobileMenuOpen(false)}
                  className="text-base font-medium text-foreground py-2 border-b border-border/40 last:border-0 transition-colors active:text-primary"
                >
                  {link.label}
                </Link>
              ))}
              
              {/* Opção de logout movida de forma nativa para dentro do menu mobile se estiver logado */}
              {isAuthenticated && (
                <button
                  onClick={handleLogout}
                  className="w-full text-left text-base font-medium text-destructive py-2 pt-3 flex items-center gap-2"
                >
                  <LogOut className="h-5 w-5" /> Sair da conta
                </button>
              )}
            </nav>
          </div>
        )}
      </header>

      {/* Overlay e Modais */}
      <CartDrawer />
      
      <UserProfileDialog 
        open={isProfileOpen} 
        onOpenChange={setIsProfileOpen} 
      />
    </>
  )
}