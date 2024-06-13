import { ReactNode, useEffect } from "react";
import { useAppDispatch } from "../../store/hooks";
import { env } from "../../common/constants/env";
import { setEventCache } from "../../store/reducers/eventCache";
import { getEventData } from "../../common/api/eventDataService";
import {eventBus, EventName} from "../../utils/EventBus";
import { eventDataFixture } from "../../__fixtures__/event";
import {ToastType} from "../common/Toast/Toast.types";

const AppWrapper = (props: { children: ReactNode }) => {
  const dispatch = useAppDispatch();
  useEffect(() => {
    initApp();
  }, []);

  const initApp = async () => {
    if (env.USING_FIXTURES) {
      dispatch(setEventCache(eventDataFixture));
    } else {
      try {
        const eventData = await getEventData(env.EVENT_ID);
        dispatch(setEventCache(eventData));
      } catch (e) {
        eventBus.publish(EventName.ShowToast, e, ToastType.Error);
      }
    }
  };

  return <>{props.children}</>;
};

export { AppWrapper };
