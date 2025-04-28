import axios from "axios";

export const checkIsMaintenanceOn = async () => {
  if (import.meta.env.VITE_IS_DEV) return;

  try {
    const response = await axios.get(
      `${window.location.protocol}//${window.location.hostname}/is-maintenance-mode-on`,
    );

    if (response.data) {
      window.location.reload();
    }
  } catch (error) {
    throw new Error("Action canceled due to maintenance mode.");
  }
};
