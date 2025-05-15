import { RegisterFormProvider} from "@context";
import { Form } from "@/components/RegisterForm.tsx";
import {useParams} from "react-router-dom";

export const EditCandicate = () => {
  const { id } = useParams();
  return (
    <RegisterFormProvider>
      <Form id={Number(id)} />
    </RegisterFormProvider>
  )
}
