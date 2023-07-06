import moment from "moment";
import React from "react";
import { useTimer } from "react-timer-hook";

export default function CountDownTimer() {
  const time = new Date();
  const endTime = moment('09-01-2023', 'MM-DD-YYYY'); // summit date
  time.setSeconds(time.getSeconds() +  endTime.diff(time, 'seconds')); // time left for summit date

  const { seconds, minutes, hours, days } = useTimer({ expiryTimestamp: time });

  return (
    <div data-testid="count-down-timer">
      <span>{days} days</span>, <span>{hours} hours</span>, <span>{minutes} minutes</span>, <span>{seconds} secs</span>
    </div>
  );
}
