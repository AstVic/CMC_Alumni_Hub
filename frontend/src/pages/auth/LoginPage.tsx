import { useState, type FormEvent } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useAuth } from '../../auth/useAuth';
import { Button } from '../../components/ui/Button';
import { Input, FieldError, Label } from '../../components/ui/Field';

export function LoginPage() {
  const { setSession } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const token = await authApi.login(email.trim(), password);
      setSession(token);
      const from = (location.state as { from?: string } | null)?.from;
      navigate(from ?? (token.user.role === 'ADMIN' ? '/admin' : '/alumni'), { replace: true });
    } catch (err) {
      setError(apiErrorMessage(err, 'Не удалось войти'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center px-4 py-12">
      <Link to="/" className="mb-8 flex items-center justify-center gap-2">
        <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-brand-600 font-bold text-white">
          A
        </span>
        <span className="text-xl font-semibold text-brand-900">CMC Alumni Hub</span>
      </Link>

      <div className="rounded-2xl bg-white p-8 shadow-card">
        <h1 className="mb-1 text-2xl font-semibold text-brand-950">Вход</h1>
        <p className="mb-6 text-sm text-brand-900/60">
          Для администраторов и выпускников платформы.
        </p>

        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              autoComplete="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
            />
          </div>
          <div>
            <Label htmlFor="password">Пароль</Label>
            <Input
              id="password"
              type="password"
              autoComplete="current-password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
            />
          </div>
          <FieldError>{error}</FieldError>
          <Button type="submit" className="w-full" loading={loading}>
            Войти
          </Button>
        </form>
        <div className="mt-4 text-center">
          <Link to="/forgot-password" className="text-sm text-brand-600 hover:text-brand-700">
            Забыли пароль?
          </Link>
        </div>
      </div>

      <p className="mt-6 text-center text-sm text-brand-900/50">
        Выпускники регистрируются только по приглашению администратора.
      </p>
    </div>
  );
}
