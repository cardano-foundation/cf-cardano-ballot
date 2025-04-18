import { FormProvider } from "../context/CompanyFormContext.tsx";
import { Form } from "../components/CompanyForm.tsx";

export const CompanyForm = () => {

  return (
    <FormProvider>
      <Form/>
    </FormProvider>
  )
}
