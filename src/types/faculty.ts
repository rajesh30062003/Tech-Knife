export type PublicationType =
  | 'Journal'
  | 'Conference'
  | 'Book'
  | 'Book Chapter'
  | 'Case Study'
  | 'Magazine Article'
  | 'Editorial Work';

export type PublicationStatus =
  | 'Published'
  | 'Accepted'
  | 'Under Review'
  | 'Draft';

export interface PublicationAttachment {
  name: string;
  url: string;
  size?: string;
}

export interface Publication {
  id: string;
  title: string;
  authors: string[];
  journal: string;
  publisher?: string;
  issnIsbn?: string;
  volume?: string;
  issue?: string;
  pages?: string;
  doi?: string;
  publicationDate: string;
  abstract?: string;
  keywords?: string[];
  attachments?: PublicationAttachment[];
  externalUrl?: string;
  status: PublicationStatus;
  type: PublicationType;
  citationsCount?: number;
  createdAt?: string;
}

export interface ResearchProject {
  id: string;
  title: string;
  role: 'Principal Investigator (PI)' | 'Co-Principal Investigator (Co-PI)' | 'Research Fellow' | 'Lead Investigator';
  fundingAgency?: string;
  grantAmount?: number;
  startDate: string;
  endDate?: string;
  status: 'Ongoing' | 'Completed' | 'Proposed';
  description?: string;
}

export interface ResearchGrant {
  id: string;
  grantName: string;
  sponsoringAgency: string;
  amount: number;
  grantNumber: string;
  startYear: number;
  endYear: number;
  status: 'Sanctioned' | 'In Progress' | 'Closed';
}

export interface ResearchSupervision {
  id: string;
  studentName: string;
  degree: 'Ph.D.' | 'M.Tech' | 'M.S.' | 'B.Tech / Undergraduate' | 'Post-Doc';
  thesisTitle: string;
  status: 'Ongoing' | 'Awarded' | 'Submitted';
  year: number;
}

export interface Collaborator {
  name: string;
  institution: string;
  country: string;
}

export interface ResearchProfile {
  id: string;
  facultyId: string;
  orcidId?: string;
  scopusId?: string;
  googleScholarUrl?: string;
  researchGateUrl?: string;
  researchInterests: string[];
  researchAreas: string[];
  collaborators: Collaborator[];
  projects: ResearchProject[];
  grants: ResearchGrant[];
  supervisions: ResearchSupervision[];
  totalCitations: number;
  hIndex: number;
  i10Index: number;
}

export type EmploymentType = 'Full-Time' | 'Part-Time' | 'Visiting' | 'Adjunct' | 'Clinical';

export interface TeachingExperience {
  id: string;
  institution: string;
  department: string;
  designation: string;
  joiningDate: string;
  endDate?: string;
  isCurrent: boolean;
  employmentType: EmploymentType;
  subjectsTaught: string[];
  classesHandled: string[];
  teachingHoursPerWeek: number;
  clinicalPosting?: string;
  administrativeResponsibility?: string;
}

export type MembershipType = 'Life Member' | 'Annual Member' | 'Senior Member' | 'Fellow' | 'Student Member';

export interface ProfessionalMembership {
  id: string;
  organization: string;
  membershipNumber: string;
  membershipType: MembershipType;
  validUntil?: string;
  certificateUrl?: string;
}

export interface AwardAchievement {
  id: string;
  awardTitle: string;
  awardingOrganization: string;
  year: number;
  description?: string;
  certificateUrl?: string;
  imageUrl?: string;
}

export type SeminarCategory = 'Conference' | 'Workshop' | 'FDP' | 'CME' | 'Seminar';
export type SeminarRole = 'Speaker' | 'Participant' | 'Organizer' | 'Keynote';

export interface SeminarWorkshop {
  id: string;
  title: string;
  category: SeminarCategory;
  role: SeminarRole;
  organization: string;
  date: string;
  certificateUrl?: string;
}

export type TimelineCategory =
  | 'Education'
  | 'Appointments'
  | 'Promotions'
  | 'Awards'
  | 'Research'
  | 'Publications'
  | 'Training';

export interface ProfileTimelineItem {
  id: string;
  year: number;
  date: string;
  category: TimelineCategory;
  title: string;
  organization: string;
  description?: string;
}
