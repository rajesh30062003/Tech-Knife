import React from 'react';
import { AuditTrailViewer } from '../../components/core/AuditTrailViewer';

export const AuditLogsPage: React.FC = () => {
  return (
    <div className="pb-12">
      <AuditTrailViewer />
    </div>
  );
};
