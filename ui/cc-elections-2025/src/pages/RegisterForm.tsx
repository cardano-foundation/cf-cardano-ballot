import { RegisterFormProvider} from "@context";
import { Form } from "@/components/RegisterForm.tsx";

export const RegisterForm = () => {
  return (
    <RegisterFormProvider>
      <Form />
    </RegisterFormProvider>
  )
}
