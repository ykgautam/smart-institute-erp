import { useState } from 'react';
import { Typography, Box, Tabs, Tab } from '@mui/material';

// Generic tab shell reused by every multi-report grouping page —
// avoids rebuilding tab-switching logic per report category.
function ReportTabsPage({ title, tabs }) {
  const [activeTab, setActiveTab] = useState(0);

  return (
    <Box>
      <Typography variant="h2" sx={{ mb: 2 }}>{title}</Typography>
      <Tabs value={activeTab} onChange={(_, val) => setActiveTab(val)} sx={{ mb: 3 }}>
        {tabs.map((t) => <Tab key={t.label} label={t.label} />)}
      </Tabs>
      {tabs[activeTab].content}
    </Box>
  );
}

export default ReportTabsPage;