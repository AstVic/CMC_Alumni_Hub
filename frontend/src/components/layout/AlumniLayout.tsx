import { DashboardLayout, type NavItem } from './DashboardLayout';

const ITEMS: NavItem[] = [
  { to: '/alumni', label: 'Обзор', end: true },
  { to: '/alumni/profile', label: 'Моя карточка' },
  { to: '/alumni/questions', label: 'Вопросы' },
];

export function AlumniLayout() {
  return <DashboardLayout title="Личный кабинет" items={ITEMS} />;
}
