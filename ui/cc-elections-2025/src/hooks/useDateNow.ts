import { useEffect, useState } from 'react';

export function useDateNow(interval = 1000) {
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    const timer = setInterval(() => {
      setNow(Date.now());
    }, interval);

    return () => clearInterval(timer); // Cleanup on unmount
  }, [interval]);

  return now;
}
