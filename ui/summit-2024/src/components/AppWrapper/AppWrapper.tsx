import { ReactNode, useEffect } from "react";
import {useAppDispatch, useAppSelector} from "../../store/hooks";
import {env} from "../../common/constants/env";
import {getEventCache, setEventCache} from "../../store/reducers/eventCache";
import {getEventData} from "../../common/api/eventDataService";
import {eventBus} from "../../utils/EventBus";

const AppWrapper = (props: { children: ReactNode }) => {
  const dispatch = useAppDispatch();
  const event = useAppSelector(getEventCache);
  console.log("event1");
  console.log(event);
  useEffect(() => {
    initApp();
  }, []);

  const initApp = async () => {
    try {
      const eventData = await getEventData(env.EVENT_ID);
      dispatch(setEventCache(eventData));
    } catch (e) {
      eventBus.publish("showToast", e, "error");
    }
  };

  return <>{props.children}</>;
};

export { AppWrapper };
