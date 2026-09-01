import { Outlet } from 'react-router-dom';
import { Toolbar } from '@mui/material';
import Sidebar from './Sidebar';
import Topbar from './Topbar';
import styles from './layout.module.css';

// The persistent ERP shell. Rendered ONCE as a layout route wrapping all
// protected pages — pages render into <Outlet /> only, they never
// re-implement sidebar/topbar (Section 18 requirement).
function AppLayout() {
  return (
    <div className={styles.shell}>
      <Topbar />
      <Sidebar />
      <div className={styles.mainContent}>
        {/* Spacer matching Topbar's fixed height so content isn't hidden under it */}
        <Toolbar />
        <div className={styles.contentArea}>
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default AppLayout;