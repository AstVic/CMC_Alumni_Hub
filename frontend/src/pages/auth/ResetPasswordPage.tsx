import { useState, type FormEvent } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { publicApi } from '../../api/publicApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { Input, Label, FieldError } from '../../components/ui/Field';
import { AuthShell } from './AuthShell';

export function ResetPasswordPage() {
  const [params] = useSearchParams();
  const token = params.get('token') ?? '';
  const navigate = useNavigate();
  const { notify } = useToast();

  const [newPassword, setNewPassword] = useState('');
  const [newPasswordConfirm, setNewPasswordConfirm] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (!token) {
    return (
      <AuthShell>
        <h1 className="text-2xl font-semibold text-brand-950">Ссылка недействительна</h1>
        <p className="mt-2 text-sm text-brand-900/60">
          Ссылка для сброса пароля некорректна. Запросите новую на странице восстановления.
        </p>
        <Link to="/forgot-password" className="mt-6 inline-block">
          <Button variant="secondary">Восстановить пароль</Button>
        </Link>
      </AuthShell>
    );
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    if (newPassword !== newPasswordConfirm) {
      setError('Пароли не совпадают');
      return;
    }
    setLoading(true);
    try {
      await publicApi.resetPassword(token, newPassword, newPasswordConfirm);
      notify('Пароль обновлён. Войдите с новым паролем.', 'success');
      navigate('/login', { replace: true });
    } catch (err) {
      setError(apiErrorMessage(err, 'Не удалось сбросить пароль'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell>
      <h1 className="mb-1 text-2xl font-semibold text-brand-950">Новый пароль</h1>
      <p className="mb-6 text-sm text-brand-900/60">Задайте новый пароль для вашего аккаунта.</p>
      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <Label htmlFor="new-password">Новый пароль</Label>
          <Input
            id="new-password"
            type="password"
            autoComplete="new-password"
            required
            minLength={8}
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder="Минимум 8 символов"
          />
        </div>
        <div>
          <Label htmlFor="new-password-confirm">Повторите пароль</Label>
          <Input
            id="new-password-confirm"
            type="password"
            autoComplete="new-password"
            required
            value={newPasswordConfirm}
            onChange={(e) => setNewPasswordConfirm(e.target.value)}
          />
        </div>
        <FieldError>{error}</FieldError>
        <Button type="submit" className="w-full" loading={loading}>
          Сохранить пароль
        </Button>
      </form>
    </AuthShell>
  );
}
