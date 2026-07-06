import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../../api/adminApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { Badge, ProfileStatusBadge } from '../../components/ui/Badge';
import { LoadingState, EmptyState } from '../../components/ui/States';

export function AdminAlumniPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const { data, isLoading } = useQuery({ queryKey: ['admin', 'alumni'], queryFn: adminApi.listAlumni });

  const block = useMutation({
    mutationFn: ({ id, blocked }: { id: number; blocked: boolean }) =>
      adminApi.setBlocked(id, blocked),
    onSuccess: (_, v) => {
      notify(v.blocked ? 'Аккаунт заблокирован' : 'Аккаунт разблокирован', 'success');
      qc.invalidateQueries({ queryKey: ['admin', 'alumni'] });
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  if (isLoading) return <LoadingState />;
  if (!data || data.length === 0) return <EmptyState title="Выпускников пока нет" />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-brand-950">Выпускники</h1>
      <div className="overflow-x-auto rounded-2xl bg-white shadow-card">
        <table className="w-full min-w-[720px] text-sm">
          <thead className="border-b border-surface-border text-left text-brand-900/50">
            <tr>
              <th className="px-4 py-3 font-medium">Выпускник</th>
              <th className="px-4 py-3 font-medium">Email</th>
              <th className="px-4 py-3 font-medium">Карточка</th>
              <th className="px-4 py-3 font-medium">Вопросов</th>
              <th className="px-4 py-3 font-medium">Аккаунт</th>
              <th className="px-4 py-3 font-medium">Действия</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-surface-border">
            {data.map((a) => (
              <tr key={a.userId}>
                <td className="px-4 py-3">
                  <div className="font-medium text-brand-900">{a.fullName ?? '—'}</div>
                  <div className="text-xs text-brand-900/40">
                    {a.company ?? ''}
                    {a.graduationYear ? ` · ${a.graduationYear}` : ''}
                  </div>
                </td>
                <td className="px-4 py-3 text-brand-900/60">{a.email}</td>
                <td className="px-4 py-3">
                  {a.profileStatus ? <ProfileStatusBadge status={a.profileStatus} /> : '—'}
                </td>
                <td className="px-4 py-3 text-brand-900/60">{a.questionCount}</td>
                <td className="px-4 py-3">
                  {a.enabled ? <Badge tone="green">Активен</Badge> : <Badge tone="red">Заблокирован</Badge>}
                </td>
                <td className="px-4 py-3">
                  <Button
                    size="sm"
                    variant={a.enabled ? 'danger' : 'success'}
                    disabled={block.isPending}
                    onClick={() => block.mutate({ id: a.userId, blocked: a.enabled })}
                  >
                    {a.enabled ? 'Заблокировать' : 'Разблокировать'}
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
