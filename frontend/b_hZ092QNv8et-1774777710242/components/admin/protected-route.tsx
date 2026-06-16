"use client"

import { useRouter } from "next/navigation"
import { useEffect, useState } from "react"

export function AdminGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter()
  const [authorized, setAuthorized] = useState(false)

  useEffect(() => {
    const checkAccess = () => {
      // 1. Verifica o cookie (Certifique-se que o Java envia com este nome exato)
      const authCookie = document.cookie.match(new RegExp('(^| )is-authenticated=([^;]+)'))
      const isAuthenticated = authCookie && authCookie[2] === "true"

      // 2. Recupera as roles (Ajustado para ler a chave "roles" que salvamos no login)
      const storedRoles = localStorage.getItem("roles")
      
      let roles: string[] = []
      try {
        roles = storedRoles ? JSON.parse(storedRoles) : []
      } catch (e) {
        console.error("Erro ao parsear roles do localStorage")
      }

      const isAdmin = roles.includes("ROLE_ADMIN")

      console.log("Debug AdminGuard:", { isAuthenticated, roles, isAdmin }) // LOG DE APOIO

      if (!isAdmin) {
        // Se não for admin, manda para a home
        router.replace("/")
      } else {
        setAuthorized(true)
      }
    }

    checkAccess()
  }, [router])

  // Se não estiver autorizado, não renderiza nada
  if (!authorized) return null

  return <>{children}</>
}