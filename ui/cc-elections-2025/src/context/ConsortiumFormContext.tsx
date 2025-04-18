import {createContext, useState, ReactNode} from 'react';
import type { ErrObject, FormContextType, ConsortumFormData } from '../types/formData'

const FormContext = createContext<FormContextType<ConsortumFormData> | null>(null);

type FormProviderProps = {
  children: ReactNode;
}

export const FormProvider = ({ children }: FormProviderProps) => {
  const title = [
    'Constitutional Committee application (Consortium)',
    'Cold Credential Generation',
    'Governance Action Rationale',
    'Consortium Members',
    'Consortium member information',
    'Consortium Information',
    'General Candidate Information',
    'Additional candidate information',
  ];

  const req = [
    ['termsAndCondition'],
    [],
    [],
    ['membersAmount'],
    [],
    ['name', 'email'],
    [],
    [],
  ]

  const [page, setPage] = useState<number>(0);

  const [data, setData] = useState<ConsortumFormData>({
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
    members: [
      {
        name: '',
        country: '',
        bio: '',
        socialX: '',
        socialLinkedin: '',
        socialDiscord: '',
        socialTelegram: '',
        socialOther: '',
        liveliness: '',
        conflictOfInterest: '',
        drepId: '',
        stakeId: '',
        xverification: '',
      }
    ],
  });

  const [error, setError] = useState<ErrObject>({});

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target) {
      const type = event.target.type;

      const name = event.target.name;
      const value = type === 'checkbox' ? event.target.checked : event.target.value;

      setData(prevData => ({...prevData, [name]: value}));
    }
  }

  const handleMemberChange = (event: React.ChangeEvent<HTMLInputElement>, index: number) => {
    if (event.target) {
      const type = event.target.type;

      const name = event.target.name;
      const value = type === 'checkbox' ? event.target.checked : event.target.value;

      setData(prevData => ({...prevData, members: prevData.members.map((dataItem, itemIndex) => {
        if(index === itemIndex) {
          return { ...dataItem, [name]: value }
        } else {
          return { ...dataItem };
        }
      })}));
    }
  }

  return (
    <FormContext.Provider value={{title, page, setPage, data, setData, handleChange, error, setError, req}}>
      {children}
    </FormContext.Provider>
  )
}

export default FormContext;
