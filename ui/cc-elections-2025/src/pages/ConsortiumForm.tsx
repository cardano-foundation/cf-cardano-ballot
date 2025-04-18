import {FormProvider} from "../context/FormContext.tsx";
import { Form } from "../components/Form.tsx";

export const ConsortiumForm = () => {
  return (
    <FormProvider>
      <Form/>
    </FormProvider>
  )
}
