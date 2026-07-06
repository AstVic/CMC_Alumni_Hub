import { http } from './httpClient';
import type {
  AdminAccount,
  AdminAlumni,
  AdminQuestion,
  AlumniProfile,
  DashboardStats,
  Invite,
  QuestionStatus,
  Tag,
  UpdateProfilePayload,
} from '../types';

export interface TagPayload {
  name: string;
  slug: string;
  category?: string;
}

export const adminApi = {
  // Dashboard
  async dashboard(): Promise<DashboardStats> {
    const { data } = await http.get<DashboardStats>('/admin/dashboard');
    return data;
  },

  // Invites
  async listInvites(): Promise<Invite[]> {
    const { data } = await http.get<Invite[]>('/admin/invites');
    return data;
  },
  async createInvite(email: string, note?: string): Promise<Invite> {
    const { data } = await http.post<Invite>('/admin/invites', { email, note });
    return data;
  },
  async resendInvite(id: number): Promise<Invite> {
    const { data } = await http.post<Invite>(`/admin/invites/${id}/resend`);
    return data;
  },
  async revokeInvite(id: number): Promise<Invite> {
    const { data } = await http.patch<Invite>(`/admin/invites/${id}/revoke`);
    return data;
  },

  // Alumni
  async listAlumni(): Promise<AdminAlumni[]> {
    const { data } = await http.get<AdminAlumni[]>('/admin/alumni');
    return data;
  },
  async getAlumni(userId: number): Promise<AlumniProfile> {
    const { data } = await http.get<AlumniProfile>(`/admin/alumni/${userId}`);
    return data;
  },
  async updateAlumni(userId: number, payload: UpdateProfilePayload): Promise<AlumniProfile> {
    const { data } = await http.put<AlumniProfile>(`/admin/alumni/${userId}`, payload);
    return data;
  },
  async setBlocked(userId: number, blocked: boolean): Promise<AdminAlumni> {
    const { data } = await http.patch<AdminAlumni>(`/admin/alumni/${userId}/block`, null, {
      params: { blocked },
    });
    return data;
  },

  // Profile moderation
  async allProfiles(): Promise<AlumniProfile[]> {
    const { data } = await http.get<AlumniProfile[]>('/admin/profiles');
    return data;
  },
  async profilesModeration(): Promise<AlumniProfile[]> {
    const { data } = await http.get<AlumniProfile[]>('/admin/profiles/moderation');
    return data;
  },
  async approveProfile(id: number): Promise<AlumniProfile> {
    const { data } = await http.patch<AlumniProfile>(`/admin/profiles/${id}/approve`);
    return data;
  },
  async rejectProfile(id: number, comment: string): Promise<AlumniProfile> {
    const { data } = await http.patch<AlumniProfile>(`/admin/profiles/${id}/reject`, { comment });
    return data;
  },

  // Questions
  async listQuestions(status?: QuestionStatus): Promise<AdminQuestion[]> {
    const { data } = await http.get<AdminQuestion[]>('/admin/questions', {
      params: status ? { status } : undefined,
    });
    return data;
  },
  async questionsModeration(): Promise<AdminQuestion[]> {
    const { data } = await http.get<AdminQuestion[]>('/admin/questions/moderation');
    return data;
  },
  async questionsRejected(): Promise<AdminQuestion[]> {
    const { data } = await http.get<AdminQuestion[]>('/admin/questions/rejected');
    return data;
  },
  async approveQuestion(id: number): Promise<AdminQuestion> {
    const { data } = await http.patch<AdminQuestion>(`/admin/questions/${id}/approve`);
    return data;
  },
  async rejectQuestion(id: number, comment?: string): Promise<AdminQuestion> {
    const { data } = await http.patch<AdminQuestion>(`/admin/questions/${id}/reject`, { comment });
    return data;
  },

  // Admin accounts (owner only)
  async listAdmins(): Promise<AdminAccount[]> {
    const { data } = await http.get<AdminAccount[]>('/admin/admins');
    return data;
  },
  async createAdmin(email: string, password: string): Promise<AdminAccount> {
    const { data } = await http.post<AdminAccount>('/admin/admins', { email, password });
    return data;
  },
  async setAdminBlocked(id: number, blocked: boolean): Promise<AdminAccount> {
    const { data } = await http.patch<AdminAccount>(`/admin/admins/${id}/block`, null, {
      params: { blocked },
    });
    return data;
  },
  async transferOwnership(id: number): Promise<AdminAccount> {
    const { data } = await http.patch<AdminAccount>(`/admin/admins/${id}/transfer-ownership`);
    return data;
  },
  async listAdminInvites(): Promise<Invite[]> {
    const { data } = await http.get<Invite[]>('/admin/admins/invites');
    return data;
  },
  async createAdminInvite(email: string, note?: string): Promise<Invite> {
    const { data } = await http.post<Invite>('/admin/admins/invites', { email, note });
    return data;
  },
  async resendAdminInvite(id: number): Promise<Invite> {
    const { data } = await http.post<Invite>(`/admin/admins/invites/${id}/resend`);
    return data;
  },
  async revokeAdminInvite(id: number): Promise<Invite> {
    const { data } = await http.patch<Invite>(`/admin/admins/invites/${id}/revoke`);
    return data;
  },

  // Tags
  async listTags(): Promise<Tag[]> {
    const { data } = await http.get<Tag[]>('/admin/tags');
    return data;
  },
  async createTag(payload: TagPayload): Promise<Tag> {
    const { data } = await http.post<Tag>('/admin/tags', payload);
    return data;
  },
  async updateTag(id: number, payload: TagPayload): Promise<Tag> {
    const { data } = await http.put<Tag>(`/admin/tags/${id}`, payload);
    return data;
  },
  async deleteTag(id: number): Promise<void> {
    await http.delete(`/admin/tags/${id}`);
  },
};
