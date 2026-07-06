// Shared domain types mirroring the backend DTOs.

export type Role = 'ADMIN' | 'ALUMNI';

export interface User {
  id: number;
  email: string;
  role: Role;
  enabled: boolean;
  owner: boolean;
}

export interface AdminAccount {
  id: number;
  email: string;
  enabled: boolean;
  owner: boolean;
  createdAt: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Tag {
  id: number;
  name: string;
  slug: string;
  category: string | null;
}

export type ProfileStatus = 'DRAFT' | 'PENDING_MODERATION' | 'PUBLISHED' | 'REJECTED';

export interface ProfileCard {
  id: number;
  fullName: string;
  graduationYear: number | null;
  department: string | null;
  currentPosition: string | null;
  company: string | null;
  city: string | null;
  country: string | null;
  shortDescription: string | null;
  photoUrl: string | null;
  questionCount: number;
  tags: Tag[];
}

export interface ProfileDetail {
  id: number;
  fullName: string;
  graduationYear: number | null;
  department: string | null;
  currentPosition: string | null;
  company: string | null;
  city: string | null;
  country: string | null;
  careerDescription: string | null;
  interestsDescription: string | null;
  photoUrl: string | null;
  questionCount: number;
  tags: Tag[];
  publishedAt: string | null;
}

export interface AlumniProfile {
  id: number;
  fullName: string | null;
  graduationYear: number | null;
  department: string | null;
  currentPosition: string | null;
  company: string | null;
  city: string | null;
  country: string | null;
  careerDescription: string | null;
  interestsDescription: string | null;
  photoUrl: string | null;
  status: ProfileStatus;
  moderationComment: string | null;
  questionCount: number;
  tags: Tag[];
  publishedAt: string | null;
  updatedAt: string;
}

export interface UpdateProfilePayload {
  fullName: string;
  graduationYear?: number | null;
  department?: string | null;
  currentPosition?: string | null;
  company?: string | null;
  city?: string | null;
  country?: string | null;
  careerDescription?: string | null;
  interestsDescription?: string | null;
  tagSlugs: string[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export type InviteStatus = 'CREATED' | 'SENT' | 'USED' | 'EXPIRED' | 'REVOKED';

export interface Invite {
  id: number;
  email: string;
  role: Role;
  status: InviteStatus;
  createdAt: string;
  expiresAt: string;
  usedAt: string | null;
  revokedAt: string | null;
  note: string | null;
}

export type InviteValidationResult = 'VALID' | 'INVALID' | 'EXPIRED' | 'USED' | 'REVOKED';

export interface InviteValidation {
  result: InviteValidationResult;
  email: string | null;
  role: Role | null;
}

export interface AlumniQuestion {
  id: number;
  senderName: string | null;
  senderEmail: string | null;
  questionText: string;
  read: boolean;
  answerText: string | null;
  answeredAt: string | null;
  createdAt: string;
}

export interface PublicQuestion {
  id: number;
  senderName: string | null;
  questionText: string;
  answerText: string | null;
  createdAt: string;
  answeredAt: string | null;
}

export type QuestionStatus =
  | 'PENDING_MODERATION'
  | 'AI_APPROVED'
  | 'AI_REJECTED'
  | 'PENDING_ADMIN_REVIEW'
  | 'APPROVED_BY_ADMIN'
  | 'REJECTED_BY_ADMIN'
  | 'VISIBLE_TO_ALUMNI'
  | 'ARCHIVED';

export type AiModerationStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'NEEDS_REVIEW';

export interface AdminQuestion {
  id: number;
  alumniProfileId: number;
  alumniName: string | null;
  senderName: string | null;
  senderEmail: string | null;
  questionText: string;
  status: QuestionStatus;
  aiModerationStatus: AiModerationStatus | null;
  aiModerationReason: string | null;
  adminModerationComment: string | null;
  readByAlumni: boolean;
  createdAt: string;
}

export interface AdminAlumni {
  userId: number;
  email: string;
  enabled: boolean;
  profileId: number | null;
  fullName: string | null;
  profileStatus: ProfileStatus | null;
  company: string | null;
  graduationYear: number | null;
  questionCount: number;
  registeredAt: string;
}

export interface DashboardStats {
  totalAlumni: number;
  publishedProfiles: number;
  profilesOnModeration: number;
  totalQuestions: number;
  questionsOnModeration: number;
  rejectedQuestions: number;
  usedInvites: number;
  pendingInvites: number;
}
