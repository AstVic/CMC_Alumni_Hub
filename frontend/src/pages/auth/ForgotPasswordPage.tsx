import { useState, type FormEvent } from 'react';
import { Link } from 'react-router-dom';
import { publicApi } from '../../api/publicApi';
import { apiErrorMessage } from '../../api/httpClient';
import { Button } from '../../components/ui/Button';
import { Input, Label, FieldError } from '../../components/ui/Field';
import { AuthShell } from './AuthShell';

export function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState<string | null>(null);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await publicApi.forgotPassword(email.trim());
      setSent(res.message);
    } catch (err) {
      setError(apiErrorMessage(err, 'Не удалось отправить письмо'));
    } finally {
      setLoading(false);
    }
  };

  if (sent) {
    return (
      <AuthShell>
        <h1 className="text-2xl font-semibold text-brand-950">Проверьте почту</h1>
        <p className="mt-2 text-sm text-brand-900/60">{sent}</p>
        <Link to="/login" className="mt-6 inline-block">
          <Button variant="secondary">Вернуться ко входу</Button>
        </Link>
      </AuthShell>
    );
  }

  return (
    <AuthShell>
      <h1 className="mb-1 text-2xl font-semibold text-brand-950">Восстановление пароля</h1>
      <p className="mb-6 text-sm text-brand-900/60">
        Введите email вашего аккаунта — мы пришлём ссылку для сброса пароля.
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
        <FieldError>{error}</FieldError>
        <Button type="submit" className="w-full" loading={loading}>
          Отправить ссылку
        </Button>
      </form>
      <Link
        to="/login"
        className="mt-4 inline-block text-sm text-brand-600 hover:text-brand-700"
      >
        ← Вернуться ко входу
      </Link>
    </AuthShell>
  );
}
