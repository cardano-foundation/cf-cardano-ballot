import { FormProvider } from "../context/FormContext.tsx";
import { Form } from "../components/Form.tsx";

export const IndividualForm = () => {
  return (
    <FormProvider>
      <Form />
    </FormProvider>
  )
}
