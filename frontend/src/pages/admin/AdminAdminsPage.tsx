import { useState, type FormEvent } from 'react';
import { Navigate } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../../api/adminApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useAuth } from '../../auth/useAuth';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { Modal } from '../../components/ui/Modal';
import { Input, Label, FieldError, Textarea } from '../../components/ui/Field';
import { Badge, InviteStatusBadge } from '../../components/ui/Badge';
import { EmptyState, LoadingState } from '../../components/ui/States';
import { formatDate } from '../../utils/format';
import type { AdminAccount } from '../../types';

export function AdminAdminsPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const { user, logout } = useAuth();
  const [createOpen, setCreateOpen] = useState(false);
  const [transferTarget, setTransferTarget] = useState<AdminAccount | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'admins'],
    queryFn: adminApi.listAdmins,
    enabled: !!user?.owner,
  });
  const { data: invites, isLoading: invitesLoading } = useQuery({
    queryKey: ['admin', 'admins', 'invites'],
    queryFn: adminApi.listAdminInvites,
    enabled: !!user?.owner,
  });
  const invalidate = () => qc.invalidateQueries({ queryKey: ['admin', 'admins'] });

  const block = useMutation({
    mutationFn: ({ id, blocked }: { id: number; blocked: boolean }) =>
      adminApi.setAdminBlocked(id, blocked),
    onSuccess: (_, v) => {
      notify(v.blocked ? 'Администратор заблокирован' : 'Администратор разблокирован', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  const transfer = useMutation({
    mutationFn: (id: number) => adminApi.transferOwnership(id),
    onSuccess: () => {
      notify('Права главного администратора переданы', 'success');
      setTransferTarget(null);
      // The current user is no longer the owner — refresh session so the UI updates.
      qc.clear();
      logout();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  const resendInvite = useMutation({
    mutationFn: (id: number) => adminApi.resendAdminInvite(id),
    onSuccess: () => {
      notify('Приглашение отправлено повторно', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  const revokeInvite = useMutation({
    mutationFn: (id: number) => adminApi.revokeAdminInvite(id),
    onSuccess: () => {
      notify('Приглашение отозвано', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  if (user && !user.owner) return <Navigate to="/admin" replace />;
  if (isLoading || invitesLoading) return <LoadingState />;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-brand-950">Администраторы</h1>
          <p className="mt-1 text-sm text-brand-900/60">
            Вы — главный администратор. Можете добавлять админов и передавать эту роль.
          </p>
        </div>
        <Button onClick={() => setCreateOpen(true)}>Пригласить администратора</Button>
      </div>

      <div className="overflow-x-auto rounded-2xl bg-white shadow-card">
        <table className="w-full min-w-[640px] text-sm">
          <thead className="border-b border-surface-border text-left text-brand-900/50">
            <tr>
              <th className="px-4 py-3 font-medium">Email</th>
              <th className="px-4 py-3 font-medium">Роль</th>
              <th className="px-4 py-3 font-medium">Статус</th>
              <th className="px-4 py-3 font-medium">Создан</th>
              <th className="px-4 py-3 font-medium">Действия</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-surface-border">
            {data?.map((a) => {
              const isSelf = a.id === user?.id;
              return (
                <tr key={a.id}>
                  <td className="px-4 py-3 font-medium text-brand-900">
                    {a.email}
                    {isSelf && <span className="ml-2 text-xs text-brand-900/40">(вы)</span>}
                  </td>
                  <td className="px-4 py-3">
                    {a.owner ? <Badge tone="violet">Главный админ</Badge> : <Badge tone="blue">Админ</Badge>}
                  </td>
                  <td className="px-4 py-3">
                    {a.enabled ? <Badge tone="green">Активен</Badge> : <Badge tone="red">Заблокирован</Badge>}
                  </td>
                  <td className="px-4 py-3 text-brand-900/60">{formatDate(a.createdAt)}</td>
                  <td className="px-4 py-3">
                    {a.owner ? (
                      <span className="text-xs text-brand-900/40">—</span>
                    ) : (
                      <div className="flex gap-2">
                        <Button
                          size="sm"
                          variant="secondary"
                          onClick={() => setTransferTarget(a)}
                          disabled={!a.enabled}
                        >
                          Сделать главным
                        </Button>
                        <Button
                          size="sm"
                          variant={a.enabled ? 'danger' : 'success'}
                          disabled={block.isPending}
                          onClick={() => block.mutate({ id: a.id, blocked: a.enabled })}
                        >
                          {a.enabled ? 'Заблокировать' : 'Разблокировать'}
                        </Button>
                      </div>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <div>
        <h2 className="mb-3 text-lg font-semibold text-brand-950">Приглашения</h2>
        {!invites || invites.length === 0 ? (
          <EmptyState
            title="Приглашений пока нет"
            description="Отправьте приглашение будущему администратору по email."
          />
        ) : (
          <div className="overflow-x-auto rounded-2xl bg-white shadow-card">
            <table className="w-full min-w-[680px] text-sm">
              <thead className="border-b border-surface-border text-left text-brand-900/50">
                <tr>
                  <th className="px-4 py-3 font-medium">Email</th>
                  <th className="px-4 py-3 font-medium">Статус</th>
                  <th className="px-4 py-3 font-medium">Создано</th>
                  <th className="px-4 py-3 font-medium">Действует до</th>
                  <th className="px-4 py-3 font-medium">Действия</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-border">
                {invites.map((invite) => {
                  const canManage = invite.status !== 'USED' && invite.status !== 'REVOKED';
                  return (
                    <tr key={invite.id}>
                      <td className="px-4 py-3">
                        <div className="font-medium text-brand-900">{invite.email}</div>
                        {invite.note && <div className="text-xs text-brand-900/40">{invite.note}</div>}
                      </td>
                      <td className="px-4 py-3"><InviteStatusBadge status={invite.status} /></td>
                      <td className="px-4 py-3 text-brand-900/60">{formatDate(invite.createdAt)}</td>
                      <td className="px-4 py-3 text-brand-900/60">{formatDate(invite.expiresAt)}</td>
                      <td className="px-4 py-3">
                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            variant="secondary"
                            disabled={!canManage || resendInvite.isPending}
                            onClick={() => resendInvite.mutate(invite.id)}
                          >
                            Отправить снова
                          </Button>
                          <Button
                            size="sm"
                            variant="danger"
                            disabled={!canManage || revokeInvite.isPending}
                            onClick={() => revokeInvite.mutate(invite.id)}
                          >
                            Отозвать
                          </Button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <InviteAdminModal open={createOpen} onClose={() => setCreateOpen(false)} onCreated={invalidate} />

      <Modal
        open={transferTarget !== null}
        title="Передать роль главного администратора"
        onClose={() => setTransferTarget(null)}
        footer={
          <>
            <Button variant="ghost" onClick={() => setTransferTarget(null)}>
              Отмена
            </Button>
            <Button
              variant="danger"
              loading={transfer.isPending}
              onClick={() => transferTarget && transfer.mutate(transferTarget.id)}
            >
              Передать права
            </Button>
          </>
        }
      >
        <p className="text-sm text-brand-900/70">
          Вы собираетесь передать роль главного администратора пользователю{' '}
          <b>{transferTarget?.email}</b>. После этого вы станете обычным администратором и потеряете
          доступ к управлению админами. Потребуется повторный вход. Продолжить?
        </p>
      </Modal>
    </div>
  );
}

function InviteAdminModal({
  open,
  onClose,
  onCreated,
}: {
  open: boolean;
  onClose: () => void;
  onCreated: () => void;
}) {
  const { notify } = useToast();
  const [email, setEmail] = useState('');
  const [note, setNote] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await adminApi.createAdminInvite(email.trim(), note.trim() || undefined);
      notify('Приглашение создано и отправлено', 'success');
      setEmail('');
      setNote('');
      onCreated();
      onClose();
    } catch (err) {
      setError(apiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal open={open} title="Пригласить администратора" onClose={onClose}>
      <form onSubmit={submit} className="space-y-4">
        <div>
          <Label htmlFor="a-email">Email</Label>
          <Input
            id="a-email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="admin@example.com"
          />
        </div>
        <div>
          <Label htmlFor="a-note">Заметка (необязательно)</Label>
          <Textarea id="a-note" value={note} onChange={(e) => setNote(e.target.value)} />
          <p className="mt-1 text-xs text-brand-900/50">
            Администратор получит ссылку и сам задаст пароль при регистрации.
          </p>
        </div>
        <FieldError>{error}</FieldError>
        <div className="flex justify-end gap-2">
          <Button type="button" variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button type="submit" loading={loading}>
            Создать и отправить
          </Button>
        </div>
      </form>
    </Modal>
  );
}
