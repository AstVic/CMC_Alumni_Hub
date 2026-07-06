import { http } from './httpClient';
import type {
  InviteValidation,
  PageResponse,
  ProfileCard,
  ProfileDetail,
  Tag,
  TokenResponse,
} from '../types';

export interface CatalogParams {
  search?: string;
  tags?: string[];
  graduationYear?: number;
  company?: string;
  sort?: string;
  page?: number;
  size?: number;
}

export interface QuestionPayload {
  senderName?: string;
  senderEmail?: string;
  questionText: string;
  acceptedRules: boolean;
  website?: string; // honeypot
}

export interface RegisterByInvitePayload {
  token: string;
  fullName: string;
  password: string;
  passwordConfirm: string;
  acceptedRules: boolean;
}

export const publicApi = {
  async listProfiles(params: CatalogParams): Promise<PageResponse<ProfileCard>> {
    const { data } = await http.get<PageResponse<ProfileCard>>('/public/profiles', {
      params: {
        ...params,
        tags: params.tags?.length ? params.tags.join(',') : undefined,
      },
    });
    return data;
  },
  async getProfile(id: number): Promise<ProfileDetail> {
    const { data } = await http.get<ProfileDetail>(`/public/profiles/${id}`);
    return data;
  },
  async listTags(): Promise<Tag[]> {
    const { data } = await http.get<Tag[]>('/public/tags');
    return data;
  },
  async askQuestion(profileId: number, payload: QuestionPayload): Promise<{ message: string }> {
    const { data } = await http.post(`/public/profiles/${profileId}/questions`, payload);
    return data;
  },
  async validateInvite(token: string): Promise<InviteValidation> {
    const { data } = await http.get<InviteValidation>('/public/invites/validate', {
      params: { token },
    });
    return data;
  },
  async registerByInvite(payload: RegisterByInvitePayload): Promise<TokenResponse> {
    const { data } = await http.post<TokenResponse>('/public/auth/register-by-invite', payload);
    return data;
  },
};
