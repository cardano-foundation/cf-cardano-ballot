import {createContext, useState, ReactNode} from 'react';
import type { ErrObject, FormContextType, IndividualFormData } from '../types/formData'

const FormContext = createContext<FormContextType<IndividualFormData> | null>(null);

type FormProviderProps = {
  children: ReactNode;
}

export const FormProvider = ({ children }: FormProviderProps) => {
  const title = [
    'Constitutional Committee application (Individuals)',
    'Cold Credential Generation',
    'Governance Action Rationale',
    'Personal Information',
    'General Candidate Information',
    'Additional candidate information',
    'Verifications',
  ];

  const req = [
    ['termsAndCondition'],
    [],
    [],
    ['name', 'email'],
    [],
    [],
    [],
  ]

  const [page, setPage] = useState<number>(0);

  const [data, setData] = useState<IndividualFormData>({
    termsAndCondition: false,
    coldCredential: '',
    rationale: '',
    name: '',
    email: '',
    country: '',
    socialX: '',
    socialLinkedin: '',
    socialDiscord: '',
    socialTelegram: '',
    socialOther: '',
    publicContact: '',
    about: '',
    bio: '',
    additionalInfo: '',
    videoPresentationLink: '',
    reasonToServe: '',
    governanceExperience: '',
    communicationStrategy: '',
    ecosystemContributions: '',
    legalExpertise: '',
    weeklyCommitmentHours: '',
    liveliness: '',
    conflictOfInterest: '',
    drepId: '',
    stakeId: '',
    xverification: '',
  });

  const [error, setError] = useState<ErrObject>({});

  const isEventInputElement = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>): event is React.ChangeEvent<HTMLInputElement> => {
    return (event as React.ChangeEvent<HTMLInputElement>).target.checked !== undefined;
  }

  const handleChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    if (event.target) {
      const type = event.target.type;

      const name = event.target.name;
      const value = type === 'checkbox' && isEventInputElement(event) ? event.target.checked : event.target.value;

      setData(prevData => ({...prevData, [name]: value}));
    }
  }

  return (
    <FormContext.Provider value={{title, page, setPage, data, setData, handleChange, error, setError, req}}>
      {children}
    </FormContext.Provider>
  )
}

export default FormContext;
