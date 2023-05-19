import moment from "moment";
import React from "react";
import { useTimer } from "react-timer-hook";

export default function CountDownTimer() {
  const time = new Date();
  const endTime = moment('11-04-2023', 'MM-DD-YYYY');
  time.setSeconds(time.getSeconds() +  endTime.diff(time, 'seconds')); // 169 days timer

  const { seconds, minutes, hours, days } = useTimer({ expiryTimestamp: time });

  return (
    <>
      <span>{days} days</span>, <span>{hours} hours</span>,{" "}
      <span>{minutes} minutes</span>, <span>{seconds} secs</span>
    </>
  );
}
