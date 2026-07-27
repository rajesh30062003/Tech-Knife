import { useState, useEffect, useCallback } from 'react';
import { facultyPortfolioApi } from '../api/facultyPortfolio';
import {
  ResearchProfile,
  Publication,
  TeachingExperience,
  ProfessionalMembership,
  AwardAchievement,
  SeminarWorkshop,
  ProfileTimelineItem,
} from '../types/faculty';

/**
 * Hook for managing Research Profile state & operations
 */
export function useResearchProfile() {
  const [profile, setProfile] = useState<ResearchProfile | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchProfile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await facultyPortfolioApi.getResearchProfile();
      setProfile(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load research profile');
    } finally {
      setLoading(false);
    }
  }, []);

  const saveProfile = useCallback(async (updated: Partial<ResearchProfile>) => {
    try {
      const saved = await facultyPortfolioApi.saveResearchProfile(updated);
      setProfile(saved);
      return saved;
    } catch (err: any) {
      throw new Error(err.message || 'Failed to save research profile');
    }
  }, []);

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  return { profile, loading, error, refetch: fetchProfile, saveProfile };
}

/**
 * Hook for managing Faculty Publications state & CRUD operations
 */
export function useFacultyPublications() {
  const [publications, setPublications] = useState<Publication[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPublications = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await facultyPortfolioApi.getPublications();
      setPublications(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load publications');
    } finally {
      setLoading(false);
    }
  }, []);

  const createPublication = useCallback(async (pubData: Omit<Publication, 'id' | 'createdAt'>) => {
    const created = await facultyPortfolioApi.createPublication(pubData);
    setPublications((prev) => [created, ...prev]);
    return created;
  }, []);

  const updatePublication = useCallback(async (id: string, pubData: Partial<Publication>) => {
    const updated = await facultyPortfolioApi.updatePublication(id, pubData);
    setPublications((prev) => prev.map((p) => (p.id === id ? updated : p)));
    return updated;
  }, []);

  const deletePublication = useCallback(async (id: string) => {
    await facultyPortfolioApi.deletePublication(id);
    setPublications((prev) => prev.filter((p) => p.id !== id));
  }, []);

  useEffect(() => {
    fetchPublications();
  }, [fetchPublications]);

  return {
    publications,
    loading,
    error,
    refetch: fetchPublications,
    createPublication,
    updatePublication,
    deletePublication,
  };
}

/**
 * Hook for managing Teaching Experience state
 */
export function useTeachingExperience() {
  const [experiences, setExperiences] = useState<TeachingExperience[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchTeaching = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await facultyPortfolioApi.getTeachingExperience();
      setExperiences(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load teaching experiences');
    } finally {
      setLoading(false);
    }
  }, []);

  const saveTeaching = useCallback(async (items: TeachingExperience[]) => {
    const saved = await facultyPortfolioApi.saveTeachingExperience(items);
    setExperiences(saved);
    return saved;
  }, []);

  useEffect(() => {
    fetchTeaching();
  }, [fetchTeaching]);

  return { experiences, loading, error, refetch: fetchTeaching, saveTeaching };
}

/**
 * Hook for Professional Memberships
 */
export function useFacultyMemberships() {
  const [memberships, setMemberships] = useState<ProfessionalMembership[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchMemberships = useCallback(async () => {
    setLoading(true);
    try {
      const data = await facultyPortfolioApi.getMemberships();
      setMemberships(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  const saveMembership = useCallback(async (item: ProfessionalMembership) => {
    const saved = await facultyPortfolioApi.saveMembership(item);
    await fetchMemberships();
    return saved;
  }, [fetchMemberships]);

  const deleteMembership = useCallback(async (id: string) => {
    await facultyPortfolioApi.deleteMembership(id);
    setMemberships((prev) => prev.filter((m) => m.id !== id));
  }, []);

  useEffect(() => {
    fetchMemberships();
  }, [fetchMemberships]);

  return { memberships, loading, refetch: fetchMemberships, saveMembership, deleteMembership };
}

/**
 * Hook for Awards & Achievements
 */
export function useFacultyAwards() {
  const [awards, setAwards] = useState<AwardAchievement[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchAwards = useCallback(async () => {
    setLoading(true);
    try {
      const data = await facultyPortfolioApi.getAwards();
      setAwards(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  const saveAward = useCallback(async (item: AwardAchievement) => {
    const saved = await facultyPortfolioApi.saveAward(item);
    await fetchAwards();
    return saved;
  }, [fetchAwards]);

  const deleteAward = useCallback(async (id: string) => {
    await facultyPortfolioApi.deleteAward(id);
    setAwards((prev) => prev.filter((a) => a.id !== id));
  }, []);

  useEffect(() => {
    fetchAwards();
  }, [fetchAwards]);

  return { awards, loading, refetch: fetchAwards, saveAward, deleteAward };
}

/**
 * Hook for Seminars & Workshops
 */
export function useFacultySeminars() {
  const [seminars, setSeminars] = useState<SeminarWorkshop[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchSeminars = useCallback(async () => {
    setLoading(true);
    try {
      const data = await facultyPortfolioApi.getSeminars();
      setSeminars(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  const saveSeminar = useCallback(async (item: SeminarWorkshop) => {
    const saved = await facultyPortfolioApi.saveSeminar(item);
    await fetchSeminars();
    return saved;
  }, [fetchSeminars]);

  const deleteSeminar = useCallback(async (id: string) => {
    await facultyPortfolioApi.deleteSeminar(id);
    setSeminars((prev) => prev.filter((s) => s.id !== id));
  }, []);

  useEffect(() => {
    fetchSeminars();
  }, [fetchSeminars]);

  return { seminars, loading, refetch: fetchSeminars, saveSeminar, deleteSeminar };
}

/**
 * Hook for Profile Timeline
 */
export function useProfileTimeline() {
  const [timeline, setTimeline] = useState<ProfileTimelineItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const fetchTimeline = useCallback(async () => {
    setLoading(true);
    try {
      const data = await facultyPortfolioApi.getProfileTimeline();
      setTimeline(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTimeline();
  }, [fetchTimeline]);

  return { timeline, loading, refetch: fetchTimeline };
}
