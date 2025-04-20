import { FormProvider } from "@context";
import { Form } from "../components/Form.tsx";

export const IndividualForm = () => {
  return (
    <FormProvider>
      <Form />
    </FormProvider>
  )
}
