import { useState, type FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../../api/adminApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { Modal } from '../../components/ui/Modal';
import { Input, Textarea, Label, FieldError } from '../../components/ui/Field';
import { InviteStatusBadge } from '../../components/ui/Badge';
import { LoadingState, EmptyState } from '../../components/ui/States';
import { formatDate } from '../../utils/format';

export function AdminInvitesPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const [modalOpen, setModalOpen] = useState(false);

  const { data, isLoading } = useQuery({ queryKey: ['admin', 'invites'], queryFn: adminApi.listInvites });

  const invalidate = () => qc.invalidateQueries({ queryKey: ['admin', 'invites'] });

  const resend = useMutation({
    mutationFn: (id: number) => adminApi.resendInvite(id),
    onSuccess: () => {
      notify('Приглашение отправлено повторно', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });
  const revoke = useMutation({
    mutationFn: (id: number) => adminApi.revokeInvite(id),
    onSuccess: () => {
      notify('Приглашение отозвано', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-brand-950">Приглашения выпускников</h1>
        <Button onClick={() => setModalOpen(true)}>Пригласить выпускника</Button>
      </div>

      {isLoading ? (
        <LoadingState />
      ) : !data || data.length === 0 ? (
        <EmptyState title="Приглашений пока нет" description="Создайте первое приглашение по email." />
      ) : (
        <div className="overflow-x-auto rounded-2xl bg-white shadow-card">
          <table className="w-full min-w-[720px] text-sm">
            <thead className="border-b border-surface-border text-left text-brand-900/50">
              <tr>
                <th className="px-4 py-3 font-medium">Email</th>
                <th className="px-4 py-3 font-medium">Статус</th>
                <th className="px-4 py-3 font-medium">Создано</th>
                <th className="px-4 py-3 font-medium">Действует до</th>
                <th className="px-4 py-3 font-medium">Использовано</th>
                <th className="px-4 py-3 font-medium">Действия</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-surface-border">
              {data.map((inv) => {
                const canManage = inv.status !== 'USED' && inv.status !== 'REVOKED';
                return (
                  <tr key={inv.id}>
                    <td className="px-4 py-3">
                      <div className="font-medium text-brand-900">{inv.email}</div>
                      {inv.note && <div className="text-xs text-brand-900/40">{inv.note}</div>}
                    </td>
                    <td className="px-4 py-3"><InviteStatusBadge status={inv.status} /></td>
                    <td className="px-4 py-3 text-brand-900/60">{formatDate(inv.createdAt)}</td>
                    <td className="px-4 py-3 text-brand-900/60">{formatDate(inv.expiresAt)}</td>
                    <td className="px-4 py-3 text-brand-900/60">{formatDate(inv.usedAt)}</td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <Button
                          size="sm"
                          variant="secondary"
                          disabled={!canManage || resend.isPending}
                          onClick={() => resend.mutate(inv.id)}
                        >
                          Отправить снова
                        </Button>
                        <Button
                          size="sm"
                          variant="danger"
                          disabled={!canManage || revoke.isPending}
                          onClick={() => revoke.mutate(inv.id)}
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

      <CreateInviteModal open={modalOpen} onClose={() => setModalOpen(false)} onCreated={invalidate} />
    </div>
  );
}

function CreateInviteModal({
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
      await adminApi.createInvite(email.trim(), note.trim() || undefined);
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
    <Modal open={open} title="Новое приглашение" onClose={onClose}>
      <form onSubmit={submit} className="space-y-4">
        <div>
          <Label htmlFor="inv-email">Email выпускника</Label>
          <Input
            id="inv-email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="alumni@example.com"
          />
        </div>
        <div>
          <Label htmlFor="inv-note">Заметка (необязательно)</Label>
          <Textarea id="inv-note" value={note} onChange={(e) => setNote(e.target.value)} />
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
