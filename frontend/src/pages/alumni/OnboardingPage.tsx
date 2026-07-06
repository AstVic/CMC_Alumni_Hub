import { Link } from 'react-router-dom';
import { Navbar } from '../../components/layout/Navbar';
import { Button } from '../../components/ui/Button';

export function OnboardingPage() {
  return (
    <div className="flex min-h-full flex-col">
      <Navbar />
      <main className="mx-auto flex w-full max-w-2xl flex-1 flex-col justify-center px-4 py-16 text-center">
        <span className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-brand-600 text-2xl font-bold text-white">
          🎓
        </span>
        <h1 className="text-3xl font-bold text-brand-950">Добро пожаловать в CMC Alumni Hub</h1>
        <p className="mx-auto mt-4 max-w-lg text-brand-900/70">
          Заполните свою карточку, чтобы студенты могли узнать о вашем карьерном пути и задать
          вам вопросы. После заполнения карточка отправится на модерацию администратору.
        </p>
        <div className="mt-8">
          <Link to="/alumni/profile">
            <Button size="lg">Заполнить карточку</Button>
          </Link>
        </div>
        <Link to="/alumni" className="mt-4 text-sm text-brand-600 hover:text-brand-700">
          Позже, перейти в кабинет
        </Link>
      </main>
    </div>
  );
}
