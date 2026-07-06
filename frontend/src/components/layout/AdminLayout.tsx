import { DashboardLayout, type NavItem } from './DashboardLayout';

const ITEMS: NavItem[] = [
  { to: '/admin', label: 'Дашборд', end: true },
  { to: '/admin/invites', label: 'Приглашения' },
  { to: '/admin/alumni', label: 'Выпускники' },
  { to: '/admin/profiles', label: 'Модерация карточек' },
  { to: '/admin/questions', label: 'Вопросы' },
  { to: '/admin/tags', label: 'Теги' },
];

export function AdminLayout() {
  return <DashboardLayout title="Админ-панель" items={ITEMS} />;
}
