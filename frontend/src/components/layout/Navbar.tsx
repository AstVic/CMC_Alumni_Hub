import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/useAuth';
import { Button } from '../ui/Button';
import { cn } from '../../utils/cn';

export function Navbar() {
  const { isAuthenticated, isAdmin, isAlumni, logout } = useAuth();
  const navigate = useNavigate();

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    cn(
      'rounded-lg px-3 py-2 text-sm font-medium transition-colors',
      isActive ? 'bg-brand-50 text-brand-700' : 'text-brand-900/70 hover:text-brand-700',
    );

  return (
    <header className="sticky top-0 z-40 border-b border-surface-border bg-white/90 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3 sm:px-6">
        <Link to="/" className="flex items-center gap-2">
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-brand-600 font-bold text-white">
            A
          </span>
          <span className="text-lg font-semibold text-brand-900">CMC Alumni Hub</span>
        </Link>

        <nav className="hidden items-center gap-1 md:flex">
          <NavLink to="/catalog" className={linkClass}>
            Выпускники
          </NavLink>
          {isAlumni && (
            <NavLink to="/alumni" className={linkClass}>
              Личный кабинет
            </NavLink>
          )}
          {isAdmin && (
            <NavLink to="/admin" className={linkClass}>
              Админ-панель
            </NavLink>
          )}
        </nav>

        <div className="flex items-center gap-2">
          {isAuthenticated ? (
            <Button
              variant="secondary"
              size="sm"
              onClick={() => {
                logout();
                navigate('/');
              }}
            >
              Выйти
            </Button>
          ) : (
            <Button size="sm" onClick={() => navigate('/login')}>
              Войти
            </Button>
          )}
        </div>
      </div>
    </header>
  );
}
