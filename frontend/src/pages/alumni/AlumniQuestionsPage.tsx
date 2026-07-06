import { useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { alumniApi } from '../../api/alumniApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { Textarea } from '../../components/ui/Field';
import { EmptyState, LoadingState } from '../../components/ui/States';
import { cn } from '../../utils/cn';
import { formatDate } from '../../utils/format';
import type { AlumniQuestion } from '../../types';

type Filter = 'new' | 'read' | 'archived';

const TABS: { key: Filter; label: string }[] = [
  { key: 'new', label: 'Новые' },
  { key: 'read', label: 'Просмотренные' },
  { key: 'archived', label: 'Архив' },
];

export function AlumniQuestionsPage() {
  const [filter, setFilter] = useState<Filter>('new');

  const { data, isLoading } = useQuery({
    queryKey: ['alumni', 'questions', filter],
    queryFn: () => alumniApi.listQuestions(filter),
  });

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
            <QuestionItem key={q.id} question={q} />
          ))}
        </div>
      )}
    </div>
  );
}

function QuestionItem({ question }: { question: AlumniQuestion }) {
  const qc = useQueryClient();
  const { notify } = useToast();
  const [answering, setAnswering] = useState(false);
  const [answer, setAnswer] = useState('');
  const [busy, setBusy] = useState(false);

  const refresh = () => qc.invalidateQueries({ queryKey: ['alumni', 'questions'] });

  const markRead = async () => {
    try {
      await alumniApi.markRead(question.id);
      await refresh();
      notify('Отмечено как просмотренное', 'success');
    } catch (err) {
      notify(apiErrorMessage(err), 'error');
    }
  };

  const submitAnswer = async () => {
    if (!answer.trim()) return;
    setBusy(true);
    try {
      await alumniApi.answerQuestion(question.id, answer.trim());
      await refresh();
      notify('Ответ опубликован', 'success');
      setAnswering(false);
      setAnswer('');
    } catch (err) {
      notify(apiErrorMessage(err), 'error');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="rounded-2xl bg-white p-5 shadow-card">
      <p className="text-brand-900">{question.questionText}</p>
      <div className="mt-2 text-sm text-brand-900/50">
        {question.senderName || 'Аноним'}
        {question.senderEmail ? ` · ${question.senderEmail}` : ''} · {formatDate(question.createdAt)}
      </div>

      {/* Existing answer */}
      {question.answerText && (
        <div className="mt-3 rounded-xl bg-brand-50 p-3">
          <p className="text-xs font-medium text-brand-700">Ваш ответ:</p>
          <p className="mt-1 whitespace-pre-line text-sm text-brand-900">{question.answerText}</p>
        </div>
      )}

      {/* Actions */}
      <div className="mt-3 flex flex-wrap items-center gap-2">
        {!question.read && (
          <Button size="sm" variant="secondary" onClick={markRead}>
            Отметить просмотренным
          </Button>
        )}
        {!question.answerText && !answering && (
          <Button size="sm" onClick={() => setAnswering(true)}>
            Ответить
          </Button>
        )}
        {question.answerText && !answering && (
          <Button
            size="sm"
            variant="ghost"
            onClick={() => {
              setAnswer(question.answerText ?? '');
              setAnswering(true);
            }}
          >
            Изменить ответ
          </Button>
        )}
      </div>

      {/* Answer form */}
      {answering && (
        <div className="mt-3 space-y-2">
          <Textarea
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            placeholder="Ваш ответ будет виден всем на вашей карточке…"
            maxLength={4000}
          />
          <div className="flex gap-2">
            <Button size="sm" loading={busy} onClick={submitAnswer}>
              Опубликовать ответ
            </Button>
            <Button
              size="sm"
              variant="ghost"
              onClick={() => {
                setAnswering(false);
                setAnswer('');
              }}
            >
              Отмена
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
