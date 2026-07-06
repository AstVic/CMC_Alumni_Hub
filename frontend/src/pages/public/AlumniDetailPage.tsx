import { Link, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { publicApi } from '../../api/publicApi';
import { Avatar } from '../../components/catalog/Avatar';
import { TagBadge } from '../../components/ui/Badge';
import { QuestionForm } from '../../components/question/QuestionForm';
import { LoadingState, ErrorState } from '../../components/ui/States';
import { questionLabel } from '../../utils/format';

export function AlumniDetailPage() {
  const { id } = useParams();
  const profileId = Number(id);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['profile', profileId],
    queryFn: () => publicApi.getProfile(profileId),
    enabled: Number.isFinite(profileId),
  });

  if (isLoading) return <LoadingState />;
  if (isError || !data) return <ErrorState message="Карточка не найдена или недоступна" />;

  const location = [data.city, data.country].filter(Boolean).join(', ');

  return (
    <div className="space-y-8">
      <Link to="/catalog" className="text-sm font-medium text-brand-600 hover:text-brand-700">
        ← К каталогу
      </Link>

      <div className="grid gap-8 lg:grid-cols-[1fr_360px]">
        {/* Main */}
        <div className="space-y-6">
          <div className="flex flex-col gap-5 rounded-2xl bg-white p-6 shadow-card sm:flex-row sm:items-center">
            <Avatar name={data.fullName} url={data.photoUrl} size="lg" />
            <div>
              <h1 className="text-2xl font-bold text-brand-950">{data.fullName}</h1>
              <p className="mt-1 text-brand-900/70">
                {data.currentPosition}
                {data.company ? ` · ${data.company}` : ''}
              </p>
              <p className="mt-1 text-sm text-brand-900/50">
                {data.graduationYear ? `Выпуск ${data.graduationYear}` : ''}
                {data.department ? ` · ${data.department}` : ''}
                {location ? ` · ${location}` : ''}
              </p>
              <p className="mt-2 text-sm font-medium text-accent-600">
                {questionLabel(data.questionCount)}
              </p>
            </div>
          </div>

          {data.tags.length > 0 && (
            <div className="flex flex-wrap gap-2">
              {data.tags.map((t) => (
                <TagBadge key={t.id} name={t.name} />
              ))}
            </div>
          )}

          {data.careerDescription && (
            <Section title="Карьерный путь">{data.careerDescription}</Section>
          )}
          {data.interestsDescription && (
            <Section title="Профессиональные интересы">{data.interestsDescription}</Section>
          )}
        </div>

        {/* Question form */}
        <aside className="lg:sticky lg:top-20 lg:self-start">
          <div className="rounded-2xl bg-white p-6 shadow-card">
            <h2 className="text-lg font-semibold text-brand-950">Задать вопрос</h2>
            <p className="mb-4 mt-1 text-sm text-brand-900/60">
              Ваш вопрос увидит выпускник после модерации.
            </p>
            <QuestionForm profileId={profileId} />
          </div>
        </aside>
      </div>
    </div>
  );
}

function Section({ title, children }: { title: string; children: string }) {
  return (
    <div className="rounded-2xl bg-white p-6 shadow-card">
      <h2 className="mb-2 text-lg font-semibold text-brand-950">{title}</h2>
      <p className="whitespace-pre-line text-brand-900/70">{children}</p>
    </div>
  );
}
