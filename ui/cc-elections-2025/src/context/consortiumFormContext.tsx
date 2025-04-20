import {createContext, useState, ReactNode} from 'react';
import type { ErrObject, FormContextType, ConsortumFormData } from '../types/formData'
import {MemberBody} from "@models";

export const ConsortiumFormContext = createContext<FormContextType<ConsortumFormData> | null>(null);

type FormProviderProps = {
  children: ReactNode;
}

export const ConsortiumFormProvider = ({ children }: FormProviderProps) => {
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
    ['name', 'email', 'country', 'publicContact'],
    [],
    [],
  ]

  const [page, setPage] = useState<number>(0);

  const memberInit: MemberBody = {
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
  };

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
    members: [],
    membersAmount: 0,
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

      if (name === 'membersAmount') {
        if(data.members.length > Number(value)) {
          const newData = data.members.slice(0, Number(value) - data.members.length);
          setData(prevData => ({...prevData, members: newData }));
        }

        if (data.members.length < Number(value)) {
          const membersInit: MemberBody[] = Array.from({ length: Number(value) - data.members.length }).fill(memberInit);
          setData(prevData => ({...prevData, members: prevData.members.concat(membersInit) }));
        }
      }
    }
  }

  const handleMemberChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>, index: number) => {
    if (event.target) {
      const type = event.target.type;

      const name = event.target.name;
      const value = type === 'checkbox' && isEventInputElement(event) ? event.target.checked : event.target.value;

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
    <ConsortiumFormContext.Provider value={{title, page, setPage, data, setData, handleChange, handleMemberChange, error, setError, req}}>
      {children}
    </ConsortiumFormContext.Provider>
  )
}
