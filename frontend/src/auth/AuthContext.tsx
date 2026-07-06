import { createContext, useCallback, useEffect, useMemo, useState, type ReactNode } from 'react';
import type { TokenResponse, User } from '../types';
import { tokenStorage } from './tokenStorage';
import { authApi } from '../api/authApi';

interface AuthContextValue {
  user: User | null;
  loading: boolean;
  isAuthenticated: boolean;
  isAdmin: boolean;
  isAlumni: boolean;
  setSession: (token: TokenResponse) => void;
  logout: () => void;
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Restore the session on load if a token is present.
    if (!tokenStorage.getAccess()) {
      setLoading(false);
      return;
    }
    authApi
      .me()
      .then(setUser)
      .catch(() => {
        tokenStorage.clear();
        setUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  const setSession = useCallback((token: TokenResponse) => {
    tokenStorage.set(token.accessToken, token.refreshToken);
    setUser(token.user);
  }, []);

  const logout = useCallback(() => {
    tokenStorage.clear();
    setUser(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      loading,
      isAuthenticated: user !== null,
      isAdmin: user?.role === 'ADMIN',
      isAlumni: user?.role === 'ALUMNI',
      setSession,
      logout,
    }),
    [user, loading, setSession, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
