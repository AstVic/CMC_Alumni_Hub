import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { publicApi } from '../../api/publicApi';
import { Button } from '../../components/ui/Button';
import { TagBadge } from '../../components/ui/Badge';
import { LoadingState } from '../../components/ui/States';
import { questionLabel } from '../../utils/format';
import type { ProfileCard } from '../../types';

const BENEFITS = [
  {
    title: 'Найти наставника',
    text: 'Свяжитесь с выпускниками, которые уже прошли ваш путь и готовы поделиться опытом.',
  },
  {
    title: 'Узнать о карьере',
    text: 'Реальные истории карьерного пути: от стажировки до ведущих позиций в индустрии.',
  },
  {
    title: 'Задать вопрос',
    text: 'Задайте вопрос конкретному выпускнику прямо на сайте — без регистрации.',
  },
];

export function LandingPage() {
  const { data: recent } = useQuery({
    queryKey: ['landing', 'recent'],
    queryFn: () => publicApi.listProfiles({ sort: 'newest', size: 3 }),
  });
  const { data: popular } = useQuery({
    queryKey: ['landing', 'popular'],
    queryFn: () => publicApi.listProfiles({ sort: 'questions_desc', size: 3 }),
  });
  const { data: tags } = useQuery({ queryKey: ['tags'], queryFn: publicApi.listTags });

  return (
    <div className="space-y-16">
      {/* Hero */}
      <section className="overflow-hidden rounded-3xl bg-gradient-to-br from-brand-700 via-brand-800 to-brand-950 px-6 py-16 text-white sm:px-12">
        <div className="max-w-2xl">
          <p className="mb-3 inline-block rounded-full bg-white/10 px-3 py-1 text-sm font-medium">
            Факультет ВМК МГУ
          </p>
          <h1 className="text-4xl font-bold leading-tight sm:text-5xl">
            Связь студентов с выпускниками факультета
          </h1>
          <p className="mt-4 text-lg text-white/80">
            Узнавайте о карьерных путях выпускников ВМК, находите наставников и задавайте
            вопросы тем, кто уже работает в индустрии и науке.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link to="/catalog">
              <Button size="lg" variant="secondary">
                Посмотреть выпускников
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Benefits */}
      <section className="grid gap-5 sm:grid-cols-3">
        {BENEFITS.map((b) => (
          <div key={b.title} className="rounded-2xl bg-white p-6 shadow-card">
            <h3 className="text-lg font-semibold text-brand-900">{b.title}</h3>
            <p className="mt-2 text-sm text-brand-900/60">{b.text}</p>
          </div>
        ))}
      </section>

      {/* Popular tags */}
      {tags && tags.length > 0 && (
        <section>
          <h2 className="mb-4 text-xl font-semibold text-brand-950">Популярные направления</h2>
          <div className="flex flex-wrap gap-2">
            {tags.slice(0, 14).map((t) => (
              <Link key={t.id} to={`/catalog?tags=${t.slug}`}>
                <TagBadge name={t.name} />
              </Link>
            ))}
          </div>
        </section>
      )}

      {/* Popular profiles */}
      <ProfileStrip title="Самые обсуждаемые" profiles={popular?.content} showQuestions />
      {/* Recent profiles */}
      <ProfileStrip title="Недавно опубликованные" profiles={recent?.content} />
    </div>
  );
}

function ProfileStrip({
  title,
  profiles,
  showQuestions,
}: {
  title: string;
  profiles?: ProfileCard[];
  showQuestions?: boolean;
}) {
  return (
    <section>
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-xl font-semibold text-brand-950">{title}</h2>
        <Link to="/catalog" className="text-sm font-medium text-brand-600 hover:text-brand-700">
          Смотреть все →
        </Link>
      </div>
      {!profiles ? (
        <LoadingState />
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {profiles.map((p) => (
            <Link
              key={p.id}
              to={`/alumni-profile/${p.id}`}
              className="rounded-2xl bg-white p-5 shadow-card transition-transform hover:-translate-y-0.5"
            >
              <div className="flex items-center gap-3">
                <Avatar name={p.fullName} url={p.photoUrl} />
                <div>
                  <p className="font-semibold text-brand-950">{p.fullName}</p>
                  <p className="text-sm text-brand-900/60">
                    {p.currentPosition}
                    {p.company ? ` · ${p.company}` : ''}
                  </p>
                </div>
              </div>
              {showQuestions && (
                <p className="mt-3 text-sm font-medium text-accent-600">
                  {questionLabel(p.questionCount)}
                </p>
              )}
            </Link>
          ))}
        </div>
      )}
    </section>
  );
}

function Avatar({ name, url }: { name: string; url: string | null }) {
  if (url) {
    return <img src={url} alt={name} className="h-12 w-12 rounded-full object-cover" />;
  }
  const initials = name
    .split(' ')
    .slice(0, 2)
    .map((s) => s[0])
    .join('');
  return (
    <span className="flex h-12 w-12 items-center justify-center rounded-full bg-brand-100 font-semibold text-brand-700">
      {initials}
    </span>
  );
}
