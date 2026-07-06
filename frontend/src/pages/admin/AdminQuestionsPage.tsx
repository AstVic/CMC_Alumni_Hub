import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../../api/adminApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { QuestionStatusBadge } from '../../components/ui/Badge';
import { LoadingState, EmptyState } from '../../components/ui/States';
import { cn } from '../../utils/cn';
import { formatDate } from '../../utils/format';
import type { AdminQuestion } from '../../types';

type Tab = 'moderation' | 'rejected' | 'all';

const TABS: { key: Tab; label: string }[] = [
  { key: 'moderation', label: 'На модерации' },
  { key: 'rejected', label: 'Отклонённые' },
  { key: 'all', label: 'Все' },
];

export function AdminQuestionsPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const [tab, setTab] = useState<Tab>('moderation');

  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'questions', tab],
    queryFn: () =>
      tab === 'moderation'
        ? adminApi.questionsModeration()
        : tab === 'rejected'
          ? adminApi.questionsRejected()
          : adminApi.listQuestions(),
  });

  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ['admin', 'questions'] });
    qc.invalidateQueries({ queryKey: ['admin', 'dashboard'] });
  };

  const approve = useMutation({
    mutationFn: (id: number) => adminApi.approveQuestion(id),
    onSuccess: () => {
      notify('Вопрос одобрен и виден выпускнику', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });
  const reject = useMutation({
    mutationFn: (id: number) => adminApi.rejectQuestion(id),
    onSuccess: () => {
      notify('Вопрос отклонён', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-brand-950">Вопросы</h1>

      <div className="flex gap-2">
        {TABS.map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={cn(
              'rounded-lg px-3 py-1.5 text-sm font-medium transition-colors',
              tab === t.key ? 'bg-brand-600 text-white' : 'bg-white text-brand-900/70',
            )}
          >
            {t.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <LoadingState />
      ) : !data || data.length === 0 ? (
        <EmptyState title="Вопросов нет" description="В этой категории пусто." />
      ) : (
        <div className="space-y-3">
          {data.map((q) => (
            <QuestionRow
              key={q.id}
              q={q}
              onApprove={() => approve.mutate(q.id)}
              onReject={() => reject.mutate(q.id)}
              busy={approve.isPending || reject.isPending}
            />
          ))}
        </div>
      )}
    </div>
  );
}

function QuestionRow({
  q,
  onApprove,
  onReject,
  busy,
}: {
  q: AdminQuestion;
  onApprove: () => void;
  onReject: () => void;
  busy: boolean;
}) {
  const canApprove = q.status !== 'VISIBLE_TO_ALUMNI';
  const canReject = q.status !== 'REJECTED_BY_ADMIN';
  return (
    <div className="rounded-2xl bg-white p-5 shadow-card">
      <div className="mb-2 flex flex-wrap items-center gap-2">
        <QuestionStatusBadge status={q.status} />
        <span className="text-sm text-brand-900/50">
          Кому: <span className="font-medium text-brand-900/70">{q.alumniName ?? '—'}</span>
        </span>
      </div>
      <p className="text-brand-900">{q.questionText}</p>
      {q.aiModerationReason && (
        <p className="mt-2 text-xs text-brand-900/40">AI: {q.aiModerationReason}</p>
      )}
      <div className="mt-3 flex flex-wrap items-center justify-between gap-2 text-sm text-brand-900/50">
        <span>
          {q.senderName || 'Аноним'}
          {q.senderEmail ? ` · ${q.senderEmail}` : ''} · {formatDate(q.createdAt)}
        </span>
        <div className="flex gap-2">
          {canApprove && (
            <Button size="sm" variant="success" disabled={busy} onClick={onApprove}>
              Одобрить
            </Button>
          )}
          {canReject && (
            <Button size="sm" variant="danger" disabled={busy} onClick={onReject}>
              Отклонить
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
