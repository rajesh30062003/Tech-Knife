import {
  ResearchProfile,
  Publication,
  TeachingExperience,
  ProfessionalMembership,
  AwardAchievement,
  SeminarWorkshop,
  ProfileTimelineItem,
} from '../types/faculty';

const STORAGE_KEYS = {
  RESEARCH: 'tech_knife_faculty_research_profile',
  PUBLICATIONS: 'tech_knife_faculty_publications',
  TEACHING: 'tech_knife_faculty_teaching',
  MEMBERSHIPS: 'tech_knife_faculty_memberships',
  AWARDS: 'tech_knife_faculty_awards',
  SEMINARS: 'tech_knife_faculty_seminars',
  TIMELINE: 'tech_knife_faculty_timeline',
};

// Seed Mock Research Profile
const defaultResearchProfile: ResearchProfile = {
  id: 'rp-101',
  facultyId: 'EMP-1002',
  orcidId: '0000-0002-1825-0097',
  scopusId: '57201928300',
  googleScholarUrl: 'https://scholar.google.com/citations?user=techknife_prof',
  researchGateUrl: 'https://www.researchgate.net/profile/Dr-Alexander-Vance',
  researchInterests: [
    'Deep Learning & Vision Transformers',
    'Edge AI & Neuromorphic Computing',
    'Bio-Medical Signal Processing',
    'Distributed Systems & High-Performance Computing',
  ],
  researchAreas: [
    'Artificial Intelligence & Machine Learning',
    'Embedded Intelligent Systems',
    'Biomedical Engineering Analytics',
  ],
  collaborators: [
    { name: 'Dr. Elena Rostova', institution: 'MIT Computer Science & AI Lab', country: 'United States' },
    { name: 'Prof. Hiroshi Tanaka', institution: 'University of Tokyo', country: 'Japan' },
    { name: 'Dr. Marcus Vance', institution: 'Imperial College London', country: 'United Kingdom' },
    { name: 'Prof. Rajesh Sharma', institution: 'Indian Institute of Technology Delhi', country: 'India' },
  ],
  projects: [
    {
      id: 'proj-101',
      title: 'Autonomous Precision Diagnostic Framework for Neurological Disorders using Vision Transformers',
      role: 'Principal Investigator (PI)',
      fundingAgency: 'National Science & Technology Research Council (NSTRC)',
      grantAmount: 250000,
      startDate: '2023-01-15',
      endDate: '2026-01-14',
      status: 'Ongoing',
      description: 'Developing low-power, edge-deployable deep neural network architectures for real-time MRI anomaly segmentation.',
    },
    {
      id: 'proj-102',
      title: 'Distributed Federated Learning for Privacy-Preserving Enterprise Healthcare Cloud',
      role: 'Co-Principal Investigator (Co-PI)',
      fundingAgency: 'Global Health & Tech Foundation',
      grantAmount: 180000,
      startDate: '2021-06-01',
      endDate: '2023-12-31',
      status: 'Completed',
      description: 'Multi-institutional federated learning system protecting sensitive patient electronic health records across node clusters.',
    },
  ],
  grants: [
    {
      id: 'grant-201',
      grantName: 'NSTRC Advanced Innovation Fellowship Grant',
      sponsoringAgency: 'Ministry of Science & Innovation',
      amount: 250000,
      grantNumber: 'NSTRC-AI-2023-8821',
      startYear: 2023,
      endYear: 2026,
      status: 'In Progress',
    },
    {
      id: 'grant-202',
      grantName: 'Enterprise AI Seed Research Grant',
      sponsoringAgency: 'Tech Knife Global R&D Council',
      amount: 75000,
      grantNumber: 'TK-RND-2022-04',
      startYear: 2022,
      endYear: 2023,
      status: 'Closed',
    },
  ],
  supervisions: [
    {
      id: 'sup-301',
      studentName: 'Aarav Mehta',
      degree: 'Ph.D.',
      thesisTitle: 'Self-Supervised Representation Learning on Ultra-Sparse Medical Datasets',
      status: 'Ongoing',
      year: 2024,
    },
    {
      id: 'sup-302',
      studentName: 'Sophia Chen',
      degree: 'Ph.D.',
      thesisTitle: 'Efficient Quantization Techniques for Edge Neural Accelerators',
      status: 'Awarded',
      year: 2023,
    },
    {
      id: 'sup-303',
      studentName: 'David K. Miller',
      degree: 'M.Tech',
      thesisTitle: 'Real-Time Edge Traffic Analytics with YOLOv8',
      status: 'Awarded',
      year: 2022,
    },
  ],
  totalCitations: 1420,
  hIndex: 18,
  i10Index: 26,
};

// Seed Publications
const defaultPublications: Publication[] = [
  {
    id: 'pub-01',
    title: 'Attention-Guided Vision Transformers for High-Resolution Medical Image Segmentation',
    authors: ['Dr. Alexander Vance', 'Elena Rostova', 'Aarav Mehta'],
    journal: 'IEEE Transactions on Pattern Analysis and Machine Intelligence (TPAMI)',
    publisher: 'IEEE',
    issnIsbn: '0162-8828',
    volume: '45',
    issue: '8',
    pages: '10214-10228',
    doi: '10.1109/TPAMI.2023.3289012',
    publicationDate: '2023-08-15',
    abstract: 'We present a novel lightweight vision transformer architecture that leverages spatial attention mechanisms to achieve state-of-the-art segmentation accuracy on multi-modal MRI scans with minimal computational latency.',
    keywords: ['Vision Transformer', 'Medical Imaging', 'Deep Learning', 'Image Segmentation'],
    attachments: [
      { name: 'IEEE_TPAMI_2023_Vance.pdf', url: 'https://example.com/papers/tpami-2023.pdf', size: '2.4 MB' },
    ],
    externalUrl: 'https://doi.org/10.1109/TPAMI.2023.3289012',
    status: 'Published',
    type: 'Journal',
    citationsCount: 245,
    createdAt: '2023-08-16',
  },
  {
    id: 'pub-02',
    title: 'Privacy-Preserving Federated Learning for Distributed Healthcare Analytics',
    authors: ['Dr. Alexander Vance', 'Hiroshi Tanaka', 'Sophia Chen'],
    journal: 'ACM Transactions on Intelligent Systems and Technology (TIST)',
    publisher: 'ACM',
    issnIsbn: '2157-6904',
    volume: '14',
    issue: '3',
    pages: '45:1-45:22',
    doi: '10.1145/3581201',
    publicationDate: '2022-05-10',
    abstract: 'In this paper, we establish a differential privacy guarantee framework combined with homomorphic encryption to ensure cross-institutional patient data security during global model training.',
    keywords: ['Federated Learning', 'Healthcare AI', 'Differential Privacy', 'Distributed Systems'],
    attachments: [
      { name: 'ACM_TIST_Federated_Healthcare.pdf', url: 'https://example.com/papers/tist-2022.pdf', size: '1.8 MB' },
    ],
    externalUrl: 'https://doi.org/10.1145/3581201',
    status: 'Published',
    type: 'Journal',
    citationsCount: 312,
    createdAt: '2022-05-11',
  },
  {
    id: 'pub-03',
    title: 'Ultra-Low Power Neuromorphic AI Accelerators for Edge Diagnostic Devices',
    authors: ['Dr. Alexander Vance', 'Marcus Vance'],
    journal: 'International Conference on Computer Vision (ICCV 2023)',
    publisher: 'IEEE/CVF',
    issnIsbn: '978-1-6654-9872-1',
    volume: 'Proc. ICCV 2023',
    issue: 'Oral',
    pages: '4120-4130',
    doi: '10.1109/ICCV51201.2023.1023',
    publicationDate: '2023-10-02',
    abstract: 'Demonstrating sub-milliwatt power inference performance for real-time cardiac arrhythmia detection running directly on microcontroller hardware.',
    keywords: ['Neuromorphic Computing', 'Edge AI', 'Sub-mW Inference', 'Microcontrollers'],
    attachments: [],
    externalUrl: 'https://iccv2023.thecvf.com/paper/4120',
    status: 'Published',
    type: 'Conference',
    citationsCount: 180,
    createdAt: '2023-10-05',
  },
  {
    id: 'pub-04',
    title: 'Enterprise AI Infrastructure: Design, Deployment, and MLOps at Scale',
    authors: ['Dr. Alexander Vance', 'Rajesh Sharma'],
    journal: 'Springer Nature Academic Press',
    publisher: 'Springer Nature',
    issnIsbn: '978-3-031-29910-4',
    volume: '1st Edition',
    issue: 'Hardcover',
    pages: '1-380',
    doi: '10.1007/978-3-031-29910-4',
    publicationDate: '2022-11-20',
    abstract: 'A comprehensive textbook covering end-to-end Machine Learning Operations, model deployment pipelines, distributed GPU orchestration, and enterprise model governance.',
    keywords: ['MLOps', 'Enterprise Architecture', 'Model Governance', 'Deep Learning Infrastructure'],
    attachments: [],
    externalUrl: 'https://link.springer.com/book/10.1007/978-3-031-29910-4',
    status: 'Published',
    type: 'Book',
    citationsCount: 420,
    createdAt: '2022-11-22',
  },
  {
    id: 'pub-05',
    title: 'Generative AI and Large Language Models in Higher Education Administration',
    authors: ['Dr. Alexander Vance'],
    journal: 'IEEE Higher Education Case Studies Journal',
    publisher: 'IEEE Education Society',
    issnIsbn: '2380-1122',
    volume: '10',
    issue: '2',
    pages: '14-29',
    doi: '10.1109/MHER.2024.0118',
    publicationDate: '2024-02-01',
    abstract: 'An empirical case study evaluating automated student feedback, curriculum alignment, and ethical AI policies implemented across multi-campus universities.',
    keywords: ['Generative AI', 'Higher Education', 'LLMs', 'EdTech'],
    attachments: [],
    externalUrl: 'https://doi.org/10.1109/MHER.2024.0118',
    status: 'Published',
    type: 'Case Study',
    citationsCount: 88,
    createdAt: '2024-02-02',
  },
  {
    id: 'pub-06',
    title: 'Next-Generation Spiking Neural Networks for Spatial Audio Localization',
    authors: ['Dr. Alexander Vance', 'Elena Rostova'],
    journal: 'IEEE Signal Processing Letters',
    publisher: 'IEEE',
    issnIsbn: '1070-9908',
    volume: 'Under Review',
    issue: '-',
    pages: '1-6',
    doi: '',
    publicationDate: '2024-05-10',
    abstract: 'Investigating temporal coding mechanisms in spiking neural networks to drastically reduce computational requirements for binaural audio localization.',
    keywords: ['Spiking Neural Networks', 'Audio Signal Processing', 'Temporal Coding'],
    attachments: [],
    externalUrl: '',
    status: 'Under Review',
    type: 'Journal',
    citationsCount: 0,
    createdAt: '2024-05-11',
  },
];

// Seed Teaching Experience
const defaultTeachingExperience: TeachingExperience[] = [
  {
    id: 'teach-101',
    institution: 'Tech Knife Enterprise University / Institute of Technology',
    department: 'Department of Computer Science & Artificial Intelligence',
    designation: 'Professor & Head of Research',
    joiningDate: '2021-08-01',
    isCurrent: true,
    employmentType: 'Full-Time',
    subjectsTaught: [
      'CS-801: Advanced Vision Transformers & Deep Learning',
      'CS-705: Enterprise MLOps & Distributed AI',
      'CS-602: Neuromorphic Computing & Edge Systems',
    ],
    classesHandled: ['Ph.D. Research Cohort 2023-2026', 'M.Tech AI Specialization Year 2'],
    teachingHoursPerWeek: 12,
    clinicalPosting: 'N/A',
    administrativeResponsibility: 'Director of AI Excellence Center, University Research Council Chair',
  },
  {
    id: 'teach-102',
    institution: 'National Institute of Computer Sciences',
    department: 'Department of Software Engineering',
    designation: 'Associate Professor',
    joiningDate: '2017-06-15',
    endDate: '2021-07-31',
    isCurrent: false,
    employmentType: 'Full-Time',
    subjectsTaught: [
      'SE-401: Distributed Systems & Microservices',
      'CS-503: Machine Learning Algorithms',
      'SE-302: Operating Systems Internals',
    ],
    classesHandled: ['B.Tech Computer Science Final Year', 'M.S. Software Engineering'],
    teachingHoursPerWeek: 16,
    clinicalPosting: 'N/A',
    administrativeResponsibility: 'Department Academic Coordinator, Curriculum Re-vision Committee Lead',
  },
];

// Seed Professional Memberships
const defaultMemberships: ProfessionalMembership[] = [
  {
    id: 'mem-01',
    organization: 'Institute of Electrical and Electronics Engineers (IEEE)',
    membershipNumber: 'IEEE-92184029',
    membershipType: 'Senior Member',
    validUntil: '2028-12-31',
    certificateUrl: 'https://example.com/certificates/ieee-senior-member.pdf',
  },
  {
    id: 'mem-02',
    organization: 'Association for Computing Machinery (ACM)',
    membershipNumber: 'ACM-7729104',
    membershipType: 'Life Member',
    validUntil: '2035-12-31',
    certificateUrl: 'https://example.com/certificates/acm-life.pdf',
  },
  {
    id: 'mem-03',
    organization: 'International Society for Computational Biology (ISCB)',
    membershipNumber: 'ISCB-2022-811',
    membershipType: 'Fellow',
    validUntil: '2026-06-30',
    certificateUrl: 'https://example.com/certificates/iscb-fellow.pdf',
  },
];

// Seed Awards & Achievements
const defaultAwards: AwardAchievement[] = [
  {
    id: 'award-01',
    awardTitle: 'Excellence in AI Research & Innovation Gold Medal',
    awardingOrganization: 'National Academy of Science & Engineering',
    year: 2023,
    description: 'Recognized for breakthrough contributions to edge vision transformers and privacy-preserving federated healthcare algorithms.',
    certificateUrl: 'https://example.com/certificates/award-2023.pdf',
    imageUrl: 'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&q=80&w=600',
  },
  {
    id: 'award-02',
    awardTitle: 'Best Paper Award (Oral Presentation)',
    awardingOrganization: 'International Conference on Computer Vision (ICCV)',
    year: 2022,
    description: 'Awarded for top-ranked research paper on ultra-low power spiking neural networks.',
    certificateUrl: 'https://example.com/certificates/iccv-best-paper.pdf',
    imageUrl: 'https://images.unsplash.com/photo-1567427017947-545c5f8d16ad?auto=format&fit=crop&q=80&w=600',
  },
  {
    id: 'award-03',
    awardTitle: 'Outstanding Educator & Mentor Award',
    awardingOrganization: 'Tech Knife Higher Education Council',
    year: 2021,
    description: 'Honored for exceptional mentorship of Ph.D. scholars and high-impact undergraduate research publications.',
    certificateUrl: 'https://example.com/certificates/educator-award.pdf',
  },
];

// Seed Seminars & Workshops
const defaultSeminars: SeminarWorkshop[] = [
  {
    id: 'sem-01',
    title: 'Keynote Address: Scaling Vision Transformers for Real-Time Edge Intelligence',
    category: 'Conference',
    role: 'Keynote',
    organization: 'Global IEEE AI Summit 2024 (Tokyo, Japan)',
    date: '2024-03-12',
    certificateUrl: 'https://example.com/certs/keynote-tokyo.pdf',
  },
  {
    id: 'sem-02',
    title: 'Faculty Development Program on Generative AI & MLOps Pipelines',
    category: 'FDP',
    role: 'Organizer',
    organization: 'Tech Knife Center for Advanced Academic Research',
    date: '2023-11-20',
    certificateUrl: 'https://example.com/certs/fdp-mlops.pdf',
  },
  {
    id: 'sem-03',
    title: 'International Hands-on Workshop on Quantization & TensorRT Inference',
    category: 'Workshop',
    role: 'Speaker',
    organization: 'ACM SIGKDD Chapter Conference',
    date: '2023-07-14',
    certificateUrl: 'https://example.com/certs/workshop-tensorrt.pdf',
  },
  {
    id: 'sem-04',
    title: 'Continuous Medical Education (CME) on AI-Driven Radiology Diagnostics',
    category: 'CME',
    role: 'Speaker',
    organization: 'University Medical College & Research Hospital',
    date: '2022-09-08',
    certificateUrl: 'https://example.com/certs/cme-radiology.pdf',
  },
];

// Seed Profile Timeline Items
const defaultTimelineItems: ProfileTimelineItem[] = [
  {
    id: 'time-01',
    year: 2024,
    date: 'March 2024',
    category: 'Research',
    title: 'Delivered Keynote Speech at IEEE AI Summit Tokyo',
    organization: 'IEEE Computational Intelligence Society',
    description: 'Presented landmark research on Sub-mW vision transformers for medical edge devices.',
  },
  {
    id: 'time-02',
    year: 2023,
    date: 'August 2023',
    category: 'Publications',
    title: 'Published Landmark Paper in IEEE TPAMI',
    organization: 'IEEE Transactions on Pattern Analysis and Machine Intelligence',
    description: 'Paper titled "Attention-Guided Vision Transformers for High-Resolution Medical Image Segmentation".',
  },
  {
    id: 'time-03',
    year: 2023,
    date: 'June 2023',
    category: 'Awards',
    title: 'Received National Gold Medal for AI Excellence',
    organization: 'National Academy of Science & Engineering',
    description: 'Honored for pioneering work in privacy-preserving federated healthcare AI.',
  },
  {
    id: 'time-04',
    year: 2021,
    date: 'August 2021',
    category: 'Appointments',
    title: 'Appointed as Professor & Head of AI Research',
    organization: 'Tech Knife Enterprise University',
    description: 'Assumed leadership of the Department of Computer Science & AI Research Labs.',
  },
  {
    id: 'time-05',
    year: 2019,
    date: 'December 2019',
    category: 'Promotions',
    title: 'Promoted to Tenured Senior Associate Professor',
    organization: 'National Institute of Computer Sciences',
    description: 'Awarded tenure based on exceptional research funding and Q1 publication index.',
  },
  {
    id: 'time-06',
    year: 2016,
    date: 'May 2016',
    category: 'Education',
    title: 'Awarded Ph.D. in Computer Science & Artificial Intelligence',
    organization: 'Stanford University / MIT Collaborative Doctoral Program',
    description: 'Dissertation: "Deep Neural Networks for Low-Latency Spatio-Temporal Pattern Recognition".',
  },
];

// Helper to load or initialize local storage
function loadFromStorage<T>(key: string, defaultValue: T): T {
  try {
    const item = localStorage.getItem(key);
    if (!item) {
      localStorage.setItem(key, JSON.stringify(defaultValue));
      return defaultValue;
    }
    return JSON.parse(item);
  } catch (err) {
    console.error(`Failed to load ${key} from storage:`, err);
    return defaultValue;
  }
}

function saveToStorage<T>(key: string, value: T): void {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (err) {
    console.error(`Failed to save ${key} to storage:`, err);
  }
}

/**
 * Service API for Faculty Academic Portfolio Management
 */
export const facultyPortfolioApi = {
  // --- Research Profile ---
  async getResearchProfile(): Promise<ResearchProfile> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const profile = loadFromStorage<ResearchProfile>(
          STORAGE_KEYS.RESEARCH,
          defaultResearchProfile
        );
        resolve(profile);
      }, 200);
    });
  },

  async saveResearchProfile(updated: Partial<ResearchProfile>): Promise<ResearchProfile> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const current = loadFromStorage<ResearchProfile>(
          STORAGE_KEYS.RESEARCH,
          defaultResearchProfile
        );
        const merged: ResearchProfile = {
          ...current,
          ...updated,
        };
        saveToStorage(STORAGE_KEYS.RESEARCH, merged);
        resolve(merged);
      }, 300);
    });
  },

  // --- Publications ---
  async getPublications(): Promise<Publication[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const publications = loadFromStorage<Publication[]>(
          STORAGE_KEYS.PUBLICATIONS,
          defaultPublications
        );
        resolve(publications);
      }, 200);
    });
  },

  async createPublication(pubData: Omit<Publication, 'id' | 'createdAt'>): Promise<Publication> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const list = loadFromStorage<Publication[]>(
          STORAGE_KEYS.PUBLICATIONS,
          defaultPublications
        );

        // Check for duplicate title (case insensitive)
        const isDuplicate = list.some(
          (p) => p.title.trim().toLowerCase() === pubData.title.trim().toLowerCase()
        );

        if (isDuplicate) {
          reject(new Error('A publication with this exact title already exists in your academic portfolio.'));
          return;
        }

        const newPub: Publication = {
          ...pubData,
          id: `pub-${Date.now()}`,
          createdAt: new Date().toISOString().split('T')[0],
          citationsCount: pubData.citationsCount || 0,
        };

        const updatedList = [newPub, ...list];
        saveToStorage(STORAGE_KEYS.PUBLICATIONS, updatedList);

        // Auto add to profile timeline
        try {
          const timeline = loadFromStorage<ProfileTimelineItem[]>(
            STORAGE_KEYS.TIMELINE,
            defaultTimelineItems
          );
          const year = new Date(newPub.publicationDate).getFullYear() || new Date().getFullYear();
          const monthStr = new Date(newPub.publicationDate).toLocaleString('default', { month: 'long' });
          const newTimelineItem: ProfileTimelineItem = {
            id: `time-${Date.now()}`,
            year,
            date: `${monthStr} ${year}`,
            category: 'Publications',
            title: `Published ${newPub.type}: ${newPub.title}`,
            organization: newPub.journal || newPub.publisher || 'Academic Publisher',
            description: newPub.abstract || `Authors: ${newPub.authors.join(', ')}`,
          };
          saveToStorage(STORAGE_KEYS.TIMELINE, [newTimelineItem, ...timeline]);
        } catch (e) {
          console.error('Failed to sync timeline:', e);
        }

        resolve(newPub);
      }, 300);
    });
  },

  async updatePublication(id: string, pubData: Partial<Publication>): Promise<Publication> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const list = loadFromStorage<Publication[]>(
          STORAGE_KEYS.PUBLICATIONS,
          defaultPublications
        );

        const index = list.findIndex((p) => p.id === id);
        if (index === -1) {
          reject(new Error('Publication record not found.'));
          return;
        }

        if (pubData.title) {
          const isDuplicate = list.some(
            (p) => p.id !== id && p.title.trim().toLowerCase() === pubData.title?.trim().toLowerCase()
          );
          if (isDuplicate) {
            reject(new Error('Another publication with this exact title already exists.'));
            return;
          }
        }

        const updated = { ...list[index], ...pubData };
        list[index] = updated;
        saveToStorage(STORAGE_KEYS.PUBLICATIONS, list);
        resolve(updated);
      }, 300);
    });
  },

  async deletePublication(id: string): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const list = loadFromStorage<Publication[]>(
          STORAGE_KEYS.PUBLICATIONS,
          defaultPublications
        );
        const filtered = list.filter((p) => p.id !== id);
        saveToStorage(STORAGE_KEYS.PUBLICATIONS, filtered);
        resolve(true);
      }, 200);
    });
  },

  // --- Teaching Experience ---
  async getTeachingExperience(): Promise<TeachingExperience[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const experiences = loadFromStorage<TeachingExperience[]>(
          STORAGE_KEYS.TEACHING,
          defaultTeachingExperience
        );
        resolve(experiences);
      }, 200);
    });
  },

  async saveTeachingExperience(experiences: TeachingExperience[]): Promise<TeachingExperience[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        saveToStorage(STORAGE_KEYS.TEACHING, experiences);
        resolve(experiences);
      }, 300);
    });
  },

  // --- Memberships ---
  async getMemberships(): Promise<ProfessionalMembership[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const items = loadFromStorage<ProfessionalMembership[]>(
          STORAGE_KEYS.MEMBERSHIPS,
          defaultMemberships
        );
        resolve(items);
      }, 200);
    });
  },

  async saveMembership(item: ProfessionalMembership): Promise<ProfessionalMembership> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const list = loadFromStorage<ProfessionalMembership[]>(
          STORAGE_KEYS.MEMBERSHIPS,
          defaultMemberships
        );
        const idx = list.findIndex((m) => m.id === item.id);
        if (idx !== -1) {
          list[idx] = item;
        } else {
          list.unshift(item);
        }
        saveToStorage(STORAGE_KEYS.MEMBERSHIPS, list);
        resolve(item);
      }, 250);
    });
  },

  async deleteMembership(id: string): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const list = loadFromStorage<ProfessionalMembership[]>(
          STORAGE_KEYS.MEMBERSHIPS,
          defaultMemberships
        );
        saveToStorage(
          STORAGE_KEYS.MEMBERSHIPS,
          list.filter((m) => m.id !== id)
        );
        resolve(true);
      }, 200);
    });
  },

  // --- Awards ---
  async getAwards(): Promise<AwardAchievement[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const items = loadFromStorage<AwardAchievement[]>(
          STORAGE_KEYS.AWARDS,
          defaultAwards
        );
        resolve(items);
      }, 200);
    });
  },

  async saveAward(item: AwardAchievement): Promise<AwardAchievement> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const list = loadFromStorage<AwardAchievement[]>(
          STORAGE_KEYS.AWARDS,
          defaultAwards
        );
        const idx = list.findIndex((a) => a.id === item.id);
        if (idx !== -1) {
          list[idx] = item;
        } else {
          list.unshift(item);
        }
        saveToStorage(STORAGE_KEYS.AWARDS, list);
        resolve(item);
      }, 250);
    });
  },

  async deleteAward(id: string): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const list = loadFromStorage<AwardAchievement[]>(
          STORAGE_KEYS.AWARDS,
          defaultAwards
        );
        saveToStorage(
          STORAGE_KEYS.AWARDS,
          list.filter((a) => a.id !== id)
        );
        resolve(true);
      }, 200);
    });
  },

  // --- Seminars & Workshops ---
  async getSeminars(): Promise<SeminarWorkshop[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const items = loadFromStorage<SeminarWorkshop[]>(
          STORAGE_KEYS.SEMINARS,
          defaultSeminars
        );
        resolve(items);
      }, 200);
    });
  },

  async saveSeminar(item: SeminarWorkshop): Promise<SeminarWorkshop> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const list = loadFromStorage<SeminarWorkshop[]>(
          STORAGE_KEYS.SEMINARS,
          defaultSeminars
        );
        const idx = list.findIndex((s) => s.id === item.id);
        if (idx !== -1) {
          list[idx] = item;
        } else {
          list.unshift(item);
        }
        saveToStorage(STORAGE_KEYS.SEMINARS, list);
        resolve(item);
      }, 250);
    });
  },

  async deleteSeminar(id: string): Promise<boolean> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const list = loadFromStorage<SeminarWorkshop[]>(
          STORAGE_KEYS.SEMINARS,
          defaultSeminars
        );
        saveToStorage(
          STORAGE_KEYS.SEMINARS,
          list.filter((s) => s.id !== id)
        );
        resolve(true);
      }, 200);
    });
  },

  // --- Profile Timeline ---
  async getProfileTimeline(): Promise<ProfileTimelineItem[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const items = loadFromStorage<ProfileTimelineItem[]>(
          STORAGE_KEYS.TIMELINE,
          defaultTimelineItems
        );
        resolve(items.sort((a, b) => b.year - a.year));
      }, 200);
    });
  },
};
