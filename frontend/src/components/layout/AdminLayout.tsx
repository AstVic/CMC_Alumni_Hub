import { DashboardLayout, type NavItem } from './DashboardLayout';
import { useAuth } from '../../auth/useAuth';

const BASE_ITEMS: NavItem[] = [
  { to: '/admin', label: 'Дашборд', end: true },
  { to: '/admin/invites', label: 'Приглашения' },
  { to: '/admin/alumni', label: 'Выпускники' },
  { to: '/admin/profiles', label: 'Модерация карточек' },
  { to: '/admin/questions', label: 'Вопросы' },
  { to: '/admin/tags', label: 'Теги' },
];

export function AdminLayout() {
  const { user } = useAuth();
  // The admin-management section is owner-only.
  const items = user?.owner
    ? [...BASE_ITEMS, { to: '/admin/admins', label: 'Администраторы' }]
    : BASE_ITEMS;
  return <DashboardLayout title="Админ-панель" items={items} />;
}
