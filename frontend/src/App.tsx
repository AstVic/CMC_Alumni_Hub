import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import { ToastProvider } from './components/ui/Toast';
import { PublicLayout } from './components/layout/PublicLayout';
import { LandingPage } from './pages/public/LandingPage';
import { CatalogPage } from './pages/public/CatalogPage';
import { AlumniDetailPage } from './pages/public/AlumniDetailPage';
import { LoginPage } from './pages/auth/LoginPage';
import { InviteRegisterPage } from './pages/auth/InviteRegisterPage';
import { ForgotPasswordPage } from './pages/auth/ForgotPasswordPage';
import { ResetPasswordPage } from './pages/auth/ResetPasswordPage';
import { AlumniLayout } from './components/layout/AlumniLayout';
import { OnboardingPage } from './pages/alumni/OnboardingPage';
import { AlumniDashboardPage } from './pages/alumni/AlumniDashboardPage';
import { ProfileEditPage } from './pages/alumni/ProfileEditPage';
import { AlumniQuestionsPage } from './pages/alumni/AlumniQuestionsPage';
import { AlumniSecurityPage } from './pages/alumni/AlumniSecurityPage';
import { ProtectedRoute } from './router/ProtectedRoute';
import { AdminLayout } from './components/layout/AdminLayout';
import { AdminDashboardPage } from './pages/admin/AdminDashboardPage';
import { AdminInvitesPage } from './pages/admin/AdminInvitesPage';
import { AdminAlumniPage } from './pages/admin/AdminAlumniPage';
import { AdminProfileModerationPage } from './pages/admin/AdminProfileModerationPage';
import { AdminQuestionsPage } from './pages/admin/AdminQuestionsPage';
import { AdminTagsPage } from './pages/admin/AdminTagsPage';
import { AdminAdminsPage } from './pages/admin/AdminAdminsPage';
import { AdminSecurityPage } from './pages/admin/AdminSecurityPage';
import { NotFoundPage } from './pages/NotFoundPage';

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, refetchOnWindowFocus: false } },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <ToastProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/invite/register" element={<InviteRegisterPage />} />
              <Route path="/forgot-password" element={<ForgotPasswordPage />} />
              <Route path="/reset-password" element={<ResetPasswordPage />} />

              {/* Alumni onboarding (no sidebar) */}
              <Route
                path="/alumni/onboarding"
                element={
                  <ProtectedRoute role="ALUMNI">
                    <OnboardingPage />
                  </ProtectedRoute>
                }
              />

              {/* Alumni cabinet */}
              <Route
                path="/alumni"
                element={
                  <ProtectedRoute role="ALUMNI">
                    <AlumniLayout />
                  </ProtectedRoute>
                }
              >
                <Route index element={<AlumniDashboardPage />} />
                <Route path="profile" element={<ProfileEditPage />} />
                <Route path="questions" element={<AlumniQuestionsPage />} />
                <Route path="security" element={<AlumniSecurityPage />} />
              </Route>

              {/* Admin panel */}
              <Route
                path="/admin"
                element={
                  <ProtectedRoute role="ADMIN">
                    <AdminLayout />
                  </ProtectedRoute>
                }
              >
                <Route index element={<AdminDashboardPage />} />
                <Route path="invites" element={<AdminInvitesPage />} />
                <Route path="alumni" element={<AdminAlumniPage />} />
                <Route path="profiles" element={<AdminProfileModerationPage />} />
                <Route path="questions" element={<AdminQuestionsPage />} />
                <Route path="tags" element={<AdminTagsPage />} />
                <Route path="admins" element={<AdminAdminsPage />} />
                <Route path="security" element={<AdminSecurityPage />} />
              </Route>

              <Route element={<PublicLayout />}>
                <Route index element={<LandingPage />} />
                <Route path="catalog" element={<CatalogPage />} />
                <Route path="alumni-profile/:id" element={<AlumniDetailPage />} />
                <Route path="*" element={<NotFoundPage />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </ToastProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}
