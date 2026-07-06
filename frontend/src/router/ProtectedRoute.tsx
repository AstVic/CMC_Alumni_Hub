import { Navigate, useLocation } from 'react-router-dom';
import type { ReactNode } from 'react';
import { useAuth } from '../auth/useAuth';
import { LoadingState } from '../components/ui/States';
import type { Role } from '../types';

/**
 * Guards a route by authentication and (optionally) role.
 */
export function ProtectedRoute({ role, children }: { role?: Role; children: ReactNode }) {
  const { isAuthenticated, user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <LoadingState />;
  }
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  if (role && user?.role !== role) {
    // Logged in but wrong role — send to their own home.
    return <Navigate to={user?.role === 'ADMIN' ? '/admin' : '/alumni'} replace />;
  }
  return <>{children}</>;
}
