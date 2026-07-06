import { useState, type FormEvent } from 'react';
import { authApi } from '../../api/authApi';
import { apiErrorMessage } from '../../api/httpClient';
import { Button } from '../../components/ui/Button';
import { FieldError, Input, Label } from '../../components/ui/Field';
import { useToast } from '../../components/ui/Toast';

export function AdminSecurityPage() {
  const { notify } = useToast();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [newPasswordConfirm, setNewPasswordConfirm] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    if (newPassword !== newPasswordConfirm) {
      setError('Новые пароли не совпадают');
      return;
    }
    setLoading(true);
    try {
      await authApi.changePassword(currentPassword, newPassword, newPasswordConfirm);
      setCurrentPassword('');
      setNewPassword('');
      setNewPasswordConfirm('');
      notify('Пароль изменён', 'success');
    } catch (err) {
      setError(apiErrorMessage(err, 'Не удалось изменить пароль'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-brand-950">Безопасность</h1>
        <p className="mt-1 text-sm text-brand-900/60">Изменение пароля вашей учётной записи.</p>
      </div>

      <form onSubmit={submit} className="max-w-lg space-y-4 rounded-2xl bg-white p-6 shadow-card">
        <div>
          <Label htmlFor="current-password">Текущий пароль</Label>
          <Input
            id="current-password"
            type="password"
            autoComplete="current-password"
            required
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
          />
        </div>
        <div>
          <Label htmlFor="new-password">Новый пароль</Label>
          <Input
            id="new-password"
            type="password"
            autoComplete="new-password"
            minLength={8}
            required
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
          <p className="mt-1 text-xs text-brand-900/50">Минимум 8 символов.</p>
        </div>
        <div>
          <Label htmlFor="new-password-confirm">Повторите новый пароль</Label>
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
        <Button type="submit" loading={loading}>Изменить пароль</Button>
      </form>
    </div>
  );
}
