import { CompanyFormProvider } from "@context";
import { Form } from "../components/CompanyForm.tsx";

export const CompanyForm = () => {

  return (
    <CompanyFormProvider>
      <Form/>
    </CompanyFormProvider>
  )
}
