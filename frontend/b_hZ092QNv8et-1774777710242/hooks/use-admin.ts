import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { jwtDecode } from "jwt-decode"

export function useAdminGuard() {
  const router = useRouter()
  const [isAdmin, setIsAdmin] = useState(false)
  const [checking, setChecking] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem("token")

    if (!token) {
      router.push("/login")
      return
    }

    try {
      const decoded: any = jwtDecode(token)
      const roles = decoded.authorities || []

      if (roles.includes("ROLE_ADMIN")) {
        setIsAdmin(true)
      } else {
        // Se for um usuário comum tentando forçar a URL /admin, expulsa ele
        router.push("/")
      }
    } catch (err) {
      localStorage.removeItem("token")
      router.push("/login")
    } finally {
      setChecking(false)
    }
  }, [router])

  return { isAdmin, checking }
}