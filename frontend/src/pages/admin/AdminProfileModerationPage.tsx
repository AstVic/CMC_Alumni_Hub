import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi } from '../../api/adminApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { Modal } from '../../components/ui/Modal';
import { Textarea, Label, FieldError } from '../../components/ui/Field';
import { ProfileStatusBadge, TagBadge } from '../../components/ui/Badge';
import { LoadingState, EmptyState } from '../../components/ui/States';
import type { AlumniProfile } from '../../types';

export function AdminProfileModerationPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const [rejecting, setRejecting] = useState<AlumniProfile | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'profiles', 'moderation'],
    queryFn: adminApi.profilesModeration,
  });

  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ['admin', 'profiles', 'moderation'] });
    qc.invalidateQueries({ queryKey: ['admin', 'dashboard'] });
  };

  const approve = useMutation({
    mutationFn: (id: number) => adminApi.approveProfile(id),
    onSuccess: () => {
      notify('Карточка опубликована', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  if (isLoading) return <LoadingState />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-brand-950">Карточки на модерации</h1>

      {!data || data.length === 0 ? (
        <EmptyState title="Нет карточек на модерации" description="Все карточки обработаны." />
      ) : (
        <div className="space-y-4">
          {data.map((p) => (
            <div key={p.id} className="rounded-2xl bg-white p-6 shadow-card">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="text-lg font-semibold text-brand-950">{p.fullName}</h3>
                    <ProfileStatusBadge status={p.status} />
                  </div>
                  <p className="text-sm text-brand-900/70">
                    {p.currentPosition}
                    {p.company ? ` · ${p.company}` : ''}
                    {p.graduationYear ? ` · Выпуск ${p.graduationYear}` : ''}
                  </p>
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="success"
                    size="sm"
                    disabled={approve.isPending}
                    onClick={() => approve.mutate(p.id)}
                  >
                    Одобрить
                  </Button>
                  <Button variant="danger" size="sm" onClick={() => setRejecting(p)}>
                    Отклонить
                  </Button>
                </div>
              </div>

              {p.careerDescription && (
                <p className="mt-3 whitespace-pre-line text-sm text-brand-900/70">
                  {p.careerDescription}
                </p>
              )}
              {p.tags.length > 0 && (
                <div className="mt-3 flex flex-wrap gap-1.5">
                  {p.tags.map((t) => (
                    <TagBadge key={t.id} name={t.name} />
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      <RejectModal
        profile={rejecting}
        onClose={() => setRejecting(null)}
        onDone={() => {
          invalidate();
          setRejecting(null);
        }}
      />
    </div>
  );
}

function RejectModal({
  profile,
  onClose,
  onDone,
}: {
  profile: AlumniProfile | null;
  onClose: () => void;
  onDone: () => void;
}) {
  const { notify } = useToast();
  const [comment, setComment] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async () => {
    if (!profile) return;
    if (!comment.trim()) {
      setError('Укажите причину отклонения');
      return;
    }
    setLoading(true);
    try {
      await adminApi.rejectProfile(profile.id, comment.trim());
      notify('Карточка отклонена', 'success');
      setComment('');
      onDone();
    } catch (err) {
      setError(apiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      open={profile !== null}
      title="Отклонить карточку"
      onClose={onClose}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button variant="danger" loading={loading} onClick={submit}>
            Отклонить
          </Button>
        </>
      }
    >
      <Label htmlFor="reject-comment">Комментарий для выпускника</Label>
      <Textarea
        id="reject-comment"
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        placeholder="Например: добавьте описание карьерного пути."
      />
      <FieldError>{error}</FieldError>
    </Modal>
  );
}
