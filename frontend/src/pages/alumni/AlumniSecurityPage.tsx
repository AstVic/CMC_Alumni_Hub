import { ChangePasswordForm } from '../../components/account/ChangePasswordForm';

export function AlumniSecurityPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-brand-950">Безопасность</h1>
        <p className="mt-1 text-sm text-brand-900/60">Изменение пароля вашей учётной записи.</p>
      </div>
      <ChangePasswordForm />
    </div>
  );
}
