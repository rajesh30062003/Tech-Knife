# Tech Knife - Disaster Recovery & Continuity Guide

## 1. Recovery Time & Point Objectives
- **Recovery Time Objective (RTO)**: < 15 minutes for critical API services; < 1 hour for full environment restoration.
- **Recovery Point Objective (RPO)**: < 5 minutes for transaction databases (continuous WAL shipping).

---

## 2. Backup Schedules
1. **Database Automated Backups**:
   - Hourly incremental snapshots retained for 7 days.
   - Daily full backups stored in geo-redundant cloud storage retained for 90 days.
2. **Media & Document Assets**:
   - Multi-region bucket replication for all uploaded contracts, resumes, and project assets.
3. **Environment & Secrets Configuration**:
   - Encrypted secrets managed via Google Cloud Secret Manager / environment key vaults.

---

## 3. Disaster Recovery Procedures
1. **Database Restoration**:
   - Identify latest snapshot timestamp prior to incident.
   - Execute point-in-time recovery (PITR) script to provision new database instance.
   - Update database connection string in secret vault.
2. **Application Server Failover**:
   - Container health check automatically re-routes traffic to secondary Cloud Run instance across regions upon health check failure.
