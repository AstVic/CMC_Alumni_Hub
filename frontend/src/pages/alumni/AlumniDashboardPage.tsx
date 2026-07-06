import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { alumniApi } from '../../api/alumniApi';
import { ProfileStatusBadge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { LoadingState, ErrorState } from '../../components/ui/States';
import { questionLabel } from '../../utils/format';

export function AlumniDashboardPage() {
  const { data: profile, isLoading, isError } = useQuery({
    queryKey: ['alumni', 'profile'],
    queryFn: alumniApi.getProfile,
  });
  const { data: newQuestions } = useQuery({
    queryKey: ['alumni', 'questions', 'new'],
    queryFn: () => alumniApi.listQuestions('new'),
  });

  if (isLoading) return <LoadingState />;
  if (isError || !profile) return <ErrorState message="Не удалось загрузить профиль" />;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-brand-950">Обзор</h1>

      {profile.status === 'REJECTED' && profile.moderationComment && (
        <div className="rounded-2xl border border-red-100 bg-red-50 p-4">
          <p className="font-semibold text-red-800">Карточка отклонена</p>
          <p className="mt-1 text-sm text-red-700">{profile.moderationComment}</p>
          <Link to="/alumni/profile" className="mt-2 inline-block text-sm font-medium text-red-800 underline">
            Исправить карточку
          </Link>
        </div>
      )}

      <div className="grid gap-4 sm:grid-cols-3">
        <StatCard label="Статус карточки">
          <ProfileStatusBadge status={profile.status} />
        </StatCard>
        <StatCard label="Всего вопросов">
          <span className="text-2xl font-bold text-brand-950">{profile.questionCount}</span>
        </StatCard>
        <StatCard label="Новых вопросов">
          <span className="text-2xl font-bold text-accent-600">{newQuestions?.length ?? 0}</span>
        </StatCard>
      </div>

      <div className="rounded-2xl bg-white p-6 shadow-card">
        <h2 className="text-lg font-semibold text-brand-950">Ваша карточка</h2>
        <p className="mt-1 text-sm text-brand-900/60">
          {profile.status === 'PUBLISHED'
            ? 'Карточка опубликована и видна студентам в каталоге.'
            : profile.status === 'PENDING_MODERATION'
              ? 'Карточка на модерации у администратора.'
              : 'Заполните и отправьте карточку на модерацию, чтобы она появилась в каталоге.'}
        </p>
        <div className="mt-4 flex gap-2">
          <Link to="/alumni/profile">
            <Button variant="secondary">Редактировать карточку</Button>
          </Link>
          <Link to="/alumni/questions">
            <Button variant="ghost">
              Вопросы ({questionLabel(newQuestions?.length ?? 0)})
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}

function StatCard({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="rounded-2xl bg-white p-5 shadow-card">
      <p className="text-sm text-brand-900/50">{label}</p>
      <div className="mt-2">{children}</div>
    </div>
  );
}
