import { Link } from 'react-router-dom';
import { Button } from '../components/ui/Button';

export function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center py-24 text-center">
      <p className="text-6xl font-bold text-brand-200">404</p>
      <h1 className="mt-4 text-2xl font-semibold text-brand-950">Страница не найдена</h1>
      <p className="mt-2 text-brand-900/60">Возможно, ссылка устарела или введена неверно.</p>
      <Link to="/" className="mt-6">
        <Button>На главную</Button>
      </Link>
    </div>
  );
}
