import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { adminApi } from '../../api/adminApi';
import { LoadingState, ErrorState } from '../../components/ui/States';

export function AdminDashboardPage() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['admin', 'dashboard'],
    queryFn: adminApi.dashboard,
  });

  if (isLoading) return <LoadingState />;
  if (isError || !data) return <ErrorState message="Не удалось загрузить статистику" />;

  const cards = [
    { label: 'Выпускников', value: data.totalAlumni, to: '/admin/alumni' },
    { label: 'Опубликовано карточек', value: data.publishedProfiles, to: '/admin/profiles' },
    { label: 'Карточек на модерации', value: data.profilesOnModeration, to: '/admin/profiles', accent: true },
    { label: 'Всего вопросов', value: data.totalQuestions, to: '/admin/questions' },
    { label: 'Вопросов на модерации', value: data.questionsOnModeration, to: '/admin/questions', accent: true },
    { label: 'Отклонённых вопросов', value: data.rejectedQuestions, to: '/admin/questions' },
    { label: 'Использовано приглашений', value: data.usedInvites, to: '/admin/invites' },
    { label: 'Активных приглашений', value: data.pendingInvites, to: '/admin/invites' },
  ];

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-brand-950">Дашборд</h1>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {cards.map((c) => (
          <Link
            key={c.label}
            to={c.to}
            className="rounded-2xl bg-white p-5 shadow-card transition-transform hover:-translate-y-0.5"
          >
            <p className="text-sm text-brand-900/50">{c.label}</p>
            <p
              className={
                'mt-2 text-3xl font-bold ' + (c.accent ? 'text-accent-600' : 'text-brand-950')
              }
            >
              {c.value}
            </p>
          </Link>
        ))}
      </div>
    </div>
  );
}
