import { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { alumniApi } from '../../api/alumniApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { EmptyState, LoadingState } from '../../components/ui/States';
import { cn } from '../../utils/cn';
import { formatDate } from '../../utils/format';

type Filter = 'new' | 'read' | 'archived';

const TABS: { key: Filter; label: string }[] = [
  { key: 'new', label: 'Новые' },
  { key: 'read', label: 'Просмотренные' },
  { key: 'archived', label: 'Архив' },
];

export function AlumniQuestionsPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const [filter, setFilter] = useState<Filter>('new');

  const { data, isLoading } = useQuery({
    queryKey: ['alumni', 'questions', filter],
    queryFn: () => alumniApi.listQuestions(filter),
  });

  const markRead = async (id: number) => {
    try {
      await alumniApi.markRead(id);
      await qc.invalidateQueries({ queryKey: ['alumni', 'questions'] });
      notify('Отмечено как просмотренное', 'success');
    } catch (err) {
      notify(apiErrorMessage(err), 'error');
    }
  };

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-brand-950">Вопросы</h1>

      <div className="flex gap-2">
        {TABS.map((t) => (
          <button
            key={t.key}
            onClick={() => setFilter(t.key)}
            className={cn(
              'rounded-lg px-3 py-1.5 text-sm font-medium transition-colors',
              filter === t.key ? 'bg-brand-600 text-white' : 'bg-white text-brand-900/70',
            )}
          >
            {t.label}
          </button>
        ))}
      </div>

      {isLoading ? (
        <LoadingState />
      ) : !data || data.length === 0 ? (
        <EmptyState
          title="Пока нет вопросов"
          description={
            filter === 'new'
              ? 'Новые вопросы появятся здесь после модерации администратором.'
              : 'В этой категории вопросов нет.'
          }
        />
      ) : (
        <div className="space-y-3">
          {data.map((q) => (
            <div key={q.id} className="rounded-2xl bg-white p-5 shadow-card">
              <p className="text-brand-900">{q.questionText}</p>
              <div className="mt-3 flex flex-wrap items-center justify-between gap-2 text-sm text-brand-900/50">
                <span>
                  {q.senderName || 'Аноним'}
                  {q.senderEmail ? ` · ${q.senderEmail}` : ''} · {formatDate(q.createdAt)}
                </span>
                {!q.read && (
                  <Button size="sm" variant="secondary" onClick={() => markRead(q.id)}>
                    Отметить просмотренным
                  </Button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
