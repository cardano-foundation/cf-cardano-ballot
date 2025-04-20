import { ConsortiumFormProvider } from "@context";
import { Form } from "../components/ConsortiumForm";

export const ConsortiumForm = () => {
  return (
    <ConsortiumFormProvider>
      <Form />
    </ConsortiumFormProvider>
  )
}
