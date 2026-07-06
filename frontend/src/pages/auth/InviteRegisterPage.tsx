import { useState, type FormEvent } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { publicApi } from '../../api/publicApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useAuth } from '../../auth/useAuth';
import { Button } from '../../components/ui/Button';
import { Input, Label, FieldError } from '../../components/ui/Field';
import { LoadingState } from '../../components/ui/States';

function AuthShell({ children }: { children: React.ReactNode }) {
  return (
    <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center px-4 py-12">
      <Link to="/" className="mb-8 flex items-center justify-center gap-2">
        <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-brand-600 font-bold text-white">
          A
        </span>
        <span className="text-xl font-semibold text-brand-900">CMC Alumni Hub</span>
      </Link>
      <div className="rounded-2xl bg-white p-8 shadow-card">{children}</div>
    </div>
  );
}

export function InviteRegisterPage() {
  const [params] = useSearchParams();
  const token = params.get('token') ?? '';
  const navigate = useNavigate();
  const { setSession } = useAuth();

  const { data, isLoading } = useQuery({
    queryKey: ['invite', token],
    queryFn: () => publicApi.validateInvite(token),
    enabled: token.length > 0,
    retry: false,
  });

  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [acceptedRules, setAcceptedRules] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  if (!token) {
    return <InviteInvalid />;
  }
  if (isLoading) {
    return (
      <AuthShell>
        <LoadingState label="Проверяем приглашение…" />
      </AuthShell>
    );
  }
  if (!data || data.result === 'INVALID' || data.result === 'USED' || data.result === 'REVOKED') {
    return <InviteInvalid used={data?.result === 'USED'} />;
  }
  if (data.result === 'EXPIRED') {
    return <InviteExpired />;
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    if (password !== passwordConfirm) {
      setError('Пароли не совпадают');
      return;
    }
    if (!acceptedRules) {
      setError('Необходимо принять правила сайта');
      return;
    }
    setLoading(true);
    try {
      const tokenResp = await publicApi.registerByInvite({
        token,
        fullName: fullName.trim(),
        password,
        passwordConfirm,
        acceptedRules,
      });
      setSession(tokenResp);
      navigate('/alumni/onboarding', { replace: true });
    } catch (err) {
      setError(apiErrorMessage(err, 'Не удалось завершить регистрацию'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthShell>
      <h1 className="mb-1 text-2xl font-semibold text-brand-950">Регистрация выпускника</h1>
      <p className="mb-6 text-sm text-brand-900/60">
        Вы приглашены присоединиться к платформе. Создайте аккаунт.
      </p>
      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <Label>Email</Label>
          <Input value={data.email ?? ''} disabled readOnly />
          <p className="mt-1 text-xs text-brand-900/50">Email привязан к приглашению.</p>
        </div>
        <div>
          <Label htmlFor="fullName">ФИО</Label>
          <Input
            id="fullName"
            required
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            placeholder="Иван Иванов"
          />
        </div>
        <div>
          <Label htmlFor="password">Пароль</Label>
          <Input
            id="password"
            type="password"
            required
            minLength={8}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Минимум 8 символов"
          />
        </div>
        <div>
          <Label htmlFor="passwordConfirm">Повторите пароль</Label>
          <Input
            id="passwordConfirm"
            type="password"
            required
            value={passwordConfirm}
            onChange={(e) => setPasswordConfirm(e.target.value)}
          />
        </div>
        <label className="flex items-start gap-2 text-sm text-brand-900/70">
          <input
            type="checkbox"
            className="mt-0.5"
            checked={acceptedRules}
            onChange={(e) => setAcceptedRules(e.target.checked)}
          />
          <span>Я принимаю правила сайта.</span>
        </label>
        <FieldError>{error}</FieldError>
        <Button type="submit" className="w-full" loading={loading}>
          Создать аккаунт
        </Button>
      </form>
    </AuthShell>
  );
}

function InviteInvalid({ used }: { used?: boolean }) {
  return (
    <AuthShell>
      <h1 className="text-2xl font-semibold text-brand-950">Приглашение недействительно</h1>
      <p className="mt-2 text-sm text-brand-900/60">
        {used
          ? 'Это приглашение уже было использовано. Если у вас есть аккаунт, войдите в систему.'
          : 'Ссылка недействительна или была отозвана. Обратитесь к администратору за новым приглашением.'}
      </p>
      <Link to="/login" className="mt-6 inline-block">
        <Button variant="secondary">Перейти ко входу</Button>
      </Link>
    </AuthShell>
  );
}

function InviteExpired() {
  return (
    <AuthShell>
      <h1 className="text-2xl font-semibold text-brand-950">Срок приглашения истёк</h1>
      <p className="mt-2 text-sm text-brand-900/60">
        Ссылка действовала ограниченное время. Попросите администратора отправить новое приглашение.
      </p>
      <Link to="/" className="mt-6 inline-block">
        <Button variant="secondary">На главную</Button>
      </Link>
    </AuthShell>
  );
}
