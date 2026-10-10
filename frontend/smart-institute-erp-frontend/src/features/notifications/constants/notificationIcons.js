import CampaignOutlinedIcon from '@mui/icons-material/CampaignOutlined';
import PaymentsOutlinedIcon from '@mui/icons-material/PaymentsOutlined';
import EventAvailableOutlinedIcon from '@mui/icons-material/EventAvailableOutlined';
import AssignmentOutlinedIcon from '@mui/icons-material/AssignmentOutlined';
import GroupsOutlinedIcon from '@mui/icons-material/GroupsOutlined';
import NotificationsOutlinedIcon from '@mui/icons-material/NotificationsOutlined';
import { NOTIFICATION_TYPE } from '@constants/enums';

// Maps each notification type to a representative icon — centralized so
// the bell dropdown and full list page render identically.
export function getNotificationIcon(type) {
  switch (type) {
    case NOTIFICATION_TYPE.ANNOUNCEMENT:
      return CampaignOutlinedIcon;
    case NOTIFICATION_TYPE.FEE_REMINDER:
    case NOTIFICATION_TYPE.FEE_PAYMENT_SUCCESS:
      return PaymentsOutlinedIcon;
    case NOTIFICATION_TYPE.ATTENDANCE_ALERT:
      return EventAvailableOutlinedIcon;
    case NOTIFICATION_TYPE.EXAM_SCHEDULE:
    case NOTIFICATION_TYPE.EXAM_RESULT:
      return AssignmentOutlinedIcon;
    case NOTIFICATION_TYPE.BATCH_UPDATE:
      return GroupsOutlinedIcon;
    default:
      return NotificationsOutlinedIcon;
  }
}