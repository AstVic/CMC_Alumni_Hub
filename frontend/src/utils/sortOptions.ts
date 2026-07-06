export interface SortOption {
  value: string;
  label: string;
}

export const SORT_OPTIONS: SortOption[] = [
  { value: 'newest', label: 'Сначала новые' },
  { value: 'oldest', label: 'Сначала старые' },
  { value: 'name_asc', label: 'По алфавиту' },
  { value: 'graduation_year_desc', label: 'Год выпуска: новые' },
  { value: 'graduation_year_asc', label: 'Год выпуска: старшие' },
  { value: 'questions_desc', label: 'Больше вопросов' },
  { value: 'questions_asc', label: 'Меньше вопросов' },
];

export const DEFAULT_SORT = 'newest';
